package com.zmall.controller.backend;

import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.service.ICategoryService;
import com.zmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody //使得返回值使用jackson序列化
    public ServerResponse addCategory(HttpSession session, String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //增加分类逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }

    //Update category name by id
    @RequestMapping("set_category_name.do")
    @ResponseBody //使得返回值使用jackson序列化
    public ServerResponse setCategoryName(HttpSession session,Integer categoryId,String categoryName){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }

    //查询子分类
    @RequestMapping("get_category.do")
    @ResponseBody
    //@RequestParam()是如果传入了categoryId 参数的话就用这个参数，如果没传用default
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询节点category 信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点ID 和 递归子节点ID
            return iCategoryService.selectCategoryAndChildrenById(categoryId);

        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }


}
