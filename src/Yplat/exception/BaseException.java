package Yplat.exception;

/**
 * 基础异常类，该类主要规定了错误码和错误信息两个值.
 * @author zhangcq
 * @date Nov 28, 2016
 * @comment
 */
public abstract class BaseException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private String errCode;
	private String errMsg;
	
	public BaseException( String retCode, String retMsg )
	{
		super(retMsg);
		this.errCode = retCode;
		this.errMsg = retMsg;
	}
	
	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}
	
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	public String toString()
	{
		return String.format("%s:[%s:%s]", getClass().getName(),errCode,errMsg);
	}
}
