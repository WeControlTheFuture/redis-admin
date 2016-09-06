package com.mauersu.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.data.redis.core.RedisTemplate;

import com.mauersu.redis.IRedisClient;
import com.mauersu.util.ztree.ZNode;

@SuppressWarnings("rawtypes")
public interface Constant {

	public static final Map<String, IRedisClient> redisTemplatesMap = new HashMap<String, IRedisClient>();
	public static final Map<String, CopyOnWriteArrayList<String>> redisKeysListMap = new HashMap<String, CopyOnWriteArrayList<String>>();
	public static final Map<RKey, Object> redisVMCache = new ConcurrentHashMap<RKey, Object>();
	public static final CopyOnWriteArrayList<ZNode> redisNavigateZtree = new CopyOnWriteArrayList<ZNode>();
	public static final CopyOnWriteArrayList<ServerInfo> redisServerCache = new CopyOnWriteArrayList<ServerInfo>();

	public static final int DEFAULT_ITEMS_PER_PAGE = 10;
	public static final String DEFAULT_REDISKEY_SEPARATOR = ":";
	public static final int REDIS_DEFAULT_DB_SIZE = 15;
	public static final String DEFAULT_SEPARATOR = "_";
	public static final String UTF_8 = "utf-8";

	public static final String REDIS_IS_CLUSTER = "redis.is.cluster";
	public static final String REDISCLUSTERPROPERTIES_SERVER_NUM_KEY = "redis.cluster.server.num";
	public static final String REDISCLUSTERPROPERTIES_LANGUAGE_KEY = "redis.cluster.language";

	public static final String REDISCLUSTERPROPERTIES_HOST_AND_PORT_PROFIXKEY = "redis.cluster.host.and.port";
	public static final String REDISCLUSTERPROPERTIES_NAME_PROFIXKEY = "redis.cluster.name.";
	public static final String REDISCLUSTERPROPERTIES_PASSWORD_PROFIXKEY = "redis.cluster.password.";
	/** redis properties key **/
	public static final String REDISPROPERTIES_SERVER_NUM_KEY = "redis.server.num";
	public static final String REDISPROPERTIES_LANGUAGE_KEY = "redis.language";

	public static final String REDISPROPERTIES_HOST_PROFIXKEY = "redis.host.";
	public static final String REDISPROPERTIES_NAME_PROFIXKEY = "redis.name.";
	public static final String REDISPROPERTIES_PORT_PROFIXKEY = "redis.port.";
	public static final String REDISPROPERTIES_PASSWORD_PROFIXKEY = "redis.password.";

	/** default **/
	// public static final String DEFAULT_REDISSERVERNAME = "default";
	public static final int DEFAULT_DBINDEX = 0;

	/** query key **/
	public static final String MIDDLE_KEY = "middle";
	public static final String HEAD_KEY = "head";
	public static final String TAIL_KEY = "tail";
	public static final String EMPTY_STRING = "";

	/** operator for log **/
	public static final String GETKV = "GETKV";
}
