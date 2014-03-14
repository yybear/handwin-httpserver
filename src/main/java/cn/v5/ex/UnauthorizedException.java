package cn.v5.ex;
/** 
 * @author qgan
 * @version 2014年2月20日 下午2:16:19
 */
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 5002930977814652184L;
	
	private String messsage;
	
	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message) {
		super(message);
	}
	
	public String getMessage() {
		return this.messsage;
	}
}
