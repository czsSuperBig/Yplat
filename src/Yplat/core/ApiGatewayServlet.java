package Yplat.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.support.WebApplicationContextUtils;

import Yplat.common.SystemTrans;
import Yplat.configManager.ConfigManager;
import Yplat.core.handle.ResponseDataHandle;
import Yplat.exception.SystemException;
import Yplat.freemarker.LabelForUrl;
import freemarker.template.Configuration;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Created by Administrator on 2018/1/20.
 */
@WebServlet(name="ApiGatewayServlet",urlPatterns= {"/api"},loadOnStartup=1,asyncSupported=true)
public class ApiGatewayServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6273873422790817982L;
	
	private static Logger logger = LoggerFactory.getLogger(ApiGatewayServlet.class);

	private ApplicationContext context;

    private ApiGatewayHand apiHand;
    
    private ThreadPoolTaskExecutor threadPool;
    
    private Configuration cfg;

    @Override
    public void init() throws ServletException{
        super.init();
        //获取spring上下文
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        apiHand = context.getBean(ApiGatewayHand.class);
        threadPool = context.getBean(ThreadPoolTaskExecutor.class);
        //初始化freemarker模板
        cfg = new Configuration(Configuration.VERSION_2_3_28);
        String path = ConfigManager.getInstance().getSystemConfig().getString("freemarker.temple.path");
        logger.info("============正在加载freemarker模板。模板路径为：{}==============",path);
        cfg.setServletContextForTemplateLoading(getServletContext(), path);
        //添加自定义标签
        cfg.setSharedVariable("url", new LabelForUrl());
    }

    
    
    @Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	AsyncContext asyncCtx = request.startAsync();
    	asyncCtx.setTimeout(100000);
    	asyncCtx.addListener(new AsyncListener() {
			
			@Override
			public void onTimeout(AsyncEvent arg0) throws IOException {
				// TODO Auto-generated method stub
				logger.warn("======================timeOut error=======================");
				ServletResponse response = arg0.getAsyncContext().getResponse();
				ResponseDataHandle responseDataHandle = ResponseDataHandle.getInstance();
				responseDataHandle.buildErrorJsonData(SystemTrans.CD_TIME_OUT_ERROR, SystemTrans.MSG_TIME_OUT_ERROR, (HttpServletResponse)response, null);
			}
			
			@Override
			public void onStartAsync(AsyncEvent arg0) throws IOException {
				// TODO Auto-generated method stub
				logger.info("AppAsyncListener start");
			}
			
			@Override
			public void onError(AsyncEvent arg0) throws IOException {
				// TODO Auto-generated method stub
				logger.warn("===============error on processing=========================");
				ServletResponse response = arg0.getAsyncContext().getResponse();
				ResponseDataHandle responseDataHandle = ResponseDataHandle.getInstance();
				responseDataHandle.buildErrorJsonData(SystemTrans.CD_SYS_ERROR, SystemTrans.MSG_SYS_ERROR, (HttpServletResponse)response, null);
		        
			}
			
			@Override
			public void onComplete(AsyncEvent arg0) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	//使用线程池异步处理请求
    	threadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Instant startTime = Instant.now();
				YHandleContent content = null;
				try {
					content = new YHandleContent((HttpServletRequest)asyncCtx.getRequest(), (HttpServletResponse)asyncCtx.getResponse());
					content.setCfg(cfg);
					apiHand.asyncHandler(content);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					String errorCode;
					String errorMsg;
					if (e instanceof SystemException) {
						errorCode = ((SystemException) e).getErrCode();
						errorMsg = ((SystemException) e).getErrMsg();
					}else {
						errorCode = SystemTrans.CD_SYS_ERROR;
						errorMsg = e.getLocalizedMessage();
					}
					if (StringUtils.isEmpty(errorMsg)) {
						errorMsg = "系统开了个小差，请稍后再试！";
					}
					ResponseDataHandle responseDataHandle = ResponseDataHandle.getInstance();
					responseDataHandle.buildErrorData(errorCode, errorMsg, content);
				}finally {
					asyncCtx.complete();
					logger.info("#########线程{}处理完成本次请求，本次请求的接口代号为：{}，请求时长：{}毫秒############",Thread.currentThread().getName(),content.getApiRunnable()==null?"无代号":content.getApiRunnable().apiName,Duration.between(startTime, Instant.now()).toMillis());
				}
			}
		});
	}
    
}
