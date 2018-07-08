package com.supermarket.sso.dao;


public interface JedisClient {
	
	
	/***********operate string*******************/
	String get(String key);
	
	String set(String key,String value);
	
	/***********operate hash*******************/
	String hget(String hkey,String field);
	
	long hset(String hkey,String field,String value);
	
	
	/**
	 * self add (increased)
	 * @param key
	 * @return
	 */
	long incr(String key);
	
	/**self-reduction 
	 * @param key
	 * @return
	 */
	long decr(String key);
	
	/**
	 * set key expire limit(unit second)
	 * @param key
	 * @param expired
	 * @return
	 */
	long expire(String key,int seconds);
	
	/**
	 * get distance expired (unit second)
	 * @param key
	 * @return
	 */
	long ttl(String key);
	
	/**delete specified key
	 * @param key
	 * @return
	 */
	long del(String key); 
	
	long hdel(String hkey,String field);
}
