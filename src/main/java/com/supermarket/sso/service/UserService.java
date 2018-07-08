package com.supermarket.sso.service;

import com.supermarket.common.utils.KklResult;
import com.supermarket.pojo.TbUser;

public interface UserService {
	
	/**   
	 * @Title: checkData   
	 * @Description: check user import it`s usable or not
	 * @param: @param content
	 * @param: @param type
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	KklResult checkData(String content,Integer type);

	/**   
	 * @Title: newUserRegister   
	 * @Description: new user register   
	 * @param: @param user
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	KklResult newUserRegister(TbUser user);

	/**   
	 * @Title: login   
	 * @Description: user login   
	 * @param: @param username
	 * @param: @param password
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	KklResult login(String username, String password);

	/**   
	 * @Title: getUserInfoByToken   
	 * @Description: query user information by token 
	 * @param: @param token
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	KklResult getUserInfoByToken(String token);

	/**   
	 * @Title: logout   
	 * @Description: user secure logout
	 * @param: @param token
	 * @param: @return      
	 * @return: KklResult      
	 * @throws   
	 */ 
	KklResult logout(String token);

}
