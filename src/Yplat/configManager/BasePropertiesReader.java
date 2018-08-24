package Yplat.configManager;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Yplat.common.SystemTrans;
import Yplat.exception.SystemException;

/**
 * 读取.property文件，并将读取的结果放在cmap中，初始化的时候读一次，以后每次property参数在cmap中取
 * 该类的子类或者工具类应该实现为一个单例或者常量类.
 * 不要反复加载配置.
 * 最好用单例实现,并附加一个reload的操作.
 * @comment
 */
public class BasePropertiesReader
{
	
	private static final Logger log = LoggerFactory.getLogger(BasePropertiesReader.class);
	
	private Map<String,String> cMap = new ConcurrentHashMap<String,String>();

	/**
	 * @param envType	环境类型.
	 * @param proName	配置列表.
	 */
	public BasePropertiesReader()
	{
	}
	
	//初始化.
	protected void loadConfigs( String envType,boolean print, String ...proName  ) throws SystemException
	{
		if ( StringUtils.isEmpty(envType) )
		{
			log.warn("ERROR:the envType of resource-files cannot be empty.");
			throw new RuntimeException("the envType cannot be empty.");
		}

		for ( String confpath : proName )
		{
			confpath = confpath.trim();
			if( confpath.contains(":ENV:") )
			{
				confpath = confpath.replaceFirst(":ENV:", envType);
			}

			//match the environment.
			String rc = StringUtils.isEmpty(envType)?confpath:confpath+"_"+envType;

			Properties props = ConfigUtils.loadPropertis(rc);
			log.info("load property config success["+rc+"]");

			Iterator<Entry<Object, Object>> it = props.entrySet().iterator();  
			while (it.hasNext())
			{  
				Entry<Object, Object> entry = it.next();  
				String key = (String)entry.getKey();  
				String value = (String)entry.getValue();

				if( print )
				{
					log.info("property-value:[{} = {} ]", key,value );
				}

				if ( cMap.get(key) != null )
				{
					log.warn("ERROR[{}]", "duplicate config key:"+key);
					throw new SystemException(SystemTrans.CD_CONF_ERROR,SystemTrans.MSG_CONF_ERROR);
				}
				
				cMap.put(key, value );
			}
		}
	}

	public String getString(String key, boolean must )
	{
//		XLog.log("find property-value for key:"+key);
		String value = cMap.get(key);
		if ( must && StringUtils.isEmpty(value) )
		{
			throw new RuntimeException( "WARN,key-property [" + key + "] not found" );
		}

		return value;
	}

	public String getString(String key )
	{
		return getString( key, false );
	}

	public String getString(String key, String defval )
	{
		String retVal = getString(key,false);
		if ( StringUtils.isEmpty(retVal) )
		{
			return defval;
		}

		return retVal;
	}

	public int getInt(String key)
	{
		String cf = getString(key,false);
		if ( StringUtils.isEmpty(cf) )
		{
			return 0;
		}

		return Integer.parseInt(cf);
	}

	public int getInt(String key, boolean must )
	{
		return Integer.parseInt(getString(key,must));
	}

	public int getInt(String key,int defval )
	{
		int retval = getInt(key);
		if (retval==0)
		{
			return defval;
		}

		return retval;
	}

	public boolean getBoolean(String key)
	{
		String value = getString(key,false);
		if ( value != null )
		{
			value = value.trim();
		}

		return "true".equalsIgnoreCase(value);
	}
	
	/**
	 * 获取环境类型 用于加载不同文件.
	 * @author zhangcq
	 * @date Feb 7, 2017
	 * @comment 
	 * @return
	 * @throws JSystemException 
	 */
	protected static String getEnvValue( String envFile,String key)
	{
		try
		{
			Properties props = ConfigUtils.loadPropertis(envFile);
			String envType = props.getProperty(key);
					
			log.info("using enviroment type:"+envType);
			return envType;
		}
		catch (MissingResourceException e)
		{
			log.warn("ERROR:fail to tell the environment type");
			throw e;
		}
	}
}