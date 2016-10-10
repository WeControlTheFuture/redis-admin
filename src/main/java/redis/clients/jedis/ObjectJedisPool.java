package redis.clients.jedis;

import java.net.URI;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.util.JedisURIHelper;
import redis.clients.util.Pool;

public class ObjectJedisPool extends Pool<ObjectJedis> {
	public ObjectJedisPool() {
		this("localhost", 6379);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host) {
		this(poolConfig, host, 6379, 2000, null, 0, null);
	}


	public ObjectJedisPool(String host, int port) {
		this(new GenericObjectPoolConfig(), host, port, 2000, null, 0, null);
	}

	public ObjectJedisPool(String host) {
		URI uri = URI.create(host);
		if (JedisURIHelper.isValid(uri)) {
			String h = uri.getHost();
			int port = uri.getPort();
			String password = JedisURIHelper.getPassword(uri);
			int database = JedisURIHelper.getDBIndex(uri);
			this.internalPool = new GenericObjectPool(new JedisFactory(h, port, 2000, 2000, password, database, null), new GenericObjectPoolConfig());
		} else {
			this.internalPool = new GenericObjectPool(new JedisFactory(host, 6379, 2000, 2000, null, 0, null), new GenericObjectPoolConfig());
		}
	}

	public ObjectJedisPool(URI uri) {
		this(new GenericObjectPoolConfig(), uri, 2000);
	}

	public ObjectJedisPool(URI uri, int timeout) {
		this(new GenericObjectPoolConfig(), uri, timeout);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password) {
		this(poolConfig, host, port, timeout, password, 0, null);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host, int port) {
		this(poolConfig, host, port, 2000, null, 0, null);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout) {
		this(poolConfig, host, port, timeout, null, 0, null);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database) {
		this(poolConfig, host, port, timeout, password, database, null);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database, String clientName) {
		this(poolConfig, host, port, timeout, timeout, password, database, clientName);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int connectionTimeout, int soTimeout, String password, int database, String clientName) {
		super(poolConfig, new ObjectJedisFactory(host, port, connectionTimeout, soTimeout, password, database, clientName));
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, URI uri) {
		this(poolConfig, uri, 2000);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, URI uri, int timeout) {
		this(poolConfig, uri, timeout, timeout);
	}

	public ObjectJedisPool(GenericObjectPoolConfig poolConfig, URI uri, int connectionTimeout, int soTimeout) {
		super(poolConfig, new ObjectJedisFactory(uri, connectionTimeout, soTimeout, null));
	}

	public ObjectJedis getResource() {
		ObjectJedis jedis = (ObjectJedis) super.getResource();
		// jedis.setDataSource(this);
		return jedis;
	}
}
