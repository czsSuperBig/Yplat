package Yplat.aop;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import Yplat.annotation.CacheData;
import Yplat.cache.redis.RedisConnectionUtil;

/**
 * 利用切面实现 CacheData注解对方法处理后的结果的缓存 
 */
@Aspect
@Component
public class CacheDataAop {

	private static Logger logging = LoggerFactory.getLogger(CacheDataAop.class);
	
	@Around("@annotation(Yplat.annotation.CacheData)")
	public Object cacheHandle(final ProceedingJoinPoint joinPoint) {
		if (joinPoint == null) {
			return null;
		}
		Object result = null;
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		CacheData cacheData = method.getAnnotation(CacheData.class);
		RedisConnectionUtil redisConnection = RedisConnectionUtil.getInstance();
		String key = cacheData.name()+cacheData.condition();
		String value = redisConnection.getValue(key);
		if (StringUtils.isNotEmpty(value)) {
			if (cacheData.addCache()) {
				logging.info("###########方法{},命中了缓存############",method.getDeclaringClass().getName()+"."+method.getName());
				result = JSON.parse(value);
				return result;
			}else {
				logging.info("###########方法{},虽然命中了缓存,但需要删除该缓存############",method.getDeclaringClass().getName()+"."+method.getName());
				redisConnection.delValue(key);
			}
		}
		Object[] args = joinPoint.getArgs();
		try {
			synchronized (this) {
				String value2 = redisConnection.getValue(key);
				if (StringUtils.isNotEmpty(value2)) {
					if (cacheData.addCache()) {
						logging.info("###########方法{},命中了缓存############",method.getDeclaringClass().getName()+"."+method.getName());
						result = JSON.parse(value2);
						return result;
					}else {
						logging.info("###########方法{},虽然命中了缓存,但需要删除该缓存############",method.getDeclaringClass().getName()+"."+method.getName());
						redisConnection.delValue(key);
					}
				}
				result = joinPoint.proceed(args);
				if (cacheData.addCache()) {
					logging.info("###########方法{},未命中缓存，将查询的结果存入缓存############",method.getDeclaringClass().getName()+"."+method.getName());
					redisConnection.setValue(key, JSON.toJSONString(result), cacheData.time());
				}
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}
		return result;
	}
}
