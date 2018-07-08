package com.supermarket.sso.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.supermarket.sso.dao.JedisClient;

import redis.clients.jedis.JedisCluster;

public class JedisClinetCluster implements JedisClient {
	
	@Autowired
	private JedisCluster cluster;

	@Override
	public String get(String key) {
		
		return cluster.get(key);
	}

	@Override
	public String set(String key, String value) {
		return cluster.set(key, value);
	}

	@Override
	public String hget(String hkey, String field) {
		return cluster.hget(hkey, field);
	}

	@Override
	public long hset(String hkey, String field, String value) {
		return cluster.hset(hkey, field, value);
	}

	@Override
	public long incr(String key) {
		return cluster.incr(key);
	}

	@Override
	public long decr(String key) {
		return cluster.decr(key);
	}

	@Override
	public long expire(String key, int seconds) {
		return cluster.expire(key, seconds);
	}

	@Override
	public long ttl(String key) {
		return cluster.ttl(key);
	}

	@Override
	public long del(String key) {
		Long result = cluster.del(key);
		return result;
	}

	@Override
	public long hdel(String hkey, String field) {
		Long result = cluster.hdel(hkey, field);
		return result;
	}

}
