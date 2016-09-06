package com.mauersu.util;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.mauersu.exception.RedisInitException;

import jetbrick.util.StringUtils;

@Service
public class InitContext extends RedisApplication implements Constant {

	private static Log log = LogFactory.getLog(InitContext.class);

	@Autowired
	private Environment env;

	@PostConstruct
	public void initRedisServers() {
		String currentServerName = "";
		try {
			// init redis standalone
			int serverNum = Integer.parseInt(env.getProperty(REDISPROPERTIES_SERVER_NUM_KEY));

			String isCluster = env.getProperty(REDIS_IS_CLUSTER);
			if (isCluster != null) {
				boolean isRedisCluster = Boolean.parseBoolean(isCluster);
				if (isRedisCluster) {
					// init redis cluster
					for (int i = 1; i <= serverNum; i++) {
						String name = env.getProperty(REDISCLUSTERPROPERTIES_NAME_PROFIXKEY + i);
						String hostAndPorts = env.getProperty(REDISCLUSTERPROPERTIES_HOST_AND_PORT_PROFIXKEY + i);
						String password = env.getProperty(REDISCLUSTERPROPERTIES_PASSWORD_PROFIXKEY + i);
						currentServerName = name;
						ClusterServerInfo clusterServerInfo = new ClusterServerInfo(name, password, hostAndPorts);
					}
					return;
				}
			}

			for (int i = 1; i <= serverNum; i++) {
				String name = env.getProperty(REDISPROPERTIES_NAME_PROFIXKEY + i);
				String host = env.getProperty(REDISPROPERTIES_HOST_PROFIXKEY + i);
				int port = Integer.parseInt(env.getProperty(REDISPROPERTIES_PORT_PROFIXKEY + i));
				String password = env.getProperty(REDISPROPERTIES_PASSWORD_PROFIXKEY + i);
				currentServerName = name;
				if (StringUtils.isNotBlank(name))
					super.createRedisConnection(new SingleServerInfo(name, host, port, password));
				else
					super.createRedisConnection(new SingleServerInfo(host, port, password));

				// runUpdateLimit();
			}
		} catch (NumberFormatException e) {
			log.error("initRedisServers: " + currentServerName + " occur NumberFormatException :" + e.getMessage());
			throw new RedisInitException(e);
		} catch (Throwable e1) {
			log.error("initRedisServers: " + currentServerName + " occur Throwable :" + e1.getMessage());
			throw new RedisInitException(currentServerName + " init failed", e1);
		}
	}

}
