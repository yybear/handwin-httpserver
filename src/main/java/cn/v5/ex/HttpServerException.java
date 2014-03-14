package cn.v5.ex;

import cn.v5.framework.ex.ExceptionUtils;

/** 
 * @author qgan
 * @version 2014年2月20日 下午4:38:48
 */
public class HttpServerException extends RuntimeException {
	private static final long serialVersionUID = 4595549517180869921L;
	private Object args[];

	private int errno;
	
	private int status = 500;
	
	private String renderedMessage;

	public HttpServerException() {
		super();
	}

	public HttpServerException(int errno) {
		super();
		this.errno = errno;
	}
	
	public HttpServerException(int status, int errno) {
		super();
		this.status = status;
		this.errno = errno;
	}
	
	public HttpServerException(int errno, Object... args) {
		super();
		this.errno = errno;
		this.args = args;
	}
	
	public HttpServerException(int status, int errno, Object... args) {
		super();
		this.status = status;
		this.errno = errno;
		this.args = args;
	}
	
	public HttpServerException(int status, String renderedMessage) {
		this.status = status;
		this.renderedMessage = renderedMessage;
	}
	
	public HttpServerException(String renderedMessage) {
		this.renderedMessage = renderedMessage;
	}
	
	public int getErrno() {
		return errno;
	}

	public int getStatus() {
		return status;
	}
	
	@Override
	public String getMessage() {
		if (renderedMessage == null) {
			renderedMessage = ExceptionUtils.buildMessage(errno, args,
					super.getMessage(), getCause());
		}
		return renderedMessage;
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
