package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by ionolab-DP on 2018/12/21.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){

        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("User didn't log in");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
           //

            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("Requires administrator privileges");
        }
    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){

        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("User didn't log in");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //

            return iProductService.setSaleStatus(productId, status);
        }else{
            return ServerResponse.createByErrorMessage("Requires administrator privileges");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){

        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("User didn't log in");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //// TODO: 2018/12/21

            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("Requires administrator privileges");
        }
    }

//需要分页，使用pagehelper
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("User didn't log in");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){


            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("Requires administrator privileges");
        }
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("User didn't log in");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){


            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("Requires administrator privileges");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file") MultipartFile file, HttpServletRequest request){

        User user=(User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("User didn't log in");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){


            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap= Maps.newHashMap();
            fileMap.put("url",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess(fileMap);

        }else{
            return ServerResponse.createByErrorMessage("Requires administrator privileges");
        }
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "upload_file") MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        Map resultMap=Maps.newHashMap();

        User user=(User) request.getSession().getAttribute(Const.CURRENT_USER);
        if (user==null){
            resultMap.put("success",false);
            resultMap.put("msg","User didn't log in");
            return resultMap;
        }
        /*富文本对于返回值由自己的要求， simditor要求为：
        https://simditor.tower.im/docs/doc-config.html
        JSON response after uploading complete:
        {
          "success": true/false,
          "msg": "error message", # optional
          "file_path": "[real file path]"
        }
         */
        if (iUserService.checkAdminRole(user).isSuccess()){


            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);

            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","Upload file failed, network error");
            }
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            resultMap.put("success",true);
            resultMap.put("msg","Upload file success, what a progress!");
            resultMap.put("file_path",url);

            //响应头
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");

            return resultMap;

        }else{
            resultMap.put("success",false);
            resultMap.put("msg","Requires administrator privileges");
            return resultMap;
        }
    }


}
