package com.imooc.o2o.web.shopadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;
	@Autowired
	private ShopCategoryService shopCategoryService;
	@Autowired
	private AreaService areaService;
	
	
	@RequestMapping(value="/getshopinitinfo",method=RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopInitInfo() {
		Map<String, Object> ModelMap = new HashMap<String, Object>();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		List<Area> areaList = new ArrayList<Area>();
		try {
			shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
			areaList = areaService.getAreaList();
			ModelMap.put("shopCategoryList", shopCategoryList);
			ModelMap.put("areaList", areaList);
			ModelMap.put("success", true);
		} catch (Exception e) {
			ModelMap.put("success", false);
			ModelMap.put("errMsg", e.getMessage());
		}
		return ModelMap;
	}

	
	@RequestMapping(value="/registershop",method=RequestMethod.POST)
	 @ResponseBody
 private Map<String,Object>registerShop(HttpServletRequest request){
		Map<String,Object> ModelMap= new HashMap<String,Object>();
		if(!CodeUtil.checkVerifyCode(request)) {
			ModelMap.put("success",false);
			ModelMap.put("errMsg","输入了错误的验证码");
			return ModelMap;
		}
	 //接收并转化相应的参数，包括店铺信息及图片信息
		String shopStr=HttpServletRequestUtil.getString(request,"shopStr");
		ObjectMapper mapper=new ObjectMapper();
		Shop shop=null;
		try{
			shop=mapper.readValue(shopStr,Shop.class);
			}catch(Exception e) {
				ModelMap.put("success",false);
				ModelMap.put("errMsg",e.getMessage());
				return ModelMap;
			}
		CommonsMultipartFile shopImg=null;
		CommonsMultipartResolver  commonsMultipartResolver=new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if(commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest =(MultipartHttpServletRequest) request;
			shopImg=(CommonsMultipartFile)multipartHttpServletRequest.getFile("shopImg");
		}else {
			ModelMap.put("success",false);
			ModelMap.put("errMsg","上传图片不能为空");
			return ModelMap;
		}
	 //注册店铺
		if(shop!=null&&shopImg!=null) {
			PersonInfo owner=new PersonInfo();
			//Session TODO
			owner.setUserId(1L);
			shop.setOwner(owner);
			ShopExecution se;
			try {
				se = shopService.addShop(shop,shopImg.getInputStream(),shopImg.getOriginalFilename());
				if(se.getState() ==ShopStateEnum.CHECK.getState()) {
					ModelMap.put("success",true);
				}else {
					ModelMap.put("success",false);
					ModelMap.put("errMsg",se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				ModelMap.put("success",false);
				ModelMap.put("errMsg",e.getMessage());
			} catch (IOException e) {
				ModelMap.put("success",false);
				ModelMap.put("errMsg",e.getMessage());
			}
			return ModelMap;
			}else {
				ModelMap.put("success",false);
				ModelMap.put("errMsg","请输入店铺信息");
				return ModelMap;
			}
	 
 }
//	private static void inputStreamToFile(InputStream ins,File file) {
//		FileOutputStream os= null;
//		try {
//			os =new FileOutputStream(file);
//			int bytesRead=0;
//			byte[] buffer=new byte[1024];
//			while((bytesRead=ins.read(buffer))!=-1) {
//				os.write(buffer, 0, bytesRead);
//			}
//			
//		}
//		catch(Exception e) {
//			throw new RuntimeException("调用inputStreamToFile产生异常:"+e.getMessage());
//		}finally {
//			try {
//				if(os!=null) {
//					os.close();
//				}
//				if(ins!=null) {
//					ins.close();
//				}
//			}catch(IOException e) {
//				throw new RuntimeException("调用inputStreamToFile关闭io异常:"+e.getMessage());
//			}
//		}
//	}
 
}
