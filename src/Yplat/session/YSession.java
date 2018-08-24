package Yplat.session;

import Yplat.configManager.ConfigManager;
import Yplat.model.SessionModel;

/**
 * session接口 实现session的有两种方式 redis和http 
 * sessionFactory工厂类构建的处来的是Ysession
 */
public interface YSession {

	//session的有效时间
	public static final int EFFECT_TIME	= ConfigManager.getInstance().getSystemConfig().getInt("session.time");
	
	//httpsession
	public static final int HTTP_SESSION = 100;
	
	//redisSession
	public static final int REDIS_SESSION = 200;
	
	//用户键.
	public static final String K_USER_INFO = "user_info_";
	
	//创建时间.
	public static final String K_CREATE_TIME = "create_time_";
	
	//获取会话ID
	public String getSessionID();

	// 客户信息管理.
	public boolean setUserInfo(Object obj) /* throws JSystemException */;

	public SessionModel getUserInfo() /* throws JSystemException */;

	// 保存key-value
	public String getValue(String key);

	public boolean setValue(String key, String value);

	public boolean removeValue(String key);

	//不能保存List类型,List可以自己包装一层对象,或者写一个明确List泛型的类型的泛型方法.
	public <T> T getObj( String key, Class<T> kclass );

	public boolean setObj(String key, Object value);

	public boolean removeObj(String key);

	//检查会话是否存在.即客户是否登录了.
	public boolean isValid();

	//销毁会话
	public boolean destroy();

}
