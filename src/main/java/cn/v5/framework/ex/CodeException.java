package cn.v5.framework.ex;
/** 
 * 带有errno的异常
 * @author qgan
 * @version 2013年12月23日 下午7:20:37
 */
public class CodeException extends RuntimeException {
	private static final long serialVersionUID = 4595549517180869921L;
	public static final int SERVER_ERROR = -500;
	private int code = SERVER_ERROR;
	private Object args[];

	private boolean logStackTrace = true;

	private String renderedMessage;

	public CodeException() {
	}

	public CodeException(String message) {
		super(message);
	}

	public CodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodeException(Throwable cause) {
		super(cause);
	}

	public CodeException(int code) {
		super();
		this.code = code;
	}

	public CodeException(int code, Throwable cause) {
		this(cause);
		this.code = code;
	}

	public CodeException(int code, String defaultMessage, Object... args) {
		super(defaultMessage);
		this.code = code;
		this.args = args;
	}

	public CodeException(int code, String defaultMessage, Throwable cause,
			Object... args) {
		super(defaultMessage, cause);
		this.code = code;
		this.args = args;
	}

	public int getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args;
	}

	@Override
	public String getMessage() {
		if (renderedMessage == null) {
			renderedMessage = ExceptionUtils.buildMessage(code, args,
					super.getMessage(), getCause());
		}
		return renderedMessage;
	}

	@Override
	public String toString() {
		return getMessage();
	}

	public static CodeException fromRoot(Exception e) {
		return new CodeException(ExceptionUtils.getRootCause(e));
	}

	public void setRenderedMessage(String renderedMessage) {
		this.renderedMessage = renderedMessage;
	}

	public boolean isLogStackTrace() {
		return logStackTrace;
	}

	public void setLogStackTrace(boolean logStackTrace) {
		this.logStackTrace = logStackTrace;
	}

	public CodeException loggable(boolean loggable) {
		this.setLogStackTrace(loggable);
		return this;
	}
}
