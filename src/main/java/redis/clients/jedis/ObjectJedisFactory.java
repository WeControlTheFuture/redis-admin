package redis.clients.jedis;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.JedisURIHelper;

public class ObjectJedisFactory implements PooledObjectFactory<ObjectJedis> {
	private final AtomicReference<HostAndPort> hostAndPort = new AtomicReference();
	private final int connectionTimeout;
	private final int soTimeout;
	private final String password;
	private final int database;
	private final String clientName;

	public ObjectJedisFactory(String host, int port, int connectionTimeout, int soTimeout, String password, int database, String clientName) {
		this.hostAndPort.set(new HostAndPort(host, port));
		this.connectionTimeout = connectionTimeout;
		this.soTimeout = soTimeout;
		this.password = password;
		this.database = database;
		this.clientName = clientName;
	}

	public ObjectJedisFactory(URI uri, int connectionTimeout, int soTimeout, String clientName) {
		if (!(JedisURIHelper.isValid(uri))) {
			throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI. %s", new Object[] { uri.toString() }));
		}

		this.hostAndPort.set(new HostAndPort(uri.getHost(), uri.getPort()));
		this.connectionTimeout = connectionTimeout;
		this.soTimeout = soTimeout;
		this.password = JedisURIHelper.getPassword(uri);
		this.database = JedisURIHelper.getDBIndex(uri);
		this.clientName = clientName;
	}

	public void setHostAndPort(HostAndPort hostAndPort) {
		this.hostAndPort.set(hostAndPort);
	}

	public void activateObject(PooledObject<ObjectJedis> pooledJedis) throws Exception {
		ObjectJedis jedis = (ObjectJedis) pooledJedis.getObject();
		if (jedis.getDB().longValue() != this.database)
			jedis.select(this.database);
	}

	public void destroyObject(PooledObject<ObjectJedis> pooledJedis) throws Exception {
		ObjectJedis jedis = (ObjectJedis) pooledJedis.getObject();
		if (!(jedis.isConnected()))
			return;
		try {
			try {
				jedis.quit();
			} catch (Exception localException) {
			}
			jedis.disconnect();
		} catch (Exception localException1) {
		}
	}

	public PooledObject<ObjectJedis> makeObject() throws Exception {
		HostAndPort hostAndPort = (HostAndPort) this.hostAndPort.get();
		ObjectJedis jedis = new ObjectJedis(hostAndPort.getHost(), hostAndPort.getPort(), this.connectionTimeout, this.soTimeout);
		try {
			jedis.connect();
			if (null != this.password) {
				jedis.auth(this.password);
			}
			if (this.database != 0) {
				jedis.select(this.database);
			}
			if (this.clientName != null)
				jedis.clientSetname(this.clientName);
		} catch (JedisException je) {
			jedis.close();
			throw je;
		}

		return new DefaultPooledObject(jedis);
	}

	public void passivateObject(PooledObject<ObjectJedis> pooledJedis) throws Exception {
	}

	public boolean validateObject(PooledObject<ObjectJedis> pooledJedis) {
		BinaryJedis jedis = (BinaryJedis) pooledJedis.getObject();
		try {
			HostAndPort hostAndPort = (HostAndPort) this.hostAndPort.get();

			String connectionHost = jedis.getClient().getHost();
			int connectionPort = jedis.getClient().getPort();

			return ((hostAndPort.getHost().equals(connectionHost)) && (hostAndPort.getPort() == connectionPort) && (jedis.isConnected()) && (jedis.ping().equals("PONG")));
		} catch (Exception e) {
		}
		return false;
	}
}
