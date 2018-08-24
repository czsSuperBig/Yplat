package Yplat.core.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import Yplat.annotation.ApiMapping;
import Yplat.common.BussTrans;
import Yplat.common.Const;
import Yplat.common.SystemTrans;
import Yplat.core.YHandleContent;
import Yplat.exception.SystemException;
import Yplat.model.ResponseHeadModel;
import Yplat.model.ResponseModel;
import Yplat.model.SessionModel;
import Yplat.session.YSession;
import Yplat.util.RandomUtil;
import Yplat.util.encrypt.AESUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * 处理结果响应
 */
public class ResponseDataHandle {

	private static final Logger log = LoggerFactory.getLogger(ResponseDataHandle.class);
	
	private static ResponseDataHandle responseDataHandle = new ResponseDataHandle();
	
	private ResponseDataHandle() {
		
	}
	
	public static ResponseDataHandle getInstance() {
		return responseDataHandle;
	}
	
	/**
	 * 输出的是json
	 */
	public void buildResp(Object obj,YHandleContent content) throws SystemException {
		ApiMapping apiMapping = content.getApiRunnable().getApiMapping();
		if (StringUtils.equals(apiMapping.outPutType(), Const.RETURN_TYPE_JSON)) {
			//输出json
			JSONObject json = createSesion(JSON.toJSONString(obj), content);
			
			//如果是加密接口，返回的参数需aes加密
			String result;
			if (apiMapping.isDataEncrypt()) {
				String aesJson = AESUtils.encryptData(content.getAesKey(), JSON.toJSONString(json));
				result = buildRespJsonData(aesJson);
			}else {
				result = buildRespJsonData(json);
			}
			
			buildResp(result, content.getResponse(), content.getApiRunnable().getApiMapping());
		}else {
			//输出html
			//检测是否更新或创建会话
			String json = JSON.toJSONString(obj);
			JSONObject result = createSesion(json, content);
			buildRespHtml(result, content);
		}
		
	}
	
	/**
	 * 输出的是freemarker模板的html
	 * @throws SystemException 
	 */
	public void buildRespHtml(Object obj,YHandleContent content) throws SystemException {
		Configuration cfg = content.getCfg();
		if (cfg == null) {
			log.error("========freemarker模板初始化配置为空========");
			throw new SystemException(SystemTrans.CD_FREEMARKER_CONF_ERROR, SystemTrans.MSG_CONF_ERROR);
		}
		HttpServletResponse response = content.getResponse();
		log.info("========返回html格式的数据到前端==============");
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		ApiMapping apiMapping = content.getApiRunnable().getApiMapping();
		if (apiMapping.isCrossDomain()) {
        	log.info("========添加了允许跨域的操作==============");
        	response.setHeader("Access-Control-Allow-Origin", "*");  
    		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
    		response.setHeader("Access-Control-Max-Age", "3600");  
    		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		}
		try {
			Template template = cfg.getTemplate(apiMapping.freemarkerTempleName());
			template.process(obj, response.getWriter());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SystemException(SystemTrans.CD_FREEMARKER_PROCESS_ERROR,SystemTrans.CD_FREEMARKER_PROCESS_ERROR);
		}
	}
	
	/**
	 *输出的是json格式的数据 
	 */
	public void buildResp(String resJson,HttpServletResponse response,ApiMapping apiMapping) {
		PrintWriter out = null;
        try{
            response.setCharacterEncoding("UTF-8");
            //是否需要防止跨域
            if (apiMapping != null && apiMapping.isCrossDomain()) {
            	log.info("========添加了允许跨域的操作==============");
            	response.setHeader("Access-Control-Allow-Origin", "*");  
        		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
        		response.setHeader("Access-Control-Max-Age", "3600");  
        		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			}
            log.info("========返回json格式的数据到前端==============");
        	response.setContentType("application/json;charset=utf-8");  
            response.setHeader("Cache-Control","no-cache");
            response.setHeader("Pragma","no-cache");
            response.setDateHeader("Expires",0);
            out = response.getWriter();
            out.write(resJson);
            log.info("返回数据===================>：{}",resJson);
        } catch (IOException e) {
            System.out.println("服务中心响应异常"+e);
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
	} 
	
	/**
	 * 查询是否需要创建会话或者保存会话 
	 * @throws SystemException 
	 */
	private JSONObject createSesion(String obj,YHandleContent content) throws SystemException {
		//首先查询返回的参数是否有sessionModel，若有则提取出来设置
		SessionModel sessionModel = null;
		JSONObject jsonObject = JSONObject.parseObject(obj);
		Class<?> returnclass = content.getApiRunnable().getTargetMethod().getReturnType();
		Field[] fields = returnclass.getDeclaredFields();//获取所有的属性值
		for (int i = 0; i < fields.length; i++) {
			String name = fields[i].getName(); // 获取属性的名字
            String type = fields[i].getGenericType().toString(); // 获取属性的类型
            if (type.equals("class Yplat.model.SessionModel")) {
				try {
					Object object = jsonObject.get(name);
					if (object == null) {
						break;
					}
					sessionModel = JSON.parseObject(object.toString(), SessionModel.class);
					ApiMapping apiMapping = content.getApiRunnable().getApiMapping();	
					//是否创建会话
					if (apiMapping.createSession()) {//创建会话
						//创建会话后需要生成一对rsa密钥，给接口加密用。私钥保存在会话中，公钥传到前端去
						/*Map<String, String> rsaKeyPair = RSAUtils.getKeys();
						sessionModel.setRsaPrivateKey(rsaKeyPair.get("privateKey"));*/
						//上述的分配rsa密钥的工作交给开发者自己，平台只负责根据注解决定是否创建会话
						YSession session = content.setSessionModel(sessionModel, apiMapping.createSession());
						//把会话id放入cookier供前端使用
						Cookie cookie = new Cookie(Const.SESSIONID_KEY, session.getSessionID());
						content.getResponse().addCookie(cookie);
					}else {//更新会话，但不能更新会话中的私钥
						SessionModel oldSession = content.getSessionModel();
						sessionModel.setRsaPrivateKey(oldSession.getRsaPrivateKey());
						content.setSessionModel(sessionModel, apiMapping.createSession());
					}
					//是否返回会话
					if (!apiMapping.returnSession()) {//不返回会话
						//一般不能将会话返回,故将返回的sessionModel置为null
						jsonObject.remove(name);
					}
					return jsonObject;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("=======获取返回参数中获取定义的sessionModel失败=======");
					throw new SystemException(SystemTrans.CD_PARSE_SESSION_ERROR, SystemTrans.MSG_PARSE_SESSION_ERROR+";原因："+e.getMessage());
				}  
			}
		}
		return jsonObject;
	}

	/**
	 * 返回json格式的数据流 
	 */
	private String buildRespJsonData(Object obj) {
		ResponseHeadModel responseHeadModel = new ResponseHeadModel(BussTrans.SUCCESS_CODE, BussTrans.MSG_SUCCESS_CODE, RandomUtil.getRandomSequence(16));
		ResponseModel responseModel = new ResponseModel(responseHeadModel,obj);
		return JSONObject.toJSONString(responseModel);
	}
	
	/**
	 * 输出错误的json或错误页面 
	 */
	public void buildErrorData(String errorCode,String errorMsg,YHandleContent content) {
		if (content.getApiRunnable() == null) {
			ResponseHeadModel responseHeadModel = new ResponseHeadModel(errorCode, errorMsg, RandomUtil.getRandomSequence(16));
			ResponseModel responseModel = new ResponseModel(responseHeadModel,new Object());
			buildResp(JSONObject.toJSONString(responseModel), content.getResponse(), null);
		}
		ApiMapping apiMapping = content.getApiRunnable().getApiMapping();
		if (StringUtils.equals(apiMapping.outPutType(), Const.RETURN_TYPE_JSON)) {
			ResponseHeadModel responseHeadModel = new ResponseHeadModel(errorCode, errorMsg, RandomUtil.getRandomSequence(16));
			ResponseModel responseModel = new ResponseModel(responseHeadModel,new Object());
			buildResp(JSONObject.toJSONString(responseModel), content.getResponse(), apiMapping);
		}else {
			//输出html
			Map<String, String> ret = new HashMap<>();
			ret.put("errorCode", errorCode);
			ret.put("errorMsg", errorMsg);
			Configuration cfg = content.getCfg();
			HttpServletResponse response = content.getResponse();
			try {
				if (cfg == null) {
					log.error("========freemarker模板初始化配置为空========");
					response.sendError(500,"freemarker模板初始化配置为空");
				}
				log.info("========返回html格式的数据到前端==============");
				response.setContentType("text/html;charset=utf-8");
				response.setCharacterEncoding("utf-8");
				Template template = cfg.getTemplate("error/error.ftl");
				template.process(ret, response.getWriter());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
	
	public void buildErrorJsonData(String errorCode,String errorMsg,HttpServletResponse response,ApiMapping apiMapping) {
		ResponseHeadModel responseHeadModel = new ResponseHeadModel(errorCode, errorMsg, RandomUtil.getRandomSequence(16));
		ResponseModel responseModel = new ResponseModel(responseHeadModel,new Object());
		buildResp(JSONObject.toJSONString(responseModel), response, apiMapping);
	}
}
