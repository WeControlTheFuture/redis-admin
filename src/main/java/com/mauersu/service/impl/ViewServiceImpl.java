package com.mauersu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mauersu.redis.IRedisClient;
import com.mauersu.service.ViewService;
import com.mauersu.util.Constant;
import com.mauersu.util.ConvertUtil;
import com.mauersu.util.Pagination;
import com.mauersu.util.RKey;
import com.mauersu.util.RedisApplication;
import com.mauersu.util.RefreshModeEnum;
import com.mauersu.util.ServerInfo;
import com.mauersu.util.ShowTypeEnum;
import com.mauersu.util.ztree.RedisZtreeUtil;
import com.mauersu.util.ztree.ZNode;

@Service
public class ViewServiceImpl extends RedisApplication implements ViewService, Constant {

	@Override
	public void changeRefreshMode(String mode) {
		refreshMode = RefreshModeEnum.valueOf(mode);
	}

	@Override
	public Set<ZNode> getLeftTree() {
		return getLeftTree(refreshMode);
	}

	private Set<ZNode> getLeftTree(RefreshModeEnum refreshMode) {
		switch (refreshMode) {
		case manually:
			break;
		case auto:
			for (ServerInfo serverInfo : RedisApplication.redisServerCache) {
				RedisZtreeUtil.refreshRedisNavigateZtree(serverInfo.getName());
			}
			break;
		}
		return new TreeSet<ZNode>(redisNavigateZtree);

	}

	@Override
	public Set<ZNode> refresh() {
		boolean permit = getUpdatePermition();
		Set<ZNode> zTree = null;
		if (permit) {
			try {
				logCurrentTime("try {");
				for (ServerInfo serverInfo : RedisApplication.redisServerCache) {
					logCurrentTime("refreshKeys(" + serverInfo.getName());
					for (int i = 0; i <= REDIS_DEFAULT_DB_SIZE; i++) {
						refreshKeys(serverInfo.getName(), i);
					}
					logCurrentTime("refreshServerTree(" + serverInfo.getName());
					zTree = refreshServerTree(serverInfo.getName(), DEFAULT_DBINDEX);
					// test limit flow System.out.println("yes permit");
					logCurrentTime("continue");
				}
				logCurrentTime("finally {");
			} finally {
				finishUpdate();
			}
		} else {
			// test limit flow System.out.println("no permit");
		}
		return zTree;
	}

	@Override
	public void refreshAllKeys() {
		boolean permit = getUpdatePermition();
		try {
			for (ServerInfo serverInfo : RedisApplication.redisServerCache) {
				for (int i = 0; i <= REDIS_DEFAULT_DB_SIZE; i++) {
					refreshKeys(serverInfo.getName(), i);
				}
			}
		} finally {
			finishUpdate();
		}
	}

	private void refreshKeys(String serverName, int dbIndex) {
		IRedisClient redisTemplate = RedisApplication.redisTemplatesMap.get(serverName);
		// initRedisKeysCache(redisTemplate, serverName, dbIndex);
	}

	private Set<ZNode> refreshServerTree(String serverName, int dbIndex) {
		RedisZtreeUtil.refreshRedisNavigateZtree(serverName);
		return new TreeSet<ZNode>(redisNavigateZtree);
	}

	@Override
	public Set<RKey> getRedisKeys(Pagination pagination, String serverName, String dbIndex, String[] keyPrefixs,
			String queryKey, String queryValue) {

		Set<RKey> resultRedisKeys = null;

		if (keyPrefixs == null || keyPrefixs.length == 0) {
			logCurrentTime("keyPrefixs == null");
			if (!StringUtils.isEmpty(queryValue)) {
				List<RKey> queryRedisKeys = getQueryRedisKeys(redisTemplatesMap.get(serverName), queryKey, queryValue);
				int toIndex = pagination.getToIndex() > queryRedisKeys.size() ? queryRedisKeys.size()
						: pagination.getToIndex();
				List<RKey> resultList = queryRedisKeys.subList(pagination.getFromIndex(), toIndex);
				resultRedisKeys = new TreeSet<RKey>(resultList);
				pagination.setMaxentries(queryRedisKeys.size());
			}
		} else {
			StringBuffer keyPrefix = new StringBuffer("");
			for (String prefix : keyPrefixs) {
				keyPrefix.append(prefix).append(DEFAULT_REDISKEY_SEPARATOR);
			}
			List<RKey> conformRedisKeys = getConformRedisKeys(redisTemplatesMap.get(serverName), keyPrefix.toString());
			int toIndex = pagination.getToIndex() > conformRedisKeys.size() ? conformRedisKeys.size()
					: pagination.getToIndex();
			List<RKey> resultList = conformRedisKeys.subList(pagination.getFromIndex(), toIndex);
			resultRedisKeys = new TreeSet<RKey>(resultList);
			pagination.setMaxentries(conformRedisKeys.size());
		}
		return resultRedisKeys;
	}

	protected List<RKey> getRedisKeys(IRedisClient redisClient, String pattern) {
		Set<String> keysSet = redisClient.keys(pattern);
		List<RKey> tempList = new ArrayList<RKey>();
		ConvertUtil.convertByteToString(redisClient, keysSet, tempList);
		return tempList;
	}

	private List<RKey> getQueryRedisKeys(IRedisClient redisClient, String queryKey, String queryValue) {
		List<RKey> rKeySet = new ArrayList<RKey>();
		switch (queryKey) {
		case MIDDLE_KEY:
			return getRedisKeys(redisClient, "*" + queryValue + "*");
		case HEAD_KEY:
			return getRedisKeys(redisClient, queryValue + "*");
		case TAIL_KEY:
			return getRedisKeys(redisClient, "*" + queryValue);
		}
		return rKeySet;
	}

	private List<RKey> getConformRedisKeys(IRedisClient redisClient, String keyPrefix) {
		// List<RKey> allRedisKeys = getRedisKeys(redisClient);
		// List<RKey> rKeySet = new ArrayList<RKey>();
		// for (RKey rKey : allRedisKeys) {
		// if (rKey.startsWith(keyPrefix)) {
		// rKeySet.add(rKey);
		// }
		// }
		return getRedisKeys(redisClient, keyPrefix + "*");
	}

	@Override
	public void changeShowType(String state) {
		showType = ShowTypeEnum.valueOf(state);
		switch (showType) {
		case show:
			// get redisKeys again if init keys with ShowTypeEnum.hide
			refreshAllKeys();
			break;
		case hide:
			break;
		}
	}

}
