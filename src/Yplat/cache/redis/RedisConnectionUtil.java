package Yplat.cache.redis;

import Yplat.configManager.ConfigManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * 使用jedis完成redis连接 
 */
public class RedisConnectionUtil {

	private static JedisPool jedisPool = null;
	
	private RedisConnectionUtil() {
		
	}
	
	//静态代码块，只加载一次，节省资源
	static {
		String host = ConfigManager.getInstance().getSystemConfig().getString("redis.server");
		int port = ConfigManager.getInstance().getSystemConfig().getInt("redis.port");
		int timeout = ConfigManager.getInstance().getSystemConfig().getInt("redis.connect.timeout");
		int maxWaitMillis = ConfigManager.getInstance().getSystemConfig().getInt("redis.max.wait.millis");
		int maxConnect = ConfigManager.getInstance().getSystemConfig().getInt("redis.max.connect");
		int minIdle = ConfigManager.getInstance().getSystemConfig().getInt("redis.min.idle");
		String password = ConfigManager.getInstance().getSystemConfig().getString("redis.password");
		boolean testOnBorrow = ConfigManager.getInstance().getSystemConfig().getBoolean("redis.testOnBorrow");
		
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxConnect);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		jedisPool = new JedisPool(config, host, port, timeout, password);
	}
	
	private Jedis getJedis() {
		return jedisPool.getResource();
	}
	
	private static RedisConnectionUtil redisConnectionUtil = new RedisConnectionUtil();
	
	public static RedisConnectionUtil getInstance() {
		return redisConnectionUtil;
	}
	
	/**
	 * 回收jedis 
	 */
	private void returnJedis(Jedis jedis) {
		try {
			if (null != jedis && null != jedisPool) {
				jedisPool.returnResource(jedis);
			}
		} catch (Exception e) {
			if (jedis.isConnected()) {
				jedis.quit();
				jedis.disconnect();
			}
		}
		
	}
	
	/**
	 * 销毁jedis资源 
	 */
	private void destroyRource(Jedis jedis) {
		try {
			if (null != jedis && null != jedisPool) {
				jedisPool.returnBrokenResource(jedis);
			}
		} catch (Exception e) {
			if (jedis.isConnected()) {
				jedis.quit();
				jedis.disconnect();
			}
		}
	}
	
	/**
	 * 在redis给key值设置有效时间 
	 * @param seconds:秒数
	 */
	public void setTime(String key,int seconds) {
		Jedis jedis = getJedis();
		jedis.expire(key, seconds);
		returnJedis(jedis);
	}
	
	/**
	 *  设值字符型
	 */
	public void setValue(String key,String value,int seconds) {
		Jedis jedis = getJedis();
		try {
			jedis.set(key, value);
			jedis.expire(key, seconds);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * 获取字符型 
	 */
	public String getValue(String key) {
		Jedis jedis = getJedis();
		try {
			String value = jedis.get(key);
			return value;
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return "";
	}
	
	/**
	 * 删除字符型 
	 */
	public boolean delValue(String key) {
		Jedis jedis = getJedis();
		boolean result = false;
		try {
			long results = jedis.del(key);
			if (results == 1) {
				result = true;
			}
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return result;
	}
	
	/**
	 * hash列表  
	 * @param key：哈希表名  field:key值
	 * redis返回值为 1 设置成功  0失败
	 */
	public boolean hset(String key,String field,String value,int seconds) {
		Jedis jedis = getJedis();
		boolean result = false;
		try {
			if (1 == jedis.hset(key, field, value)) {
				result = true;
			}
			jedis.expire(key, seconds);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return result;
	}
	
	/**
	 * hget hash若存在 在重新设置缓存时间
	 */
	public String hget(String key,String field,int time) {
		Jedis jedis = getJedis();
		try {
			
			String result = jedis.hget(key, field);
			jedis.expire(key, time);
			return result;
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return "";
	}
	
	/**
	 * hdel 删除hash列表的key 
	 */
	public boolean hdel(String key,String field) {
		Jedis jedis = getJedis();
		boolean resullt = false;
		try {
			long ret = jedis.hdel(key, field);
			if (ret == 1 || ret == 0) {
				resullt= true;
			}
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return resullt;
	}
	
	/**
	 * redis 队列 入栈
	 * 储存redis队列 顺序储存  (最后一个进入集合的 展示在最左边)
	 */
	public void lpush(String key,String... strings) {
		Jedis jedis = getJedis();
		try {
			jedis.lpush(key, strings);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * redis 队列 入栈
	 * 储存redis队列 倒序储存  (最后一个进入集合的 展示在最右边)
	 */
	public void rpush(String key,String... strings) {
		Jedis jedis = getJedis();
		try {
			jedis.rpush(key, strings);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * redis 队列 出栈
	 * 最左端（前端）的队列数据出栈
	 */
	public void lpop(String key) {
		Jedis jedis = getJedis();
		try {
			jedis.lpop(key);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * redis 队列 出栈
	 * 最右端（后端）的队列数据出栈
	 */
	public void rpop(String key) {
		Jedis jedis = getJedis();
		try {
			jedis.rpop(key);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * redis 第一次设值值 返回 1 成功；非首次赋值 失败 返回0
	 */
	public long setnx(String key,String value,int seconds) {
		Jedis jedis = getJedis();
		try {
			long result = jedis.setnx(key, value);
			if (result == 1) {
				jedis.expire(key, seconds);
			}
			return result;
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return 0;
	}
	
	/**
	 * redis消息发布 
	 */
	public void publish(String channel,String msg) {
		Jedis jedis = getJedis();
		try {
			jedis.publish(channel, msg);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * redis 消息订阅 监听 
	 */
	public void subscribe(JedisPubSub jedisPubSub,String... channels) {
		Jedis jedis = getJedis();
		try {
			jedis.subscribe(jedisPubSub, channels);
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
	}
	
	/**
	 * redis sadd 添加集合  添加成功 返回1 添加失败 返回0 
	 */
	public boolean sadd(String key,String value,int time) {
		Jedis jedis = getJedis();
		try {
			if (jedis.sadd(key, value) == 1) {
				jedis.expire(key, time);
				return true;
			}
		} catch (Exception e) {
			destroyRource(jedis);
			e.printStackTrace();
		}finally {
			returnJedis(jedis);
		}
		return false;
	}
}
