package Yplat.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Yplat.common.Const;

/**
 * http请求处理类 
 */
public class HttpHandleUtil {
	
	private static Logger logger = LoggerFactory.getLogger(HttpHandleUtil.class);

	/**
	 *  从requset里面获取sessionid
	 *  首先从cookier请求头中获取  获取不到 在请求头中获取 
	 */
	public static String obtainSessionid(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if ( cookies != null ) {
			for ( int i = 0; i < cookies.length; ++i ) {
				logger.info("Cookies:"+cookies[i].getName()+"="+cookies[i].getValue());				
				if (StringUtils.equals(Const.SESSIONID_KEY, cookies[i].getName())) {
					return cookies[i].getValue();
				}
			}
		}
		return request.getHeader(Const.SESSIONID_KEY);
	}
}
