package Yplat.core;

import Yplat.apidoc.ApiDataHandle;
import Yplat.cache.redis.RedisConnectionUtil;
import Yplat.common.SystemTrans;
import Yplat.configManager.ConfigManager;

import Yplat.core.ApiStore.ApiRunnable;
import Yplat.core.handle.BussiHandle;
import Yplat.core.handle.QueryRequstData;
import Yplat.core.handle.RequestDataHandle;
import Yplat.core.handle.ResponseDataHandle;
import Yplat.exception.SystemException;
import Yplat.model.RequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
/**
 * Created by ch on 2018/1/20.
 */
@Service
public class ApiGatewayHand implements InitializingBean,ApplicationContextAware{
    private static final Logger log = LoggerFactory.getLogger(ApiGatewayHand.class);
   

    private ApiStore apiStore;

    public void setApplicationContext(ApplicationContext context) throws BeansException{
        //获取spring上下文，并且初始化apiStore
    	apiStore = new ApiStore(context);
    }

    public void afterPropertiesSet(){
        //获取 ApiMapping注解的方法，并且注册到map中，供后续请求映射到对于的service方法用
    	apiStore.loadApiFromSpringBeans();
    	//初始化自定义的property文件中的参数
    	//ConfigManager.getInstance();
    	//初始化redis
    	RedisConnectionUtil.getInstance();
    }

        
    /**
     * 利用spring线程池异步处理请求 
     * @throws SystemException 
     * @throws UnsupportedEncodingException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
    public void asyncHandler(YHandleContent content) throws UnsupportedEncodingException, SystemException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	//获取请求的参数
    	RequestModel requestModel = QueryRequstData.getInstance().readHttpData(content.getRequest());
    	
    	//最开始判断请求是否是请求api文档的
    	boolean isOpenApiDoc = ConfigManager.getInstance().getSystemConfig().getBoolean("app.api.open");
        if (isOpenApiDoc) {
			if (ApiDataHandle.getInstance().getApiDocData(content)) {
				return ;
			}
		}
        
    	String bussinCode = requestModel.getHead().getBussCode();
    	
    	if (StringUtils.isEmpty(bussinCode)) {
			log.warn("error:传人的业务代码为空");
			throw new SystemException(SystemTrans.CD_BUSSINESS_CODE_ERROR, SystemTrans.MSG_BUSSINESS_CODE_ERROR);
		}
    	
    	//通过业务代号查找对应的映射
    	ApiRunnable api = apiStore.findApiRunnable(bussinCode);
    	
    	if (api == null) {
    		log.warn("error:业务码：{},查询不到对应的映射方法",bussinCode);
			throw new SystemException(SystemTrans.CD_BUSSINESS_CODE_ERROR, SystemTrans.MSG_BUSSINESS_CODE_ERROR);
		}
    	
    	content.setApiRunnable(api);
    	
    	//做一些在注解中自定义的处理 比如会话检测、解密数据、请求防重等
    	String bodyStr = RequestDataHandle.getInstance().getRequestDataBody(content, requestModel);
    	
    	//利用反射执行对应的业务方法
    	Object obj = BussiHandle.getInstance().handleBuss(bodyStr, content);
    	
    	
    	//把数据输出
    	ResponseDataHandle.getInstance().buildResp(obj, content);
    }
    
    

}






/*
 *  private static final String METHOD = "method";
    private static final String PARAMS = "params";
    
    private final ParameterNameDiscoverer parameterNameDiscoverer;


    public ApiGatewayHand(){
        parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    }
    
    
 public void handle(HttpServletRequest request,HttpServletResponse response){
    //参数验证
    String method = request.getParameter(METHOD);
    String params = request.getParameter(PARAMS);
    Object result;
    ApiRunnable apiRun;
    try{
        apiRun = sysParamsValidate(request);
        log.info("请求接口:{},参数:{}",method,params);
        Object[] args = buildParams(apiRun,params,request,response);
        result = apiRun.run(args);
    } catch (ApiException e) {
        response.setStatus(500);
        log.error("调用接口:{} 异常,{}",method,e.getMessage());
        result = handleError(e);
    }catch (InvocationTargetException e){
        response.setStatus(500);
        log.error("调用接口异常"+method+"参数"+params+e.getTargetException());
        result = handleError(e.getTargetException());
    }catch (Exception e){
        response.setStatus(500);
        log.error("其他异常"+method+"参数"+params);
        e.printStackTrace();
        result = handleError(e);
    }

    returnResult(result,response);
}


public Object handleError(Throwable throwable){
    return  new CommonError("500" , throwable.getMessage());
}



public Object[] buildParams(ApiRunnable run, String paramJson, HttpServletRequest request,
                          HttpServletResponse response) throws ApiException{
    Map<String,Object> map;
    try{
        map = UtilJson.toMap(paramJson);
    }catch (IllegalArgumentException e){
        throw new ApiException("调用失败：json字符串格式异常，请检查params参数");
    }
    if(map == null){
        map = new HashMap<>();
    }

    Method method =run.getTargetMethod();
    List<String> paramNames = Arrays.asList(parameterNameDiscoverer.getParameterNames(method));

    Class<?>[] paramsTypes = method.getParameterTypes();

    for (Map.Entry<String, Object> m : map.entrySet()) {
        if(!paramNames.contains((m.getKey()))){
            throw new ApiException("调用失败：接口不存在 '"+m.getKey()+"'参数");
        }
    }
    Object[] args = new Object[paramsTypes.length];
    for(int i=0;i<paramsTypes.length; i++){
        if(paramsTypes[i].isAssignableFrom(HttpServletRequest.class)){
            args[i] = request;
        }else if(map.containsKey(paramNames.get(i))){
            try{
                args[i] = convertJsonToBean(map.get(paramNames.get(i)),paramsTypes[i]);
            }catch (Exception e){
                e.printStackTrace();
                throw new ApiException("参数：" + paramNames.get(i) +" 错误，原因："+e.getMessage());
            }

        }
    }
    return args;
}

//TODO
public Object convertJsonToBean(Object obj,Class<?> clazz){
    log.info("obj=={},{}",obj,obj.getClass().getName());
    return UtilJson.convertValue(obj,clazz);
}



public ApiRunnable sysParamsValidate(HttpServletRequest request) throws ApiException{
    String apiName = request.getParameter(METHOD);
    String  json = request.getParameter(PARAMS);

    ApiRunnable api;
    if(StringUtils.isEmpty(apiName)){
        throw new ApiException("调用失败：参数 method 为空");
    }else if (StringUtils.isEmpty(json)){
        throw new ApiException("调用失败：参数 params 为空");
    }else if((api = apiStore.findApiRunnable(apiName)) == null){
        throw new ApiException("调用失败：指定api不存在 "+apiName);
    }
    return api;
}

//205
private void returnResult(Object result , HttpServletResponse response){
    PrintWriter out = null;
    try{
        UtilJson.JSON_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,true);
        String json = UtilJson.writeValuesAsString(result);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html/json;charset=utf-8");
        response.setHeader("content-type", "text/json");   //返回数据为json格式
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
        if(json != null){
            out = response.getWriter();
            out.write(json);
        }
        //log.info("返回信息：{}",json);
    } catch (IOException e) {
        System.out.println("服务中心响应异常"+e);
        throw new RuntimeException(e);
    } finally {
        if (out != null) {
            out.close();
        }
    }
}*/

