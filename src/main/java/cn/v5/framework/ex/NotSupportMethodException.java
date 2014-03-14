package cn.v5.framework.ex;
/** 
 * @author qgan
 * @version 2014年1月28日 下午3:03:01
 */
public class NotSupportMethodException extends RuntimeException {
	private static final long serialVersionUID = 9137114281715317279L;

	public NotSupportMethodException(String msg) {
		super(msg);
	}
}
