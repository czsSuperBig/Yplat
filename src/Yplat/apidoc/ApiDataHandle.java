package Yplat.apidoc;
/**
 * api文档的储存和获取类 
 */

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Yplat.annotation.ApiDescribe;
import Yplat.annotation.ApiMapping;
import Yplat.apidoc.model.ApiDataModel;
import Yplat.apidoc.model.ApiFieldModel;
import Yplat.apidoc.model.ApiStoreModel;
import Yplat.common.SystemTrans;
import Yplat.core.YHandleContent;
import Yplat.exception.SystemException;
import Yplat.model.SessionModel;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ApiDataHandle {

	private static final Logger logger = LoggerFactory.getLogger(ApiDataHandle.class);
	
    private static ConcurrentHashMap<String, ApiStoreModel> apiDocMap = new ConcurrentHashMap<>();
    
    private static ApiDataHandle apiDataHandle = new ApiDataHandle();
    
    private ApiDataHandle() {
    	
    }
    
    public static ApiDataHandle getInstance() {
    	return apiDataHandle;
    }
    
    /**
     * api字段数据放入map中储存 
     */
    public void setApiDocData(Method m,ApiMapping apiMapping) {
    	logger.info("==========正在设置api文档数据============");
    	Class<?>[] paramsTypes = m.getParameterTypes();
    	
    	ApiStoreModel apiStoreModel = new ApiStoreModel();
    	apiStoreModel.setDescribe(apiMapping.describe());
    	apiStoreModel.setEnCode(apiMapping.isDataEncrypt());
        for (int i = 0; i < paramsTypes.length; i++) {
			Class<?> para = paramsTypes[i];
			if (para == SessionModel.class) {
				continue;
			}
			List<ApiDataModel> dataList = new ArrayList<>();
			Field[] fields = para.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				if (fields[j].getType() == SessionModel.class) {
					continue;
				}
				ApiDataModel apiDataModel = null;
				ApiDescribe apiDescribe = fields[j].getAnnotation(ApiDescribe.class);
				if (apiDescribe != null) {
					ApiFieldModel apiFieldModel = new ApiFieldModel(apiDescribe.describe(), apiDescribe.length(), apiDescribe.required());
					apiDataModel = new ApiDataModel(fields[j].getName(), apiFieldModel,fields[j].getType().getName());
				}else {
					apiDataModel = new ApiDataModel(fields[j].getName(), null,fields[j].getType().getName());
				}
				if (fields[j].getType() == List.class) {
					// 如果是List类型，得到其Generic的类型  
					Type genericType = fields[j].getGenericType(); 
					if(genericType == null) continue;  
					// 如果是泛型参数的类型   
					if(genericType instanceof ParameterizedType){   
						ParameterizedType pt = (ParameterizedType) genericType;
						//得到泛型里的class类型对象  
						Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
						if (!(genericClazz == String.class || genericClazz == Integer.class || genericClazz == Boolean.class)) {
							Field[] innerFields = genericClazz.getDeclaredFields();
							List<ApiDataModel> innerList = new ArrayList<>();
							for (int k = 0; k < innerFields.length; k++) {
								ApiDataModel innerApiDataModel = null;
								ApiDescribe innerApiDescribe = innerFields[k].getAnnotation(ApiDescribe.class);
								if (innerApiDescribe != null) {
									ApiFieldModel apiFieldModel = new ApiFieldModel(innerApiDescribe.describe(), innerApiDescribe.length(), innerApiDescribe.required());
									innerApiDataModel = new ApiDataModel(innerFields[k].getName(), apiFieldModel,innerFields[k].getType().getName());
								}else {
									innerApiDataModel = new ApiDataModel(innerFields[k].getName(), null,innerFields[k].getType().getName());
								}
								innerList.add(innerApiDataModel);
							}
							apiDataModel.setList(innerList);
						}
					}
				}
				dataList.add(apiDataModel);
			}
			apiStoreModel.setReqBody(dataList);
			break;
		}
        Field[] returnField = m.getReturnType().getDeclaredFields();
        List<ApiDataModel> backDataList = new ArrayList<>();
        for (int i = 0; i < returnField.length; i++) {
        	if (returnField[i].getType() == SessionModel.class) {
				continue;
			}
        	ApiDataModel apiDataModel = null;
			ApiDescribe apiDescribe = returnField[i].getAnnotation(ApiDescribe.class);
			if (apiDescribe != null) {
				ApiFieldModel apiFieldModel = new ApiFieldModel(apiDescribe.describe(), apiDescribe.length(), apiDescribe.required());
				apiDataModel = new ApiDataModel(returnField[i].getName(), apiFieldModel,returnField[i].getType().getName());
			}else {
				apiDataModel = new ApiDataModel(returnField[i].getName(), null,returnField[i].getType().getName());
			}
			
			if (returnField[i].getType() == List.class) {
				// 如果是List类型，得到其Generic的类型  
				Type genericType = returnField[i].getGenericType(); 
				if(genericType == null) continue;  
				// 如果是泛型参数的类型   
				if(genericType instanceof ParameterizedType){   
					ParameterizedType pt = (ParameterizedType) genericType;
					//得到泛型里的class类型对象  
					Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0]; 
					if (!(genericClazz == String.class || genericClazz == Integer.class || genericClazz == Boolean.class)) {
						Field[] innerFields = genericClazz.getDeclaredFields();
						List<ApiDataModel> innerList = new ArrayList<>();
						for (int k = 0; k < innerFields.length; k++) {
							ApiDataModel innerApiDataModel = null;
							ApiDescribe innerApiDescribe = innerFields[k].getAnnotation(ApiDescribe.class);
							if (innerApiDescribe != null) {
								ApiFieldModel apiFieldModel = new ApiFieldModel(innerApiDescribe.describe(), innerApiDescribe.length(), innerApiDescribe.required());
								innerApiDataModel = new ApiDataModel(innerFields[k].getName(), apiFieldModel,innerFields[k].getType().getName());
							}else {
								innerApiDataModel = new ApiDataModel(innerFields[k].getName(), null,innerFields[k].getType().getName());
							}
							innerList.add(innerApiDataModel);
						}
						apiDataModel.setList(innerList);
					}
				}
			}
			
			backDataList.add(apiDataModel);
		}
        apiStoreModel.setRspBody(backDataList);
        apiDocMap.put(apiMapping.value(), apiStoreModel);
    }
    
    /**
     * 获取api文档的列表页或详情页 
     * @throws SystemException 
     */
    public boolean getApiDocData(YHandleContent content) throws SystemException {
    	HttpServletRequest request = content.getRequest();
    	String doc = request.getParameter("doc");
    	if (StringUtils.isEmpty(doc)) {
			return false;
		}
    	HttpServletResponse response = content.getResponse();
    	Configuration cfg = content.getCfg();
    	response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
    	try {
    		if (StringUtils.equalsIgnoreCase(doc, "all")) {//请求列表页数据
    			Map<String, Object> resultMap = new HashMap<>();
        		Template template = cfg.getTemplate("doc/index.ftl");
        		resultMap.put("apiMap", apiDocMap);
    			template.process(resultMap, response.getWriter());
    		}else {
    			Template template = cfg.getTemplate("doc/details.ftl");
    			ApiStoreModel apiStoreModel = apiDocMap.get(doc);
    			template.process(apiStoreModel, response.getWriter());
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new SystemException(SystemTrans.CD_FREEMARKER_PROCESS_ERROR,SystemTrans.CD_FREEMARKER_PROCESS_ERROR);
		}
		
    	
    	return true;
    }
}
