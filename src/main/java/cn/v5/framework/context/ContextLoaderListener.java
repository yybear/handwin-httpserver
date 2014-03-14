package cn.v5.framework.context;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * @author qgan
 * @version 2013年12月16日 下午2:27:53
 */
public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {
	public static final String PROFILE_ACTIVE = "profile.active";
	public static final String PROFILE_DEV = "dev";
	public static final String PROFILE_TEST = "test";
	public static final String PROFILE_PRODUCTION = "production";
	public static final String FILE_LOG = "filelog.enable";
	

	static Logger logger = Logger.getLogger(ContextLoaderListener.class
			.getName());

	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext arg0) {
		ConfigLoader.load();
		String activeProfile = System.getProperty(PROFILE_ACTIVE);
		
		if(null == activeProfile) {
			activeProfile = PROFILE_DEV;
		}
		
		if ("${profile.active}".equals(activeProfile)) { // 没有配置
            activeProfile = PROFILE_DEV;
        }
		
		System.setProperty(PROFILE_ACTIVE, activeProfile);
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, activeProfile); // 设置spring的profile配置
        
        // 生产环境 log保存到文件中
        if (System.getProperty(FILE_LOG) == null && activeProfile.contains(PROFILE_PRODUCTION)) {
            System.setProperty(FILE_LOG, "true");
        }
        if (!"true".equals(System.getProperty(FILE_LOG))) {
            System.clearProperty(FILE_LOG);
        }
        
        SLF4JBridgeHandler.install(); // 替换jdk log
        
		return super.initWebApplicationContext(arg0);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			super.contextDestroyed(event);
		} finally {
			SLF4JBridgeHandler.uninstall();
		}
	}
}
