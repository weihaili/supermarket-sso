package com.supermarket.sso.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.supermarket.sso.dao.JedisClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClientSingle implements JedisClient {

	@Autowired
	private JedisPool jedisPool;
	
	@Override
	public long del(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.del(key);
		jedis.close();
		return result;
	}

	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String string = jedis.get(key);
		jedis.close();
		return string;
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String string = jedis.set(key, value);
		jedis.close();
		return string;
	}

	@Override
	public String hget(String hkey, String field) {
		Jedis jedis = jedisPool.getResource();
		String string = jedis.hget(hkey, field);
		jedis.close();
		return string;
	}

	@Override
	public long hset(String hkey, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		Long long1 = jedis.hset(hkey, field, value);
		jedis.close();
		return long1;
	}

	@Override
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long long1 = jedis.incr(key);
		jedis.close();
		return long1;
	}

	@Override
	public long decr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long long1 = jedis.decr(key);
		jedis.close();
		return long1;
	}

	@Override
	public long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		Long long1 = jedis.expire(key, seconds);
		jedis.close();
		return long1;
	}

	@Override
	public long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long long1 = jedis.ttl(key);
		jedis.close();
		return long1;
	}

	@Override
	public long hdel(String hkey, String field) {
		Jedis jedis = jedisPool.getResource();
		Long result = jedis.hdel(hkey, field);
		jedis.close();
		return result;
	}

	
}
