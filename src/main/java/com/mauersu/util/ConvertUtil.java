package com.mauersu.util;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.connection.DataType;

import com.mauersu.redis.IRedisClient;

import jetbrick.util.StringUtils;

public class ConvertUtil {

	public static void convertByteToString(IRedisClient redisClient, Set<String> keysSet, List<RKey> tempList) {
		for (String key : keysSet) {
			DataType dateType = null;
			String type = redisClient.type(key);
			if (StringUtils.isNotBlank(type))
				switch (RedisApplication.showType) {
				case show:
					dateType = DataType.fromCode(type);
					break;
				case hide:
					dateType = DataType.NONE;
					break;
				default:
					dateType = DataType.fromCode(type);
					break;
				}
			RKey rkey = new RKey(key, dateType);
			tempList.add(rkey);
		}
	}

	public static double[] convert2Double(String[] strings) {
		if (strings == null)
			return null;
		double[] doubleList = new double[strings.length];
		for (int i = 0; i < strings.length; i++) {
			double d = Double.parseDouble(strings[i]);
			doubleList[i] = d;
		}
		return doubleList;
	}
}
