/**
 * 
 */
package redis.clients.jedis;

import java.io.InvalidClassException;
import java.util.List;
import java.util.Set;

import com.mauersu.util.SafeEncoderMore;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;


/**
 * @author bixy2
 *
 */
public class ObjectJedis extends BinaryJedis
{

	public ObjectJedis()
	{
		super();
	}

	public ObjectJedis(final String host)
	{
		super(host);
	}

	public ObjectJedis(final String host, final int port)
	{
		super(host, port);
	}

	public ObjectJedis(final String host, final int port, final int timeout)
	{
		super(host, port, timeout);
	}

	public ObjectJedis(final String host, final int port, final int connectionTimeout, final int soTimeout)
	{
		super(host, port, connectionTimeout, soTimeout);
	}

	/**
	 * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1 GB).
	 * <p>
	 * Time complexity: O(1)
	 * 
	 * @param key
	 * @param value
	 * @return Status code reply
	 */
	public String set(String key, Object value)
	{
		checkIsInMultiOrPipeline();
		this.client.set(SafeEncoder.encode(key), SafeEncoderMore.encode(value));
		return this.client.getStatusCodeReply();
	}

	/**
	 * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1 GB).
	 * 
	 * @param key
	 * @param value
	 * @param nxxx
	 *           NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key if it already exist.
	 * @param expx
	 *           EX|PX, expire time units: EX = seconds; PX = milliseconds
	 * @param time
	 *           expire time in the units of <code>expx</code>
	 * @return Status code reply
	 */
	public String set(String key, Object value, String nxxx, String expx, long time)
	{
		checkIsInMultiOrPipeline();
		this.client.set(SafeEncoder.encode(key), SafeEncoderMore.encode(value), SafeEncoder.encode(nxxx), SafeEncoder.encode(expx),
				time);
		return this.client.getStatusCodeReply();
	}

	/**
	 * Get the value of the specified key. If the key does not exist null is returned. If the value stored at key is not
	 * a string an error is returned because GET can only handle string values.
	 * <p>
	 * Time complexity: O(1)
	 * 
	 * @param key
	 * @return Bulk reply
	 */
	public Object get(final String key)
	{
		try
		{
			checkIsInMultiOrPipeline();
			client.sendCommand(Protocol.Command.GET, key);
			return SafeEncoderMore.decode(client.getBinaryBulkReply());
		}
		catch (JedisException e)
		{
			if (e.getCause() instanceof InvalidClassException)
			{
			}
			else
				throw e;
		}
		return null;
	}

	/**
	 * Test if the specified key exists. The command returns the number of keys existed Time complexity: O(N)
	 * 
	 * @param keys
	 * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed 0 if none of the
	 *         specified key existed
	 */
	public Long exists(final String... keys)
	{
		checkIsInMultiOrPipeline();
		client.exists(keys);
		return client.getIntegerReply();
	}

	/**
	 * Test if the specified key exists. The command returns "1" if the key exists, otherwise "0" is returned. Note that
	 * even keys set with an empty string as value will return "1". Time complexity: O(1)
	 * 
	 * @param key
	 * @return Boolean reply, true if the key exists, otherwise false
	 */
	public Boolean exists(final String key)
	{
		checkIsInMultiOrPipeline();
		client.exists(key);
		return client.getIntegerReply() == 1;
	}

	/**
	 * Remove the specified keys. If a given key does not exist no operation is performed for this key. The command
	 * returns the number of keys removed. Time complexity: O(1)
	 * 
	 * @param keys
	 * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed 0 if none of the
	 *         specified key existed
	 */
	public Long del(final String... keys)
	{
		checkIsInMultiOrPipeline();
		client.del(keys);
		return client.getIntegerReply();
	}

	/**
	 * The command is exactly equivalent to the following group of commands: {@link #set(String, String) SET} +
	 * {@link #expire(String, int) EXPIRE}. The operation is atomic.
	 * <p>
	 * Time complexity: O(1)
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return Status code reply
	 */
	public String setex(final String key, final int seconds, final Object value)
	{
		checkIsInMultiOrPipeline();
		client.setex(SafeEncoder.encode(key), seconds, SafeEncoderMore.encode(value));
		return client.getStatusCodeReply();
	}

	public Long sadd(final String key, final String... value)
	{
		checkIsInMultiOrPipeline();
		client.sadd(key, value);
		return client.getIntegerReply();
	}

	public Long srem(String key, String... value)
	{
		checkIsInMultiOrPipeline();
		client.srem(key, value);
		return client.getIntegerReply();
	}

	public Set<String> smembers(String key)
	{
		checkIsInMultiOrPipeline();
		client.smembers(key);
		List<String> members = client.getMultiBulkReply();
		if (members == null)
		{
			return null;
		}
		return BinaryJedis.SetFromList.of(members);
	}
}
