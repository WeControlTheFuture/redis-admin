package com.mauersu.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRedisClient {

	/**
	 * 设置一个key的value值 如果key已经存在了，它会被覆盖，而不管它是什么类型。
	 * 
	 * @param key
	 *            cache中存储数据的key
	 * @param value
	 *            cache中存储数据的value，javabean建议做成json串
	 * @return 总是OK，因为SET不会失败。 @
	 */
	String set(String key, Object value);

	/**
	 * 设置一个key的value值,设置key对应字符串value,并设置有效期。
	 * 
	 * @param key
	 * @param seconds
	 *            秒 有效期
	 * @param value
	 * @return @
	 */
	String setex(String key, int seconds, Object value);

	/**
	 * 获取key的值 如果key不存在，返回null
	 * 
	 * @param key
	 *            cache中存储数据的key
	 * @return cache中key为key的value @
	 */
	Object get(String key);

	/**
	 * 返回key是否存在
	 * 
	 * @param key
	 *            cache中存储数据的key
	 * @return true存在 @
	 */
	boolean exists(String key);

	public boolean hexists(String key, String field);

	public Set<String> keys(final String pattern);

	public String type(final String key);

	public void rightPushAll(String key, String[] values);

	public void sadd(String key, String... values);

	public void zadd(String key, Set values);

	public void zadd(String key, String value, double score);

	public void hmset(String key, Map values);

	public List<Object> lrange(String key, int start, int end);

	public List<Object> srandmembers(String key, int count);

	public Set zrangebyscore(String key, int start, int end);

	public Map hgetall(String key);

	public void del(String... keys);

	public void hdel(String key, List<String> hashKeys);

	public void hset(String key, String field, String value);

	public void srem(String key, String... fields);

	public void zrem(String key, String... fields);

	public Object lpop(String key);

	public Object rpop(String key);

	public void lpush(String key, String... values);
	
	public void rpush(String key, String... values);
}
