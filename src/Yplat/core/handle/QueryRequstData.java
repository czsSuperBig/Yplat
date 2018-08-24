package Yplat.core.handle;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import Yplat.common.SystemTrans;
import Yplat.exception.SystemException;
import Yplat.model.RequestModel;

/**
 * 从reqest中读取数据（不加解密） 
 */
public class QueryRequstData {
	
	private static final Logger log = LoggerFactory.getLogger(QueryRequstData.class);

	private static QueryRequstData queryRequstData = new QueryRequstData();
	
	private QueryRequstData() {}
	
	public static QueryRequstData getInstance() {
		return queryRequstData;
	}
	
	public RequestModel readHttpData(HttpServletRequest request) throws UnsupportedEncodingException, SystemException {
		String data = queryData(request);
		log.info("请求的参数数据======>{}",data);
		if (StringUtils.isEmpty(data)) {
			log.warn("请求的参数数据为空");
			if (StringUtils.isNotEmpty(request.getParameter("doc"))) {
				return new RequestModel();
			}
			throw new SystemException(SystemTrans.CD_DATANULL_ERROR, SystemTrans.MSG_DATANULL_ERROR);
		}
		RequestModel requestModel = null;
		try {
			requestModel = JSON.parseObject(data, RequestModel.class);
		} catch (Exception e) {
			log.warn("JSON转换请求数据失败，失败原因=====>{}"+e.getMessage());
			throw new SystemException(SystemTrans.CD_DATATRANSFER_ERROR, SystemTrans.MSG_DATATRANSFER_ERROR);
		}
		return requestModel;
	}
	
	/**
	 * 从post请求中获取数据 
	 * @throws UnsupportedEncodingException 
	 * @throws SystemException 
	 */
	private String queryData(HttpServletRequest request) throws UnsupportedEncodingException, SystemException {
		String result;
		//判断是否是ajax请求
		if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
			return new String(readInputBytes__(request),"UTF-8");
		}
		result = request.getParameter("data");
		if (StringUtils.isEmpty(result)) {
			return result;
		}
		if (!(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(result))) {
			result = new String(result.getBytes("ISO-8859-1"),"UTF-8");
			log.info("转码后的result====="+result);
		}
		return result;
	}
	
	/**
	 * 从servlet中读取数据，不支持chunkedStream模式.
	 * @comment 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws JSystemException 
	 */
	private byte[] readInputBytes__( HttpServletRequest request ) throws SystemException
	{
		int cl = request.getContentLength();		
		
		if ( cl == 0 || request.getMethod().equalsIgnoreCase("GET"))
		{
			return "".getBytes();
		}
		
		//chunked?
		if ( cl < 0 )
		{
			log.error("content-length not found,无法获取报文长度,transfer-coding="+request.getHeader("transfer-coding"));
			throw new SystemException(SystemTrans.CD_APPCONN_ERROR,SystemTrans.MSG_APPCONN_ERROR+"t=c");
		}

		try
		{
			ByteArrayOutputStream bous = new ByteArrayOutputStream();
			byte[] indata = new byte[3072];	//3k;
			
			BufferedInputStream ins = new BufferedInputStream(request.getInputStream());
			int totalLen = 0;
			while (true)
			{
				// 暂未做超时处理.
				log.info("SAVAILABLE:"+ins.available());
				
				int len = ins.read(indata);
				//EOF
				if ( len == -1 || totalLen == cl )
				{
					log.info(String.format("__FINAL_READ:fl_len=%d,tl_len=%d,cl_len=%d",len,totalLen,cl));
					break;
				}
				
				log.info("__ONE_READ:rd_len="+len);

				totalLen += len;
				bous.write(indata, 0, len);
			}

			ins.close();
			if (cl != bous.size())
			{
				log.error("__READ_ERROR,contentLength=" + cl+",readlen="+ bous.size());
				return "".getBytes();
			}

			bous.close();
			return bous.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		throw new SystemException(SystemTrans.CD_APPCONN_ERROR,SystemTrans.MSG_APPCONN_ERROR);
	}
}
