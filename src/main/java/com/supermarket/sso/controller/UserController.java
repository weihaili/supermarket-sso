package com.supermarket.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.supermarket.common.utils.ExceptionUtil;
import com.supermarket.common.utils.KklResult;
import com.supermarket.pojo.TbUser;
import com.supermarket.sso.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;

	/**
	 * @URL: /user/check/{param}/{type}   
	 * @Title: checkDataEffectiveness   
	 * @Description: verify data validity and support jsonp invoke   
	 * @param: @param conten      
	 * @return: void      
	 * @throws   
	 */ 
	@RequestMapping(value="/user/check/{param}/{type}")
	@ResponseBody
	public Object checkDataEffectiveness(@PathVariable String param,@PathVariable Integer type,String callback) {
		KklResult result = null;
		
		if (StringUtils.isBlank(param)) {
			result= KklResult.build(400, "userName,phone,email can not be empty,please import");
		}
		if (type==null) {
			result= KklResult.build(400, "the type of verification cannot be empty,please check one");
		}
		if (type!=1 && type!=2 && type!=3) {
			result= KklResult.build(400, "the type of verification incorrect,please check");
		}
		
		if (null!=result) {
			if (null!=callback) {
				MappingJacksonValue jacksonValue= new MappingJacksonValue(result);
				jacksonValue.setJsonpFunction(callback);
				return jacksonValue;
			}else {
				return result;
			}
		}
		
		try {
			result = userService.checkData(param, type);
		} catch (Exception e) {
			e.printStackTrace();
			result= KklResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if (null!=callback) {
			MappingJacksonValue jacksonValue= new MappingJacksonValue(result);
			jacksonValue.setJsonpFunction(callback);
			return jacksonValue;
		}else {
			return result;
		}
	}
	
	/**   
	 * @Title: register   
	 * @Description: new user register
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public KklResult register(TbUser user) {
		System.out.println(user!=null?user.getUsername():"");
		KklResult result=null;
		try {
			result = userService.newUserRegister(user);
		} catch (Exception e) {
			e.printStackTrace();
			return KklResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return result;
	} 
	
	/**   
	 * @Title: login   
	 * @Description: user login   
	 * @param: @param username
	 * @param: @param password
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public KklResult login(String username,String password) {
		KklResult result = null;
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return KklResult.build(400, "username or password cannot be empty");
		}
		try {
			result=userService.login(username,password);
		} catch (Exception e) {
			e.printStackTrace();
			return KklResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		return result;
	}
	
	@RequestMapping(value="/user/token/{token}")
	@ResponseBody
	public Object obtainUserInfoByToken(@PathVariable String token,String callback) {
		KklResult result=null;
		if (StringUtils.isBlank(token)) {
			result=KklResult.build(400, "token is null ,please check");
		}
		try {
			result=userService.getUserInfoByToken(token);
			
		} catch (Exception e) {
			e.printStackTrace();
			result=KklResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if (!StringUtils.isBlank(callback)) {
			MappingJacksonValue jacksonValue=new MappingJacksonValue(result);
			jacksonValue.setJsonpFunction(callback);
			return jacksonValue;
		}
		return result;
	}
	
	/**   
	 * @Title: secureLogout   
	 * @Description: secure logout   
	 * @param: @return      
	 * @return: Object      
	 * @throws   
	 */ 
	@RequestMapping(value="/user/logout/{token}")
	@ResponseBody
	public Object secureLogout(@PathVariable String token,String callback) {
		KklResult result=null;
		if (StringUtils.isBlank(token)) {
			result=KklResult.build(400, "token is null,please check");
		}
		try {
			result=userService.logout(token);
		} catch (Exception e) {
			e.printStackTrace();
			result=KklResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if (!StringUtils.isBlank(callback)) {
			MappingJacksonValue jacksonValue=new MappingJacksonValue(result);
			jacksonValue.setJsonpFunction(callback);
			return jacksonValue;
		}
		return result;
	}
}
