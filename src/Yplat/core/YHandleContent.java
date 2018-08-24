package Yplat.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Yplat.core.ApiStore.ApiRunnable;
import Yplat.exception.SystemException;
import Yplat.model.SessionModel;
import Yplat.session.SessionFactory;
import Yplat.session.YSession;
import Yplat.util.HttpHandleUtil;
import freemarker.template.Configuration;

/**
 * 每次请求都会创建一次content上下文，以此来保存一些过程中计算好的结果，或者请求参数 
 */
public class YHandleContent {

	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private ApiRunnable apiRunnable;
	
	//freemarker模板配置类
	private Configuration cfg;
	
	private SessionModel sessionModel;
	
	//从前端生成的aes密钥，每次请求前端产生的aeskey均不同，返回的数据用aeskey加密
	private String aesKey;
	
	//生成的防重的随机字符串
	private String repeatStr;
	
	public YHandleContent(HttpServletRequest req,HttpServletResponse resp) {
		this.request = req;
		this.response = resp;
	}

	public ApiRunnable getApiRunnable() {
		return apiRunnable;
	}

	public void setApiRunnable(ApiRunnable apiRunnable) {
		this.apiRunnable = apiRunnable;
	}

	public SessionModel getSessionModel() throws SystemException {
		if (sessionModel != null) {
			return sessionModel;
		}
		YSession session = new SessionFactory().setRequest(request).setSessionId(HttpHandleUtil.obtainSessionid(request)).createSession(false);
		SessionModel model = session.getUserInfo();
		this.sessionModel = model;
		return model;
	}
	
	public YSession setSessionModel(SessionModel sessModel,boolean iscreate) throws SystemException {
		YSession session;
		if (iscreate) {
			session = new SessionFactory().setRequest(request).createSession(true);
			session.setObj(session.K_USER_INFO, sessModel);
		}else {
			session = new SessionFactory().setRequest(request).setSessionId(HttpHandleUtil.obtainSessionid(request)).createSession(false);
			session.setObj(session.K_USER_INFO, sessModel);
		}
		this.sessionModel = sessModel;
		return session;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String getAesKey() {
		return aesKey;
	}

	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}

	public String getRepeatStr() {
		return repeatStr;
	}

	public void setRepeatStr(String repeatStr) {
		this.repeatStr = repeatStr;
	}

	public Configuration getCfg() {
		return cfg;
	}

	public void setCfg(Configuration cfg) {
		this.cfg = cfg;
	}
	
	
}
