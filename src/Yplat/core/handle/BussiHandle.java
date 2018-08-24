package Yplat.core.handle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import Yplat.common.SystemTrans;
import Yplat.core.ApiStore.ApiRunnable;
import Yplat.core.YHandleContent;
import Yplat.exception.SystemException;
import Yplat.model.SessionModel;
import Yplat.session.SessionFactory;
import Yplat.session.YSession;
import Yplat.util.HttpHandleUtil;

/**
 * 业务方法处理流程 
 */
public class BussiHandle {

	private static final Logger log = LoggerFactory.getLogger(BussiHandle.class);
	
	private static BussiHandle bussiHandle = new BussiHandle();
	
	private BussiHandle() {
		
	}
	
	public static BussiHandle getInstance() {
		return bussiHandle;
	}
	
	public Object handleBuss(String paraStr,YHandleContent content) throws SystemException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = content.getApiRunnable().getTargetMethod();
		Class<?>[] paramsTypes = method.getParameterTypes();
		if (paramsTypes == null || paramsTypes.length == 0) {
			log.warn("业务代号：{}对应的处理方法的参数为空",content.getApiRunnable().getApiName());
			throw new SystemException(SystemTrans.CD_REQ_PARA_ERROR, SystemTrans.MSG_REQ_PARA_ERROR);
		}
		if (paramsTypes.length > 2) {
			log.warn("业务代号：{}对应的处理方法的参数不能超过两个",content.getApiRunnable().getApiName());
			throw new SystemException(SystemTrans.CD_REQ_PARA_ERROR, SystemTrans.MSG_REQ_PARA_ERROR);
		}
		/**
		 * 任何业务处理方法的参数只能由请求数据的model和会话model组成，故最多两个参数，若不需要会话可以省去 
		 */
		Object[] args = new Object[paramsTypes.length];
		for (int i = 0; i < paramsTypes.length; i++) {
			if (paramsTypes[i] == SessionModel.class) {
				//第二个参数是会话类model,获取会话信息
				args[i] = content.getSessionModel();
				continue;
			}
			args[i] = JSONObject.parseObject(paraStr, paramsTypes[i]);
		}
		Object result = content.getApiRunnable().run(args);
		return result;
	}
}
