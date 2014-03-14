package cn.v5.framework.message;

import java.util.Locale;

import org.springframework.context.MessageSource;

/** 
 * @author qgan
 * @version 2013年12月18日 下午2:07:17
 */
public class NLS {
	private static MessageSource msgSource;

    public void setMessageSource(MessageSource msgSource) {
        NLS.msgSource = msgSource;
    }
    
    public static String getMessage(String key) {
        return getMessage(key, null, null, null);
    }

    public static String getMessage(String key, Object[] args) {
        return getMessage(key, args, null, null);
    }

    public static String getMessage(String key, Object[] args, String defaultMessage) {
        return getMessage(key, args, defaultMessage, null);
    }

    public static String getMessage(String key, Object[] args, String defaultMessage, Locale locale) {
        return msgSource.getMessage(key, args, defaultMessage, locale);
    }
}
