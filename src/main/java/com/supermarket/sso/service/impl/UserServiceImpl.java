package com.supermarket.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.supermarket.common.utils.CookieUtils;
import com.supermarket.common.utils.JsonUtils;
import com.supermarket.common.utils.KklResult;
import com.supermarket.mapper.TbUserMapper;
import com.supermarket.pojo.TbUser;
import com.supermarket.pojo.TbUserExample;
import com.supermarket.pojo.TbUserExample.Criteria;
import com.supermarket.sso.dao.JedisClient;
import com.supermarket.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	@Qualifier("jedisClinetSingle")
	private JedisClient jedisClient;
	
	@Value("${REDIS_USER_SESSION_KEY}")
	private String USER_SESSION_KEY;
	
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SESSION_EFFECTIVE_DURATION;

	/**
	 * <p>
	 * Title: checkData
	 * </p>
	 * <p>
	 * Description: it`s usable or not for user importing data
	 * 
	 * @param content
	 * @param type:
	 *            1 representative userName, 2 representative phone,3.representative
	 *            email
	 * @return
	 * @see com.supermarket.sso.service.UserService#checkData(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public KklResult checkData(String content, Integer type) {
		if (type != null) {
			TbUserExample example = new TbUserExample();
			Criteria criteria = example.createCriteria();
			switch (type) {
			case 1:
				criteria.andUsernameEqualTo(content);
				break;
			case 2:
				criteria.andPhoneEqualTo(content);
				break;
			case 3:
				criteria.andEmailEqualTo(content);
				break;

			default:
				break;
			}
			List<TbUser> list = userMapper.selectByExample(example);
			if (list==null || list.size()==0) {
				return KklResult.ok(true);
			}
		}
		return KklResult.ok(false);
	}

	/**   
	 * <p>Title: newUserRegister</p>   
	 * <p>Description: new User register  
	 * @param user
	 * @return   
	 * @see com.supermarket.sso.service.UserService#newUserRegister(com.supermarket.pojo.TbUser)   
	 */ 
	@Override
	public KklResult newUserRegister(TbUser user) {
		Date date = new Date();
		user.setCreated(date);
		user.setUpdated(date);
		//md5 encryption
		KklResult checkDataUsername = checkData(user.getUsername(), 1);
		if (!(boolean) checkDataUsername.getData()) {
			return KklResult.build(400, "username is already occupied,please chose other username");
		}
		KklResult checkDataPhone = checkData(user.getPhone(),2);
		if (!(boolean) checkDataPhone.getData()) {
			return KklResult.build(400, "phone number is already register,please retrieve or use other phone register");
		}
		/*KklResult checkDataEmail = checkData(user.getEmail(), 3);
		if (!(boolean) checkDataEmail.getData()) {
			return KklResult.build(400, "email address is already register,please retrieve or use other effective email address");
		}*/
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		try {
			userMapper.insertSelective(user);
		} catch (Exception e) {
			e.printStackTrace();
			return KklResult.build(500, "register fail server is busy please try again later");
		}
		return KklResult.ok();
	}

	/**   
	 * <p>Title: login</p>   
	 * <p>Description: user login</p>   
	 * @param username
	 * @param password
	 * @return   
	 * @see com.supermarket.sso.service.UserService#login(java.lang.String, java.lang.String)   
	 */ 
	@Override
	public KklResult login(String username, String password,HttpServletRequest request,HttpServletResponse response) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		if (list==null || list.size()==0) {
			return KklResult.build(400, "username does not exist");
		}
		TbUser user = list.get(0);
		String md5EncryptionStr=DigestUtils.md5DigestAsHex(password.getBytes());
		if (!md5EncryptionStr.equals(user.getPassword())) {
			return KklResult.build(400, "username and password do not match");
		}
		//generate token by UUID
		String token = UUID.randomUUID().toString();
		//to protect user sensitive data,clear password information before serialization
		user.setPassword(null);
		jedisClient.set(USER_SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
		//set session expire time
		jedisClient.expire(USER_SESSION_KEY+":"+token, SESSION_EFFECTIVE_DURATION);
		//add write cookie logic
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);
		return KklResult.ok(token);
	}

	/**   
	 * <p>Title: getUserInfoByToken</p>   
	 * <p>Description: query user information by token</p>   
	 * @param token
	 * @return   
	 * @see com.supermarket.sso.service.UserService#getUserInfoByToken(java.lang.String)   
	 */ 
	@Override
	public KklResult getUserInfoByToken(String token) {
		if (StringUtils.isBlank(token)) {
			return KklResult.build(400, "token is null,please check");
		}
		String redisDataStr = jedisClient.get(USER_SESSION_KEY+":"+token);
		if (redisDataStr==null || StringUtils.isBlank(redisDataStr)) {
			return KklResult.build(400, "invalid token,please check");
		}
		jedisClient.expire(USER_SESSION_KEY+":"+token, SESSION_EFFECTIVE_DURATION);
		return KklResult.ok(JsonUtils.jsonToPojo(redisDataStr, TbUser.class));
	}

	/**   
	 * <p>Title: logout</p>   
	 * <p>Description: user secure logout</p>   
	 * @param token
	 * @return   
	 * @see com.supermarket.sso.service.UserService#logout(java.lang.String)   
	 */ 
	@Override
	public KklResult logout(String token) {
		if (StringUtils.isBlank(token)) {
			return KklResult.build(400, "token is empty,please check");
		}
		try {
			jedisClient.del(USER_SESSION_KEY+":"+token);
		} catch (Exception e) {
			return KklResult.build(400, "token already expired");
		}
		return KklResult.ok("");
	} 
	
	
	
	

}
