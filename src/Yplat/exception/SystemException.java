package Yplat.exception;

/**
 * 框架平台级别错误.比如反射交易方法不存在，加解密出错等.
 * @comment
 */
public class SystemException extends BaseException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7963771513846767281L;
	
	public SystemException(String retCode, String retMsg){
		super(retCode, retMsg);
		// TODO Auto-generated constructor stub
	}
}
