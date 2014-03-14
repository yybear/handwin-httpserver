package cn.v5.framework.validate;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @author qgan
 * @version 2014年3月14日 下午3:10:32
 */
public class ValidateExceptionAdvice {
	private static final Logger log = LoggerFactory.getLogger(ValidateExceptionAdvice.class);
	public void afterThrowingValidationException(ValidationException e) throws Exception {
		log.debug("e type is {}", e.getClass());
		if(e instanceof ConstraintViolationException) {
			//.... 提示参数错误
		}
		
	}
}
