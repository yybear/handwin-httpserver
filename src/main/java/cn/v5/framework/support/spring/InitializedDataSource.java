package cn.v5.framework.support.spring;

import java.io.File;

import javax.sql.DataSource;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.StringUtils;

/** 
 * @author qgan
 * @version 2013年12月23日 上午9:54:44
 */
public class InitializedDataSource implements InitializingBean, ResourceLoaderAware {

	public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; \n\r";

	protected final Logger logger = LoggerFactory.getLogger(InitializedDataSource.class);

	private DataSource dataSource;

	private String scriptEncoding = "UTF-8";

	private String[] sqlScripts = ArrayUtils.EMPTY_STRING_ARRAY;
	
	private String sqlScriptsDir = "/WEB-INF/sql/";

	protected ResourceLoader resourceLoader;

	public InitializedDataSource() {
		super();
	}

	public InitializedDataSource(DataSource ds) {
		this();

		this.setDataSource(ds);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setScripts(String scripts) {
		this.sqlScripts = StringUtils.tokenizeToStringArray(scripts, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
	}
	
	public void setScriptsDir(String scriptsDir) {
		this.sqlScriptsDir = scriptsDir;
	}

	public void setScriptEncoding(String scriptEncoding) {
		this.scriptEncoding = scriptEncoding;
	}

	protected void populate() throws Exception {
		logger.info("Initializing DataSource ...");
		for (String script : this.sqlScripts) {
			logger.info("\tpopulating: [" + script + "]");
			try {
				Resource resource = this.resourceLoader.getResource(script);
				if (resource.exists()) {
					DatabasePopulator populator = this.createPopulator(resource);
					DatabasePopulatorUtils.execute(populator, dataSource);
					logger.info("\t\t[DONE].");
				} else {
					logger.warn("\t\t[NOT EXISTS].");
				}
			} catch (Exception ex) {
				logger.warn("\t\t[IGNORE] with [" + ex.getMessage() + "].");
			}
		}
		logger.info("DataSource initialized.");
	}

	protected DatabasePopulator createPopulator(Resource script) {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setContinueOnError(false);
		populator.setIgnoreFailedDrops(false);
		populator.setScripts(new Resource[] { script });
		populator.setSqlScriptEncoding(scriptEncoding);
		return populator;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(ArrayUtils.isEmpty(sqlScripts)) {
			Resource resource = resourceLoader.getResource(sqlScriptsDir);
			File file = resource.getFile();
			if(!file.exists() || !file.isDirectory()) 
				return;
			
			String[] sqls = file.list();
			if(sqls.length > 0) {
				this.sqlScripts = new String[sqls.length];
				
				for(int i = 0; i < sqls.length; i++) {
					sqlScripts[i] = sqls[i];
				}
			}
		}
		
		if (!ArrayUtils.isEmpty(this.sqlScripts))
			this.populate();
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
