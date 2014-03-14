package cn.v5.framework.support.jedis;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/** 
 * @author qgan
 * @version 2014年2月26日 下午3:30:15
 */
public class JedisConnectionFactory<T> implements DisposableBean, FactoryBean<T> {
	
	private String hostName;
	private int port;
	private JedisPoolConfig poolConfig;
	
	private JedisPool pool = null;

	@Override
	public void destroy() throws Exception {
		if(pool != null)
			pool.destroy();
	}
	
	@Override
	public T getObject() throws Exception {
		pool = new JedisPool(poolConfig, hostName, port);
		return (T)pool;
	}
	
	@Override
	public Class<?> getObjectType() {
		return JedisPool.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
}
