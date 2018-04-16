package com.zmall.controller.backend;


import com.google.common.collect.Maps;
import com.zmall.common.Const;
import com.zmall.common.ResponseCode;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Product;
import com.zmall.pojo.User;
import com.zmall.service.IFileService;
import com.zmall.service.IProductService;
import com.zmall.service.IUserService;
import com.zmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "User needs to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //增减产品逻辑
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return ServerResponse.creatByErrorMessage("You are not administrator");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "User needs to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //设置产品上下架状态逻辑
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.creatByErrorMessage("You are not administrator");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "User needs to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //获取详细信息
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.creatByErrorMessage("You are not administrator");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "User needs to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //动态分页
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.creatByErrorMessage("You are not administrator");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "User needs to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //搜索逻辑
            return iProductService.productSearch(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.creatByErrorMessage("You are not administrator");
        }
    }

    //上传给ftp
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request){
        //判断权限
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "User needs to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            //给前端url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.creatBySuccess(fileMap);
        }else {
            return ServerResponse.creatByErrorMessage("You are not administrator");
        }
    }


    //富文本上传
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgupload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        //判断权限
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success",false);
            resultMap.put("msg","User needs to login");
            return resultMap;
        }
        //富文本中对于返回值有要求 使用simditor的要求
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","Upload fail");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","Success");
            resultMap.put("file_path",url);
            //和前端的约定,要这么设置
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","You are not administrator");
            return resultMap;
        }
    }

}
