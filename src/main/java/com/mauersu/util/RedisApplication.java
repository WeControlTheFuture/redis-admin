package com.mauersu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.mauersu.exception.ConcurrentException;
import com.mauersu.redis.IRedisClient;
import com.mauersu.redis.RedisClient;
import com.mauersu.redis.RedisClusterClient;
import com.mauersu.util.ztree.RedisZtreeUtil;

public abstract class RedisApplication implements Constant {

	private static Log log = LogFactory.getLog(RedisApplication.class);

	public static volatile RefreshModeEnum refreshMode = RefreshModeEnum.manually;
	public static volatile ShowTypeEnum showType = ShowTypeEnum.show;
	public static String BASE_PATH = "/redis-admin";

	protected volatile Semaphore limitUpdate = new Semaphore(1);
	protected static final int LIMIT_TIME = 3; // unit : second

	public static ThreadLocal<Integer> redisConnectionDbIndex = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};

	protected static ThreadLocal<Semaphore> updatePermition = new ThreadLocal<Semaphore>() {
		@Override
		protected Semaphore initialValue() {
			return null;
		}
	};

	protected static ThreadLocal<Long> startTime = new ThreadLocal<Long>() {
		protected Long initialValue() {
			return 0l;
		}
	};

	private Semaphore getSempahore() {
		startTime.set(System.currentTimeMillis());
		updatePermition.set(limitUpdate);
		return updatePermition.get();

	}

	protected boolean getUpdatePermition() {
		Semaphore sempahore = getSempahore();
		boolean permit = sempahore.tryAcquire(1);
		return permit;
	}

	protected void finishUpdate() {
		Semaphore semaphore = updatePermition.get();
		if (semaphore == null) {
			throw new ConcurrentException("semaphore==null");
		}
		final Semaphore fsemaphore = semaphore;
		new Thread(new Runnable() {

			Semaphore RSemaphore;
			{
				RSemaphore = fsemaphore;
			}

			@Override
			public void run() {
				long start = startTime.get();
				long now = System.currentTimeMillis();
				try {
					long needWait = start + LIMIT_TIME * 1000 - now;
					if (needWait > 0L) {
						Thread.sleep(needWait);
					}
				} catch (InterruptedException e) {
					log.warn("finishUpdate 's release semaphore thread had be interrupted");
				}
				RSemaphore.release(1);
				logCurrentTime("semaphore.release(1) finish");
			}
		}).start();
	}

	// this idea is not good
	/*
	 * protected void runUpdateLimit() { new Thread(new Runnable () {
	 * 
	 * @Override public void run() { while(true) { try { Thread.sleep(LIMIT_TIME
	 * * 1000); limitUpdate = new Semaphore(1); } catch(InterruptedException e)
	 * { log.warn("runUpdateLimit 's new semaphore thread had be interrupted");
	 * break; } } } }).start(); }
	 */

	protected void createRedisClusterConnection(ClusterServerInfo clusterServerInfo) {
		IRedisClient redisClient = new RedisClusterClient(clusterServerInfo.getServers());
		RedisApplication.redisTemplatesMap.put(clusterServerInfo.getName(), redisClient);
		RedisApplication.redisServerCache.add(clusterServerInfo);
		RedisZtreeUtil.initRedisNavigateZtree(clusterServerInfo.getName());
	}

	protected void createRedisConnection(SingleServerInfo serverInfo) {
		// JedisConnectionFactory connectionFactory = new
		// JedisConnectionFactory();
		// connectionFactory.setHostName(serverInfo.getHost());
		// connectionFactory.setPort(serverInfo.getPort());
		// if (!StringUtils.isEmpty(serverInfo.getPassword()))
		// connectionFactory.setPassword(serverInfo.getPassword());
		// connectionFactory.afterPropertiesSet();
		// RedisTemplate redisTemplate = new MyStringRedisTemplate();
		// redisTemplate.setConnectionFactory(connectionFactory);
		// redisTemplate.afterPropertiesSet();

		// IRedisClient redisClient = new
		// RedisApplication.redisTemplatesMap.put(serverInfo.getName(),
		// redisTemplate);

		RedisApplication.redisServerCache.add(serverInfo);
		IRedisClient redisClient = new RedisClient(serverInfo);
		RedisApplication.redisTemplatesMap.put(serverInfo.getName(), redisClient);
//		initRedisKeysCache(redisClient, serverInfo.getName());

		RedisZtreeUtil.initRedisNavigateZtree(serverInfo.getName());
	}
//
//	private void initRedisKeysCache(IRedisClient redisClient, String name) {
//		for (int i = 0; i <= REDIS_DEFAULT_DB_SIZE; i++) {
//			initRedisKeysCache(redisClient, name, i);
//		}
//	}
//
//	protected void initRedisKeysCache(IRedisClient redisClient, String serverName, int dbIndex) {
//		// RedisConnection connection =
//		// RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
//		// connection.select(dbIndex);
//		Set<String> keysSet = redisClient.keys("*");
//		// connection.close();
//		List<RKey> tempList = new ArrayList<RKey>();
//		ConvertUtil.convertByteToString(redisClient, keysSet, tempList);
////		Collections.sort(tempList);
//		CopyOnWriteArrayList<RKey> redisKeysList = new CopyOnWriteArrayList<RKey>(tempList);
//		if (redisKeysList.size() > 0) {
//			redisKeysListMap.put(serverName + DEFAULT_SEPARATOR + dbIndex, redisKeysList);
//		}
//	}

	protected static void logCurrentTime(String code) {
		log.debug("       code:" + code + "        当前时间:" + System.currentTimeMillis());
	}
}
