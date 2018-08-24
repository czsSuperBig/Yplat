package Yplat.configManager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Yplat.exception.SystemException;


/**
 * 配置管理类.
 * @author zhangcq
 * @date Jan 5, 2017
 * @comment
 */
public class ConfigManager
{
	private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
	
	private SystemConfig configEntity;
	
	private ConfigManager()
	{
		init();
	}
	
	private static final class Holer
	{
		private static final ConfigManager config = new ConfigManager();
	}
	/**
	 * 单例模式 
	 */
	public static ConfigManager getInstance()
	{
		return Holer.config;
	}
	
	private void init()
	{
		try {
			configEntity = new SystemConfig();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean reload()
	{
		SystemConfig cnf = null;
		try {
			cnf = new SystemConfig();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		
		if ( cnf != null )
		{
			configEntity = cnf;
			return true;
		}
		
		return false;
	}
	
	public SystemConfig getSystemConfig()
	{
		return configEntity;
	}
	
	/*public static void main(String args[])
	{
		for ( int i = 0; i < 1000; ++i )
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JConfigManager.getInstance().reload();
			XLog.log("--------------------"+JConfigManager.getInstance().getSystemConfig().getString("app.name"));
		}
		
	}*/
}
