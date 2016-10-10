package redis.clients.jedis;

import java.io.InvalidClassException;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.alibaba.fastjson.JSON;
import com.mauersu.util.SafeEncoderMore;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;


/**
 * @author bixy2
 *
 */
public class ObjectJedisCluster extends BinaryJedisCluster
{

	public ObjectJedisCluster(Set<HostAndPort> nodes)
	{
		this(nodes, 2000);
	}

	public ObjectJedisCluster(Set<HostAndPort> nodes, int timeout)
	{
		this(nodes, timeout, 5);
	}

	public ObjectJedisCluster(Set<HostAndPort> nodes, int timeout, int maxRedirections)
	{
		this(nodes, timeout, maxRedirections, new GenericObjectPoolConfig());
	}

	public ObjectJedisCluster(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig)
	{
		this(nodes, 2000, 5, poolConfig);
	}

	public ObjectJedisCluster(Set<HostAndPort> nodes, int timeout, GenericObjectPoolConfig poolConfig)
	{
		this(nodes, timeout, 5, poolConfig);
	}

	public ObjectJedisCluster(Set<HostAndPort> jedisClusterNode, int timeout, int maxRedirections,
			GenericObjectPoolConfig poolConfig)
	{
		super(jedisClusterNode, timeout, maxRedirections, poolConfig);
	}

	public ObjectJedisCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxRedirections,
			GenericObjectPoolConfig poolConfig)
	{
		super(jedisClusterNode, connectionTimeout, soTimeout, maxRedirections, poolConfig);
	}

	public String set(final String key, final Object value)
	{
		return new JedisClusterCommand<String>(connectionHandler, maxRedirections)
		{
			@Override
			public String execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().set(SafeEncoder.encode(key), SafeEncoderMore.encode(value));
				return connection.getClient().getStatusCodeReply();
			}
		}.run(key);
	}

	public Long setnx(final String key, final Object value)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().setnx(SafeEncoder.encode(key), SafeEncoderMore.encode(value));
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}

	public Long hsetnx(final String key, final String field, final Object value)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().hsetnx(SafeEncoder.encode(key), SafeEncoder.encode(field), SafeEncoderMore.encode(value));
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}

	public Object get(final String key)
	{
		return new JedisClusterCommand<Object>(connectionHandler, maxRedirections)
		{
			@Override
			public Object execute(Jedis connection)
			{
				try
				{
					connection.checkIsInMultiOrPipeline();
					connection.getClient().sendCommand(Protocol.Command.GET, key);
					return SafeEncoderMore.decode(connection.getClient().getBinaryBulkReply());
				}
				catch (JedisException e)
				{
					if (e.getCause() instanceof InvalidClassException)
					{
						//here we swallow this exception because Object put into redis may have different serializeId
						//if we suffer this situation, we just return null instead
						//TODO: add some log ......
					}
					else
						throw e;
				}
				return null;
			}
		}.run(key);
	}

	public Object hget(final String key, final String field)
	{
		return new JedisClusterCommand<Object>(connectionHandler, maxRedirections)
		{
			@Override
			public Object execute(Jedis connection)
			{
				try
				{
					connection.checkIsInMultiOrPipeline();
					connection.getClient().sendCommand(Protocol.Command.HGET, key, field);
					return SafeEncoderMore.decode(connection.getClient().getBinaryBulkReply());
				}
				catch (JedisException e)
				{
					if (e.getCause() instanceof InvalidClassException)
					{
						//here we swallow this exception because Object put into redis may have different serializeId
						//if we suffer this situation, we just return null instead
						//TODO: add some log ......
					}
					else
						throw e;
				}
				return null;
			}
		}.run(key);
	}

	public <T> List<T> get(final String key, Class<T> cls)
	{
		String value = new JedisClusterCommand<String>(connectionHandler, maxRedirections)
		{
			@Override
			public String execute(Jedis connection)
			{
				try
				{
					connection.checkIsInMultiOrPipeline();
					connection.getClient().sendCommand(Protocol.Command.GET, key);
					return connection.getClient().getBulkReply();
				}
				catch (JedisException e)
				{
					if (e.getCause() instanceof InvalidClassException)
					{
						//here we swallow this exception because Object put into redis may have different serializeId
						//if we suffer this situation, we just return null instead
						//TODO: add some log ......
					}
					else
						throw e;
				}
				return null;
			}
		}.run(key);
		if (value == null)
			return null;
		else
			return JSON.parseArray(value, cls);
	}

	public Boolean exists(final String key)
	{
		return new JedisClusterCommand<Boolean>(connectionHandler, maxRedirections)
		{
			@Override
			public Boolean execute(Jedis connection)
			{
				return connection.exists(key);
			}
		}.run(key);
	}

	public Long exists(final String... keys)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				return connection.exists(keys);
			}
		}.run(keys.length, keys);
	}

	public Boolean hexists(final String key, final String field)
	{
		return new JedisClusterCommand<Boolean>(connectionHandler, maxRedirections)
		{
			@Override
			public Boolean execute(Jedis connection)
			{
				return connection.hexists(key, field);
			}
		}.run(key);
	}

	public String setex(final String key, final int seconds, final Object value)
	{
		return new JedisClusterCommand<String>(connectionHandler, maxRedirections)
		{
			@Override
			public String execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().setex(SafeEncoder.encode(key), seconds, SafeEncoderMore.encode(value));
				return connection.getClient().getStatusCodeReply();
			}
		}.run(key);
	}

	public Object hinvalidate(final String key, final String field)
	{
		return new JedisClusterCommand<Object>(connectionHandler, maxRedirections)
		{
			@Override
			public Object execute(Jedis connection)
			{
				connection.multi();
				connection.getClient().sendCommand(Protocol.Command.HGET, key, field);
				Object result = SafeEncoderMore.decode(connection.getClient().getBinaryBulkReply());
				if (result != null)
					connection.getClient().sendCommand(Protocol.Command.HDEL, key, field);
				connection.getClient().exec();
				return result;
			}
		}.run(key);
	}

	public Object invalidate(final String key)
	{
		return new JedisClusterCommand<Object>(connectionHandler, maxRedirections)
		{
			@Override
			public Object execute(Jedis connection)
			{
				connection.multi();
				connection.getClient().sendCommand(Protocol.Command.GET, key);
				Object result = SafeEncoderMore.decode(connection.getClient().getBinaryBulkReply());
				if (result != null)
					connection.getClient().sendCommand(Protocol.Command.DEL, key);
				connection.getClient().exec();
				return result;
			}
		}.run(key);
	}

	public Long hset(final String key, final String field, final String value)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				connection.getClient().hset(key, field, value);
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}

	public Long del(final String key)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				connection.getClient().del(key);
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Set<String> hkeys(final String key)
	{
		return new JedisClusterCommand<Set>(connectionHandler, maxRedirections)
		{

			@Override
			public Set execute(Jedis connection)
			{
				connection.getClient().hkeys(key);
				return ((Set) BuilderFactory.STRING_SET.build(connection.getClient().getBinaryMultiBulkReply()));
			}
		}.run(key);
	}

	public Long hlen(final String key)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{

			@Override
			public Long execute(Jedis connection)
			{
				connection.getClient().hlen(key);
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}

	@SuppressWarnings("unchecked")
	public Set<String> keys(final String pattern)
	{
		return new JedisClusterCommand<Set<String>>(connectionHandler, maxRedirections)
		{

			@Override
			public Set<String> execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().keys(pattern);
				return (Set) BuilderFactory.STRING_SET.build(connection.getClient().getBinaryMultiBulkReply());
			}
		}.run(pattern);
	}

	public Long sadd(final String key, final String... value)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().sadd(key, value);
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}

	public Set<String> smembers(final String key)
	{
		return new JedisClusterCommand<Set<String>>(connectionHandler, maxRedirections)
		{
			@Override
			public Set<String> execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().smembers(key);
				List<String> members = connection.getClient().getMultiBulkReply();
				if (members == null)
				{
					return null;
				}
				return BinaryJedis.SetFromList.of(members);
			}
		}.run(key);
	}

	public Long srem(final String key, final String... value)
	{
		return new JedisClusterCommand<Long>(connectionHandler, maxRedirections)
		{
			@Override
			public Long execute(Jedis connection)
			{
				connection.checkIsInMultiOrPipeline();
				connection.getClient().srem(key, value);
				return connection.getClient().getIntegerReply();
			}
		}.run(key);
	}
}
