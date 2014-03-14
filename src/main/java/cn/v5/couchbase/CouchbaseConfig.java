package cn.v5.couchbase;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
//import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
//import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

/** 
 * @author qgan
 * @version 2014年2月22日 下午2:43:39
 */
//@Configuration
//@EnableCouchbaseRepositories("cn.v5.couchbase.dao")
public class CouchbaseConfig /*extends AbstractCouchbaseConfiguration*/ {
	private String bucketName = "default";
	private String bucketPassword = "";
	private String[] bootstrapHosts;
	
	/*@Override
	protected List<String> bootstrapHosts() {
		return Arrays.asList(bootstrapHosts);
	}

	@Override
	protected String getBucketName() {
		return bucketName;
	}

	@Override
	protected String getBucketPassword() {
		return bucketPassword;
	}*/

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public void setBucketPassword(String bucketPassword) {
		this.bucketPassword = bucketPassword;
	}

	public void setBootstrapHosts(String[] bootstrapHosts) {
		this.bootstrapHosts = bootstrapHosts;
	}
}
