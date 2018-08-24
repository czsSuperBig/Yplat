package Yplat.session;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import Yplat.cache.redis.RedisConnectionUtil;
import Yplat.common.SystemTrans;
import Yplat.exception.SystemException;
import Yplat.model.SessionModel;
import Yplat.util.RandomUtil;

/**
 * 用redis实现会话的缓存，适合用于分布式系统做全局的会话信息保存 
 */
public class YRedisSession implements YSession{

	private static Logger logger = LoggerFactory.getLogger(YRedisSession.class);
	
	//redis实例
	private static RedisConnectionUtil redisConnection = RedisConnectionUtil.getInstance();
	
	//会话id
	private String sessionId;
	
	//创建会话的时间
	private String createTime;
	
	//该缓存是否有效
	private boolean isvalid = false;
	
	//开始创建缓存
	public YRedisSession() throws SystemException {
		// TODO Auto-generated constructor stub
		logger.info("====开始创建redis缓存====");
		sessionId = RandomUtil.getUUID();
		createTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		//设置值，创建时只设置创建时间
		if (!redisConnection.hset(sessionId, K_CREATE_TIME, createTime, EFFECT_TIME)) {
			logger.warn("======创建缓存失败======");
			throw new SystemException(SystemTrans.CD_CREATE_CACHE_ERROR, SystemTrans.MSG_CREATE_CACHE_ERROR);
		}
		isvalid = true;
		logger.info("=========缓存创建成功=========");
	}
	
	//用sessionid检测是否存在session缓存
	public YRedisSession(String sessionid) throws SystemException {
		if (StringUtils.isEmpty(sessionid)) {
			logger.error("======传入的sessionid为空=====");
			throw new SystemException(SystemTrans.CD_DATANULL_ERROR, SystemTrans.MSG_DATANULL_ERROR);
		}
		//检测缓存是否存在
		if (StringUtils.isEmpty(redisConnection.hget(sessionid, K_CREATE_TIME,EFFECT_TIME))) {
			logger.error("========redisSession缓存不存在============");
			throw new SystemException(SystemTrans.CD_GET_CACHE_ERROR, SystemTrans.MSG_GET_CACHE_ERROR);
		}
		sessionId = sessionid;
		isvalid = true;
	}
	
	@Override
	public String getSessionID() {
		// TODO Auto-generated method stub
		return sessionId;
	}

	@Override
	public boolean setUserInfo(Object obj) {
		// TODO Auto-generated method stub
		return redisConnection.hset(sessionId, K_USER_INFO, JSONObject.toJSONString(obj), EFFECT_TIME);
	}

	@Override
	public SessionModel getUserInfo() {
		// TODO Auto-generated method stub
		return getObj(K_USER_INFO, SessionModel.class);
	}

	@Override
	public String getValue(String key) {
		// TODO Auto-generated method stub
		return redisConnection.hget(sessionId, key, EFFECT_TIME);
	}

	@Override
	public boolean setValue(String key, String value) {
		// TODO Auto-generated method stub
		return redisConnection.hset(sessionId, key, value, EFFECT_TIME);
	}

	@Override
	public boolean removeValue(String key) {
		// TODO Auto-generated method stub
		return redisConnection.hdel(sessionId, key);
	}

	@Override
	public <T> T getObj(String key, Class<T> kclass) {
		// TODO Auto-generated method stub
		String result = getValue(key);
		if (StringUtils.isEmpty(result)) {
			return null;
		}
		return JSONObject.parseObject(result, kclass);
	}

	@Override
	public boolean setObj(String key, Object value) {
		// TODO Auto-generated method stub
		return setValue(key, JSONObject.toJSONString(value));
	}

	@Override
	public boolean removeObj(String key) {
		// TODO Auto-generated method stub
		return removeValue(key);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return isvalid;
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

}
