package cn.v5.util;

import java.util.Properties;

/** 
 * @author qgan
 * @version 2014年2月25日 下午2:32:50
 */
public class ConfigUtils {
	private static Properties appConfig;
	
	public static void setAppConfig(Properties appConfig) {
		ConfigUtils.appConfig = appConfig;
	}

	public static String getString(String key) {
		return appConfig.getProperty(key);
	}
}
