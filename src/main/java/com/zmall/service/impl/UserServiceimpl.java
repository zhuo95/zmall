package com.zmall.service.impl;

import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.common.TokenCahce;
import com.zmall.dao.UserMapper;
import com.zmall.pojo.User;
import com.zmall.service.IUserService;
import com.zmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service("iUserService")
public class UserServiceimpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.creatByErrorMessage("Username doesn't exist");
        }
        //对比 MD5加密后的密码是否相同
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.creatByErrorMessage("Wrong password");
        }
        //把密码设置成空，不返回密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess("Login success",user);

    }

    public ServerResponse<String> register(User user){
//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if(resultCount > 0){
//            return ServerResponse.creatByErrorMessage("Username already exist");
//        }
        //代码复用
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USER_NAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount > 0){
//            return ServerResponse.creatByErrorMessage("Email has been registered");
//        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.creatByErrorMessage("Insert to db error");
        }
        return ServerResponse.creatBySuccessMessage("Sign up success");
    }

    //检查是否注册过被用过
    public ServerResponse<String> checkValid(String str, String type){
        if(StringUtils.isNotBlank(type)){
            //check
            if(Const.USER_NAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.creatByErrorMessage("Username already exist");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.creatByErrorMessage("Email has been registered");
                }
            }
        }else {
            return ServerResponse.creatByErrorMessage("Wrong argument");
        }
        return ServerResponse.creatBySuccessMessage("Check success");
    }

    //忘记密码后，找出question
    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USER_NAME);
        if(validResponse.isSuccess()){
            //表示用户不存在,看checkValid function可知
            return ServerResponse.creatByErrorMessage("Username doesn't exist");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.creatBySuccess(question);
        }
        return ServerResponse.creatByErrorMessage("Can't get the question, because it's blank");
    }

    //忘记密码后回答安全问题检查是否回答正确
    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //>0说明回答正确
            String forgetToken = UUID.randomUUID().toString();
            //本地缓存放入token
            TokenCahce.setKey(TokenCahce.TOKEN_PREFIX+username,forgetToken); //TokenCahce.TOKEN_PREFIX = token_
            return ServerResponse.creatBySuccess(forgetToken);
        }
        return ServerResponse.creatBySuccessMessage("Wrong answer for the question");
    }

    //忘记密码时重置密码
    public ServerResponse<String> forgetRestPassword(String username,String newPassword,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.creatByErrorMessage("Token 需要传递");
        }
        //检查用户是否存在
        ServerResponse validResponse = this.checkValid(username,Const.USER_NAME);
        if(validResponse.isSuccess()){
            //表示用户不存在,看checkValid function可知
            return ServerResponse.creatByErrorMessage("Username doesn't exist");
        }
        String token = TokenCahce.getKey(TokenCahce.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.creatByErrorMessage("Token invalid or expired");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password =  MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServerResponse.creatBySuccessMessage("Change password success");
            }
        }else{
            //token错误，请重新获取token
            return ServerResponse.creatByErrorMessage("Wrong token");
        }
        return ServerResponse.creatByErrorMessage("Change password fail");
    }

    //登录状态下重置密码
    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,User user){
        //防止横向越权，要校验这个用户的旧密码，一定要指定这个用户，因为我们要查询一个count，如果不指定id，那么结果就是true了(count>0)
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount==0){
            return ServerResponse.creatBySuccessMessage("Old password is wrong");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        //updateByPrimaryKeySelective是选择性更新 为null的不更新
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.creatBySuccessMessage("Password update success");
        }
        return ServerResponse.creatBySuccessMessage("Password update fail");
    }

    //更新用户信息

    public ServerResponse<User> updateInformation(User user){
        //username 不能更新。 email 要校验新email是否存在，且如果存在的话不能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.creatByErrorMessage("This email has benn used");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        //选择性更新 只更新上面五个字段，因为updateuser里面是空的字段不会更新到数据库
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServerResponse.creatBySuccess("Update personal info success",updateUser);
        }
        return ServerResponse.creatByErrorMessage("Update personal info fail");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.creatByErrorMessage("Can not find this user");
        }
        //把密码置空返给前端，安全
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess(user);
    }

}
