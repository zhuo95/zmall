package com.zmall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Order;
import com.zmall.pojo.User;
import com.zmall.service.IOrderService;
import com.zmall.service.IUserService;
import com.zmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> oderList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                             @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iOrderService.manageList(pageNum,pageSize);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iOrderService.manageDetail(orderNo);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,
                                               @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }

    //发货
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGood(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"User doesn't login");
        }
        //检查是否为管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iOrderService.manageSendGood(orderNo);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator,无权限管理");
        }
    }
}
