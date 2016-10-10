package com.mauersu.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.ObjectJedisCluster;
import redis.clients.jedis.exceptions.JedisClusterException;

public class RedisClusterClient implements IRedisClient {
	private GenericObjectPoolConfig config;
	private List<HostAndPort> hosts;
	private ObjectJedisCluster objectJedisCluster;

	public RedisClusterClient(GenericObjectPoolConfig config, String hostString) {
		this.config = config;
		this.hosts = new ArrayList<HostAndPort>();
		for (String str : hostString.split(",", -1)) {
			String[] ipAndPort = str.split(":");
			this.hosts.add(new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
		}
		getCluster();
	}

	public RedisClusterClient(List<HostAndPort> servers) {
		this.hosts = servers;
		this.config = new GenericObjectPoolConfig();
		getCluster();
	}

	private void getCluster() {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		try {
			for (HostAndPort address : hosts) {
				jedisClusterNodes.add(address);
			}
			if (config.getMaxWaitMillis() < 20000)
				config.setMaxWaitMillis(20000);
			objectJedisCluster = new ObjectJedisCluster(jedisClusterNodes, 20000, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean canConnection() {
		try {
			objectJedisCluster.get("ok");
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String set(String key, Object value) {
		try {
			return objectJedisCluster.set(key, value);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return set(key, value);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public String setex(String key, int seconds, Object value) {
		try {
			return objectJedisCluster.setex(key, seconds, value);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return setex(key, seconds, value);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Long setnx(String key, Object value) {
		try {
			return objectJedisCluster.setnx(key, value);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return setnx(key, value);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Long hsetnx(String key, String field, Object value) {
		try {
			return objectJedisCluster.hsetnx(key, field, value);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return hsetnx(key, field, value);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Object get(String key) {
		try {
			return objectJedisCluster.get(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return get(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Object hget(String key, String field) {
		try {
			return objectJedisCluster.hget(key, field);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return hget(key, field);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public <T> List<T> get(String key, Class<T> cls) {
		try {
			return objectJedisCluster.get(key, cls);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return get(key, cls);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public boolean exists(String key) {
		try {
			return objectJedisCluster.exists(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return exists(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public boolean hexists(String key, String field) {
		try {
			return objectJedisCluster.hexists(key, field);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return hexists(key, field);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Object invalidate(String key) {
		try {
			return objectJedisCluster.invalidate(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return invalidate(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Object hinvalidate(String key, String field) {
		try {
			return objectJedisCluster.hinvalidate(key, field);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return hinvalidate(key, field);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Object del(String key) {
		try {
			return objectJedisCluster.del(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return del(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Set<String> hkeys(String key) {
		try {
			return objectJedisCluster.hkeys(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return hkeys(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Long hlen(String key) {
		try {
			return objectJedisCluster.hlen(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return hlen(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

	public Set<String> keys(final String pattern) {
		try {
			return objectJedisCluster.keys(pattern);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return keys(pattern);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

//	public Long sadd(String key, String... value) {
//		try {
//			return objectJedisCluster.sadd(key, value);
//		} catch (JedisClusterException jcException) {
//			getCluster();
//			if (canConnection()) {
//				return sadd(key, value);
//			}
//			// log.error(jcException.getMessage(), jcException);
//			throw jcException;
//		}
//	}

	public Set<String> smembers(final String key) {
		try {
			return objectJedisCluster.smembers(key);
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return smembers(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
	}

//	public Long srem(final String key, final String... value) {
//		try {
//			return objectJedisCluster.srem(key, value);
//		} catch (JedisClusterException jcException) {
//			getCluster();
//			if (canConnection()) {
//				return srem(key, value);
//			}
//			// log.error(jcException.getMessage(), jcException);
//			throw jcException;
//		}
//	}

	public String type(final String key) {
		try {
			return objectJedisCluster.type(key.getBytes());
		} catch (JedisClusterException jcException) {
			getCluster();
			if (canConnection()) {
				return type(key);
			}
			// log.error(jcException.getMessage(), jcException);
			throw jcException;
		}
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
