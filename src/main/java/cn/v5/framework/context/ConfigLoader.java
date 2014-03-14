package cn.v5.framework.context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import cn.v5.framework.Constants;

/** 
 * 读取系统配置
 * @author qgan
 * @version 2013年12月16日 上午11:50:33
 */
public class ConfigLoader {
	private static final Logger LOG = Logger.getLogger(ConfigLoader.class.getName());
	
	private static final String CONFIG_FILE_NAME = "handwin.properties";
	private static ConfigLoader loader = new ConfigLoader();
	
	public static void load(final String... locations) {
		ConfigLoader al = new ConfigLoader();
        al.loadConfig(locations);
    }
	
	public void loadConfig(final String... locations) {
		Resource res = new UrlResource(ConfigLoader.class.getResource("/META-INF/" + CONFIG_FILE_NAME));
		Properties result = new Properties();
        PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
		if (res.exists()) {
            try {
            	loader.loadProperties(res, result, propertiesPersister);
            } catch (IOException e) {
                LOG.warning("Could not load properties from " + res + ": " + e.getMessage());
            }
        } else {
            LOG.info("Load any global config error,config file not found");
        }
		
		Enumeration names = result.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            setConfigProperty(name, result.getProperty(name));
        }
	}
	
	private void loadProperties(Resource res, Properties result, PropertiesPersister propertiesPersister) throws IOException {
        InputStream is = null;
        try {
            is = res.getInputStream();
            propertiesPersister.load(result, new InputStreamReader(is, Constants.DEFAULT_CHARSET));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
	
	protected String getConfigProperty(String key) {
        return System.getProperty(key);
    }
	
	protected void setConfigProperty(String key, String value) {
        if (getConfigProperty(key) == null) {
            LOG.info("Set global property [" + key + " = " + value + "]");
            System.setProperty(key, value);
        } else {
            LOG.info("Global property exist [" + key + " = " + value + "],skip");
        }
    }
}
