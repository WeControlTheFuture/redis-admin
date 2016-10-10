package com.mauersu.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.mauersu.util.SingleServerInfo;

import redis.clients.jedis.ObjectJedis;
import redis.clients.jedis.ObjectJedisPool;

public class RedisClient implements IRedisClient {

	private ObjectJedisPool objectJedisPool;

	public RedisClient(SingleServerInfo serverInfo) {
		this.objectJedisPool = new ObjectJedisPool(new GenericObjectPoolConfig(), serverInfo.getHost(),
				serverInfo.getPort(), 20000, serverInfo.getPassword());
	}

	private ObjectJedis getConection() {
		return objectJedisPool.getResource();
	}

	@Override
	public String set(String key, Object value) {
		return getConection().set(key, value);
	}

	@Override
	public String setex(String key, int seconds, Object value) {
		return getConection().setex(key, seconds, value);
	}

	@Override
	public Object get(String key) {
		return getConection().get(key);
	}

	@Override
	public boolean exists(String key) {
		return getConection().exists(key);
	}

	@Override
	public boolean hexists(String key, String field) {
		return false;
	}

	@Override
	public Set<String> keys(String pattern) {
		return getConection().keys(pattern);
	}

	@Override
	public String type(String key) {
		return getConection().type(key);
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
