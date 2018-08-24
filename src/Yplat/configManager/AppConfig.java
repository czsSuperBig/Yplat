package Yplat.configManager;

import java.io.File;


/**
 * 不会变或者高频访问的建议改成常量.
 * 那些可能会动态改变的 还是用方法调用.不然需要重启才会生效.
 * @author zhangcq
 * @date Jan 5, 2017
 * @comment
 */
public class AppConfig
{
	//报文字符集.
	public static String PACK_CHARSET = "utf-8";
	
	//是否开启防重检查.
	public static boolean CHK_REPEAT = ConfigManager.getInstance().getSystemConfig().getBoolean("app.check.repeat");
	
	//session类型. 200-HTTP 300-REDIS.
	public static int SESSION_TYPE = ConfigManager.getInstance().getSystemConfig().getInt("session.type");

	//是否是测试环境.
	public static boolean IS_TEST = ConfigManager.getInstance().getSystemConfig().getBoolean("app.test");

	//app名字.
	public static String APP_NAME = ConfigManager.getInstance().getSystemConfig().getString("app.name");
	
}
