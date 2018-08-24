package Yplat.configManager;
import Yplat.exception.SystemException;



/**
 * 父类基础类.该类不应该直接使用和修改.
 * 系统基础信息在JAppconfig中.
 * @author zhangcq
 * @date Jan 5, 2017
 * @comment
 */
public class SystemConfig extends BasePropertiesReader
{	
	//环境切换配置文件.
	private String envFile = "/conf/system/AENV";
	
	//环境配置文件.
//	private static String[] confs = {"conf/system/:ENV:/sys_base","conf/system/:ENV:/sys_env","conf/system/:ENV:/batch"};
	
	public SystemConfig() throws SystemException
	{
		init();
	}
	
	private void init() throws SystemException
	{
		//环境类型
		String envType = getEnvValue(envFile,"envType");
		
		//配置文件列表.
		String[] confs = getEnvValue(envFile,"proFiles").split(",");
		
		loadConfigs(envType,true,confs);
	}
}
