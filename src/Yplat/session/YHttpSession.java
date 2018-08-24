package Yplat.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Yplat.common.SystemTrans;
import Yplat.exception.SystemException;
import Yplat.model.SessionModel;

/**
 * httpSession实现session  只用于无redis 且线上只有单台服务器的情况下的 session缓存 
 */
public class YHttpSession implements YSession{

	private static Logger logger = LoggerFactory.getLogger(YHttpSession.class);
	
	private HttpSession session;
	
	private HttpServletRequest request;
	
	public YHttpSession(HttpServletRequest req,boolean create) throws SystemException {
		// TODO Auto-generated constructor stub
		request = req;
		//强制创建缓存
		if (create && request.isRequestedSessionIdValid()) {
			HttpSession httpSession = req.getSession();
			if (httpSession != null) {
				httpSession.invalidate();//使缓存失效
			}
		}
		session = req.getSession(create);
		session.setMaxInactiveInterval(EFFECT_TIME);
		//不需要创建session 但session为空
		if (!create && session == null) {
			logger.error("=======httpSession为空错误====");
			throw new SystemException(SystemTrans.CD_GET_CACHE_ERROR, SystemTrans.MSG_GET_CACHE_ERROR);
		}
	}
	
	@Override
	public String getSessionID() {
		// TODO Auto-generated method stub
		if (session != null) {
			return session.getId();
		}
		return null;
	}

	@Override
	public boolean setUserInfo(Object obj) {
		// TODO Auto-generated method stub
		return setObj(K_USER_INFO, obj);
	}

	@Override
	public SessionModel getUserInfo() {
		// TODO Auto-generated method stub
		return getObj(K_USER_INFO, SessionModel.class);
	}

	@Override
	public String getValue(String key) {
		// TODO Auto-generated method stub
		return getObj(key, String.class);
	}

	@Override
	public boolean setValue(String key, String value) {
		// TODO Auto-generated method stub
		return setObj(key, value);
	}

	@Override
	public boolean removeValue(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T getObj(String key, Class<T> kclass) {
		// TODO Auto-generated method stub
		if (session != null) {
			return (T) (session.getAttribute(key));
		}
		return null;
	}

	@Override
	public boolean setObj(String key, Object value) {
		// TODO Auto-generated method stub
		if(session != null) {
			session.setAttribute(key, value);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeObj(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		if (session != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return false;
	}

}
