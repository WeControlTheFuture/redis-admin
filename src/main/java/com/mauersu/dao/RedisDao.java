package com.mauersu.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.mauersu.redis.IRedisClient;
import com.mauersu.util.RValue;
import com.mauersu.util.RedisApplication;

@Service
public class RedisDao extends RedisApplication {

	@Autowired
	RedisTemplateFactory redisTemplateFactory;

	// --- SET
	public void addSTRING(String serverName, int dbIndex, String key, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.set(key, value);
	}

	public void addLIST(String serverName, int dbIndex, String key, String[] values) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.rightPushAll(key, values);
	}

	public void addSET(String serverName, int dbIndex, String key, String[] values) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.sadd(key, values);
	}

	public void addZSET(String serverName, int dbIndex, String key, double[] scores, String[] members) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		Set<TypedTuple<Object>> zset = new HashSet<TypedTuple<Object>>();
		for (int i = 0; i < members.length; i++) {
			final Object ob = members[i];
			final double sc = scores[i];
			zset.add(new TypedTuple() {
				private Object v;
				private double score;
				{
					v = ob;
					score = sc;
				}

				@Override
				public int compareTo(Object o) {
					if (o == null)
						return 1;
					if (o instanceof TypedTuple) {
						TypedTuple tto = (TypedTuple) o;
						return this.getScore() - tto.getScore() >= 0 ? 1 : -1;
					}
					return 1;
				}

				@Override
				public Object getValue() {
					return v;
				}

				@Override
				public Double getScore() {
					return score;
				}
			});
		}
		redisTemplate.zadd(key, zset);
	}

	public void addHASH(String serverName, int dbIndex, String key, String[] fields, String[] values) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		Map<String, String> hashmap = new HashMap<String, String>();

		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			String value = values[i];
			hashmap.put(field, value);
		}
		redisTemplate.hmset(key, hashmap);
	}

	// --- GET
	public Object getSTRING(String serverName, int dbIndex, String key) {
		IRedisClient redisClient = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		final Object value = redisClient.get(key);
		List list = new ArrayList();
		list.add(new RValue(value));
		return list;
	}

	public Object getLIST(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		List<Object> values = redisTemplate.lrange(key, 0, 1000);
		return RValue.creatListValue(values);
	}

	public Object getSET(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		List<Object> values = redisTemplate.srandmembers(key, 1000);
		// Set<Object> values = redisTemplate.opsForSet().members(key);
		return RValue.creatSetValue(new HashSet(values));
	}

	public Object getZSET(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		Set<TypedTuple<Object>> values = redisTemplate.zrangebyscore(key, 0, 1000);
		return RValue.creatZSetValue(values);
	}

	public Object getHASH(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		Map<Object, Object> values = redisTemplate.hgetall(key);
		return RValue.creatHashValue(values);
	}

	// --- delete
	public void delRedisKeys(String serverName, int dbIndex, String deleteKeys) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		String[] keys = deleteKeys.split(",");
		redisTemplate.del(keys);
	}

	public void delRedisHashField(String serverName, int dbIndex, String key, String field) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		List<String> hashKeys = new ArrayList<String>();
		hashKeys.add(field);
		redisTemplate.hdel(key, hashKeys);
	}

	public void updateHashField(String serverName, int dbIndex, String key, String field, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		String hashKey = field;
		redisTemplate.hset(key, hashKey, value);
	}

	public void delSetValue(String serverName, int dbIndex, String key, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.srem(key, value);
	}

	public void updateSetValue(String serverName, int dbIndex, String key, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.sadd(key, value);
	}

	public void delZSetValue(String serverName, int dbIndex, String key, String member) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		String value = member;
		redisTemplate.zrem(key, value);
	}

	public void updateZSetValue(String serverName, int dbIndex, String key, double score, String member) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		String value = member;
		redisTemplate.zadd(key, value, score);
	}

	public void ldelListValue(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.lpop(key);
	}

	public void rdelListValue(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.rpop(key);
	}

	public void lupdateListValue(String serverName, int dbIndex, String key, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.lpush(key, value);
	}

	public void rupdateListValue(String serverName, int dbIndex, String key, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.rpush(key, value);
	}

	public void delRedisValue(String serverName, int dbIndex, String key) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.del(key);
	}

	public void updateValue(String serverName, int dbIndex, String key, String value) {
		IRedisClient redisTemplate = redisTemplateFactory.getRedisTemplate(serverName);
		redisConnectionDbIndex.set(dbIndex);
		redisTemplate.set(key, value);
	}
}
