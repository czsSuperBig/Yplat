package Yplat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;

import Yplat.annotation.ApiDescribe;
import Yplat.annotation.ApiMapping;
import Yplat.apidoc.ApiDataHandle;
import Yplat.configManager.ConfigManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ch on 2018/1/20.
 */
public class ApiStore {
	
	private static final Logger log = LoggerFactory.getLogger(ApiStore.class);

    private ApplicationContext applicationContext;
    private ConcurrentHashMap<String,ApiRunnable> apiMap = new ConcurrentHashMap<String, ApiRunnable>();

    public ApiStore(ApplicationContext applicationContext){
        Assert.notNull(applicationContext);
        this.applicationContext = applicationContext;
    }

    public void loadApiFromSpringBeans(){
        String[] names = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        for (String name : names) {
            type = applicationContext.getType(name);
            boolean isOpenApiDoc = ConfigManager.getInstance().getSystemConfig().getBoolean("app.api.open");
            for (Method m : type.getDeclaredMethods()) {
                 ApiMapping apiMapping = m.getAnnotation(ApiMapping.class);
                 if(apiMapping != null){
                     addApiItem(apiMapping,name,m);
                     log.info("建立映射关系===({}--------->{})",apiMapping.value(),m.getDeclaringClass().getName()+"."+m.getName());
                     if (isOpenApiDoc) {
                    	 ApiDataHandle.getInstance().setApiDocData(m, apiMapping);
					 }
                 }
            }
        }
    }

    public void addApiItem(ApiMapping apiMapping,String beanName,Method method){
        ApiRunnable apiRunnable = new ApiRunnable();
        apiRunnable.apiName = apiMapping.value();
        apiRunnable.targetName = beanName;
        apiRunnable.targetMethod = method;
        apiRunnable.apiMapping = apiMapping;

        apiMap.put(apiMapping.value(),apiRunnable);
    }

    public ApiRunnable findApiRunnable(String apiName){
        return apiMap.get(apiName);
    }

    public ApiRunnable findApiRunnable(String apiName,String version){
        return (ApiRunnable) apiMap.get(apiName+"_"+version);
    }

    public List<ApiRunnable> findApiRunnables(String apiName){
        if(apiName == null){
            throw new IllegalArgumentException("api name must not null");
        }
        List<ApiRunnable> list = new ArrayList<>();
        for (ApiRunnable apiRunnable : apiMap.values()) {
            if(apiRunnable.apiName.equals(apiName)){
                list.add(apiRunnable);
            }
        }
        return list;
    }


    public boolean contatinsApi(String apiName){
        return apiName.contains(apiName);
    }

    public class ApiRunnable{
       
		String apiName;
        String targetName;  //ioc bean 名称
        Object target;  //实例
        Method targetMethod;
        ApiMapping apiMapping;

        public Object run(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (target == null){
                target = applicationContext.getBean(targetName);
            }
            return targetMethod.invoke(target,args);
        }

        public Class<?>[] getParamTypes(){
            return targetMethod.getParameterTypes();
        }
        public String getApiName(){
            return apiName;
        }

        public String getTargetName() {
            return targetName;
        }

        public Object getTarget() {
            return target;
        }

        public Method getTargetMethod() {
            return targetMethod;
        }
        
        public ApiMapping getApiMapping() {
			return apiMapping;
		}
    }
}
