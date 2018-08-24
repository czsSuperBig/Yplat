package Yplat.configManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载 .properties配置文件工具类
 */
public class ConfigUtils
{
	private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);
	
	public static Properties loadPropertis( String proPath )
	{
		if ( !proPath.endsWith("properties"))
		{
			proPath = proPath+".properties";
		}
		
		log.info("正在加载配置文件，文件路径:"+proPath);
		
		InputStream is = ConfigUtils.class.getClassLoader().getResourceAsStream(proPath);
		
		if ( is == null )
		{
			System.out.println("--------null");
		}
		
		Properties prop = new Properties();
		try {
			prop.load(is);
//			is.close();

			log.info("加载完成配置文件，文件路径:"+proPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if ( is != null )
			{
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return prop;
	}
}
