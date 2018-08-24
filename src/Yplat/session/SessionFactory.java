package Yplat.session;

import javax.servlet.http.HttpServletRequest;

import Yplat.common.SystemTrans;
import Yplat.configManager.ConfigManager;
import Yplat.exception.SystemException;

/**
 * 会话建立工厂类 
 */
public class SessionFactory {

	public static int sessionType = ConfigManager.getInstance().getSystemConfig().getInt("session.type");
	
	public String sessionId;
	
	public HttpServletRequest request;
	
	public YSession createSession(boolean create) throws SystemException {
		switch (sessionType) {
		case YSession.HTTP_SESSION:
			return new YHttpSession(request, create);
		case YSession.REDIS_SESSION:
			if (create) {
				return new YRedisSession();
			}
			return new YRedisSession(sessionId);
		default:
			throw new SystemException(SystemTrans.CD_PROPERTY_PARA_LOSE_ERRO, SystemTrans.MSG_PROPERTY_PARA_LOSE_ERRO+",未配置sessiontype");
		}
	}


	public SessionFactory setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	public SessionFactory setRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}
	
	
}
