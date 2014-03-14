package cn.v5.mq;


import java.io.IOException;

import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicies;
import net.jodah.lyra.config.RetryPolicy;
import net.jodah.lyra.util.Duration;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/** 
 * @author qgan
 * @version 2014年2月24日 下午2:06:42
 */
public class RabbitMqConnFactory implements InitializingBean, DisposableBean {
	private String host;
	private int port;
	
	private long intervalSec = 1;
	private long maxIntervalSec = 30;
	private long maxDurationMin = 10;
	
	
	private ConnectionOptions options;
    private Config config;
	
	@Override
	public void destroy() throws Exception {
		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		options = new ConnectionOptions().withHost(host).withPort(port);
		config = new Config()
        .withRecoveryPolicy(RecoveryPolicies.recoverAlways())
        .withRetryPolicy(new RetryPolicy()
                .withBackoff(Duration.seconds(intervalSec), Duration.seconds(maxIntervalSec))
                .withMaxDuration(Duration.minutes(maxDurationMin)));
	}
	
	public Connection createConnection() throws IOException {
        return Connections.create(options, config);
    }

    public Channel createChannel(Connection connection) throws IOException {
        Channel channel = connection.createChannel();
        return channel;
    }

    public void exchangeDeclare(Channel channel,String exchange,String type) throws IOException {
        channel.exchangeDeclare(exchange,type);
    }

    public Channel createChannel(Connection connection,String exchanges) throws IOException {
        Channel channel = connection.createChannel();
        exchangeDeclare(channel,exchanges);
        return channel;

    }

    private void exchangeDeclare(Channel channel,String exchangeAndTypes) throws IOException {
       String[] exchangeArray = exchangeAndTypes.split("~");
       for(String exchange:exchangeArray){
           String[] exchangeAndType = exchange.split(",");
           if(exchangeAndType.length != 2){
               throw new IllegalArgumentException("exchanges:"+exchangeAndTypes+" has wrong fromat.");
           }
           channel.exchangeDeclare(exchangeAndType[0],exchangeAndType[1]);
       }
    }
	
	public long getIntervalSec() {
		return intervalSec;
	}
	public void setIntervalSec(long intervalSec) {
		this.intervalSec = intervalSec;
	}
	public long getMaxIntervalSec() {
		return maxIntervalSec;
	}
	public void setMaxIntervalSec(long maxIntervalSec) {
		this.maxIntervalSec = maxIntervalSec;
	}
	public long getMaxDurationMin() {
		return maxDurationMin;
	}
	public void setMaxDurationMin(long maxDurationMin) {
		this.maxDurationMin = maxDurationMin;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
