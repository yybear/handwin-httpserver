package cn.v5.framework.dao.jdbi;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;


/** 
 * @author qgan
 * @version 2014年2月24日 下午7:30:23
 */
public class JdbiDaoFactory<T> extends UrlBasedRemoteAccessor implements FactoryBean<T>, MethodInterceptor {
	@Autowired
	private IDBI dbi;
	
	@Autowired
	private DataSource dataSource;
	
	private Class<?> daoInterface;
	
	public Class<?> getDaoInterface() {
		return daoInterface;
	}

	public void setDaoInterface(Class<?> daoInterface) {
		this.daoInterface = daoInterface;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		Handle h = dbi.open();
		MyDBI myDBI = (MyDBI) dbi;
		T t = (T)myDBI.attach(getDaoInterface(), h);
		DataSourceUtils.releaseConnection(h.getConnection(), dataSource);
		return t;
	}

	@Override
	public Class<?> getObjectType() {
		return getDaoInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		System.out.println("hh");
		return null;
	}
}