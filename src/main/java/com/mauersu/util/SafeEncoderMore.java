package com.mauersu.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

/**
 * @author bixy2
 *
 */
public class SafeEncoderMore extends SafeEncoder {

	public static String encode(byte[] data) {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new JedisException(e);
		}
	}

	public static byte[] encode(Object value) {
		if (value == null) {
			throw new JedisDataException("value sent to redis cannot be null");
		}
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = null;
		try {
			oo = new ObjectOutputStream(bo);
			oo.writeObject(value);
			return bo.toByteArray();
		} catch (IOException e) {
			throw new JedisException(e);
		} finally {
			if (oo != null) {
				try {
					oo.close();
				} catch (IOException io) {
					throw new JedisException(io);
				}
			}
		}
	}

	public static Object decode(byte[] data) {
		if (data == null || data.length == 0)
			return null;
		ByteArrayInputStream bi = new ByteArrayInputStream(data);
		ObjectInputStream oi = null;
		try {
			oi = new ObjectInputStream(bi);
			return oi.readObject();
		} catch (ClassNotFoundException e) {
			throw new JedisException(e);
		} catch (IOException e) {
			throw new JedisException(e);
		}
	}
}