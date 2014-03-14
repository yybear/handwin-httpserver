package cn.v5.framework.dao.jdbi;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.tweak.ConnectionFactory;
import org.skife.jdbi.v2.tweak.StatementLocator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceUtils;

/** 
 * @author qgan
 * @version 2014年2月26日 下午4:50:59
 */
public class MyDBIFactoryBean<T> implements FactoryBean<T>, InitializingBean
{
    private DataSource dataSource;
    private StatementLocator statementLocator;
    private Map<String, Object> globalDefines = new HashMap<String, Object>();

    /**
     * See org.springframework.beans.factory.FactoryBean#getObject
     */
    public T getObject() throws Exception
    {
        final MyDBI dbi = new MyDBI(new SpringDataSourceConnectionFactory(dataSource));
        if (statementLocator != null) {
            dbi.setStatementLocator(statementLocator);
        }

        for (Map.Entry<String, Object> entry : globalDefines.entrySet()) {
            dbi.define(entry.getKey(), entry.getValue());
        }

        return (T)dbi;
    }

    /**
     * See org.springframework.beans.factory.FactoryBean#getObjectType
     */
    public Class<?> getObjectType()
    {
        return IDBI.class;
    }

    /**
     * See org.springframework.beans.factory.FactoryBean#isSingleton
     *
     * @return false
     */
    public boolean isSingleton()
    {
        return true;
    }

    /**
     * The datasource, which should be managed by spring's transaction system, from which
     * the IDBI will obtain connections
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public void setStatementLocator(StatementLocator statementLocator)
    {
        this.statementLocator = statementLocator;
    }

    public void setGlobalDefines(Map<String, Object> defines) {
        globalDefines.putAll(defines);
    }

    /**
     * Verifies that a dataSource has been set
     */
    public void afterPropertiesSet() throws Exception
    {
        if (dataSource == null) {
            throw new IllegalStateException("'dataSource' property must be set");
        }
    }
}

class SpringDataSourceConnectionFactory implements ConnectionFactory
{
    private final DataSource dataSource;

    public SpringDataSourceConnectionFactory(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public Connection openConnection() throws SQLException
    {
        return DataSourceUtils.getConnection(dataSource);
    }
}