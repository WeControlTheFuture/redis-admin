package com.mauersu.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mauersu.util.SingleServerInfo;

public class RedisClient implements IRedisClient {

	public RedisClient(SingleServerInfo serverInfo) {

	}

	@Override
	public String set(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setex(String key, int seconds, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keys(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String type(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rightPushAll(String key, String[] values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sadd(String key, String... values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zadd(String key, Set values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zadd(String key, String value, double score) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hmset(String key, Map values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Object> lrange(String key, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> srandmembers(String key, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set zrangebyscore(String key, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map hgetall(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void del(String... keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hdel(String key, List<String> hashKeys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void srem(String key, String... fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zrem(String key, String... fields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object lpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object rpop(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void lpush(String key, String... values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rpush(String key, String... values) {
		// TODO Auto-generated method stub
		
	}

}
