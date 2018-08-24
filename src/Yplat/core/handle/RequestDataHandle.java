package Yplat.core.handle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Yplat.annotation.ApiMapping;
import Yplat.cache.redis.RedisConnectionUtil;
import Yplat.common.SystemTrans;
import Yplat.core.YHandleContent;
import Yplat.exception.SystemException;
import Yplat.model.RequestModel;
import Yplat.model.SessionModel;
import Yplat.util.encrypt.AESUtils;
import Yplat.util.encrypt.RSAUtils;

/**
 * 请求过来的数据处理 根据ApiMapping中的参数进行处理  得到body中的明文数据
 */
public class RequestDataHandle {

	private static Logger logger = LoggerFactory.getLogger(RequestDataHandle.class);
	
	private static RequestDataHandle requestDataHandle = new RequestDataHandle();
	
	private RequestDataHandle() {
		
	}
	
	public static RequestDataHandle getInstance() {
		return requestDataHandle;
	}
	
	public String getRequestDataBody(YHandleContent content,RequestModel requestModel) throws SystemException {
		ApiMapping apiMapping = content.getApiRunnable().getApiMapping();
		//1、是否防重检测
		if (apiMapping.isRepeat()) {
			content.setRepeatStr(requestModel.getHead().getMark());
			checkRepeat(content);
		}
		//2、是否会话检测
		if (apiMapping.checkSession()) {
			checkSession(content);
		}
		//3、是否数据解密
		if (apiMapping.isDataEncrypt()) {
			requestModel.setBody(decrypt(content, requestModel.getBody()));
		}
		return requestModel.getBody();
	}
	
	/**
	 * 请求防重检测
	 * 主要是获取请求头中的mark 放入redis维护的集合中，如保存成功则为第一次请求，失败 则是重复请求
	 * @throws SystemException 
	 */
	private boolean checkRepeat(YHandleContent content) throws SystemException {
		if (StringUtils.isEmpty(content.getRepeatStr())) {
			return true;
		}
		if (!RedisConnectionUtil.getInstance().sadd("repeat-"+content.getApiRunnable().getApiName(), content.getRepeatStr(), 30)) {
			logger.warn("交易重复,请勿重复提交，交易防重标记mark===={}",content.getRepeatStr());
			throw new SystemException(SystemTrans.CD_REPEAT_ERROR, SystemTrans.MSG_REPEAT_ERROR);
		}
		return true;
	}
	 
	/**
	 * 会话检测 
	 * @throws SystemException 
	 */
	private boolean checkSession(YHandleContent content) throws SystemException {
		SessionModel sessionModel = content.getSessionModel();
		if (sessionModel == null) {
			logger.error("=====会话为空======");
			throw new SystemException(SystemTrans.CD_GET_CACHE_ERROR, SystemTrans.MSG_GET_CACHE_ERROR);
		}
		return true;
	}
	
	/**
	 * 数据解密 
	 * @throws SystemException 
	 */
	private String decrypt(YHandleContent content,String requestData) throws SystemException {
		logger.info("需要解密的密文=============：{}",requestData);
		String[] dataArray = requestData.split("#");
		if (dataArray.length != 2) {
			logger.error("=============密文的格式错误============");
			throw new SystemException(SystemTrans.CD_DATA_DECODE_ERROR, SystemTrans.MSG_DATA_DECODE_ERROR);
		}
		SessionModel sessionModel = content.getSessionModel();
		if (sessionModel == null) {
			logger.error("=============会话为空============");
			throw new SystemException(SystemTrans.CD_GET_CACHE_ERROR, SystemTrans.MSG_GET_CACHE_ERROR);
		}
		if (StringUtils.isEmpty(sessionModel.getRsaPrivateKey())) {
			logger.error("========会话中的rsa私钥为空==========");
			throw new SystemException(SystemTrans.CD_DATA_DECODE_ERROR, SystemTrans.MSG_DATA_DECODE_ERROR);
		}
		try {
			String aeskey = RSAUtils.decryptByPrivateKey(dataArray[1], sessionModel.getRsaPrivateKey());
			content.setAesKey(aeskey);
			logger.info("有前端生成的16为aes密钥为======：{}",aeskey);
			String result = AESUtils.decryptData(aeskey, dataArray[0]);
			logger.info("解密后的明文为==========：{}",result);
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(SystemTrans.CD_DATA_DECODE_ERROR, SystemTrans.MSG_DATA_DECODE_ERROR);
		}
	}
}
