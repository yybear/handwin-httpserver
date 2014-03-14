package cn.v5.couchbase;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

//import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;*/

import cn.v5.couchbase.dao.UserRepository;
import cn.v5.model.User;

/**
 * @author qgan
 * @version 2014年2月22日 上午11:00:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext.xml")
@ActiveProfiles("test")
public class CouchbaseTest {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CouchbaseConfig config;

	@Test
	public void testCouchbase() throws Exception {
		/*userRepository.deleteAll();

		User user = new User();
		user.setLastname("Jackson");

		user = userRepository.save(user);*/
		/*userRepository.delete("user1");
		User user2 = config.couchbaseTemplate().findById("user2", User.class);
		System.out.println(user2.getFirstname());

		Query query = new Query(); 
		query.setKey("jiff");
		List<User> allUsers = userRepository.findByLastname(query);
		System.out.println("allUsers size is " + allUsers.size()); 
		User user = allUsers.get(0); System.out.println("user name is " + user.getLastname());*/
	}

	
	public void testBase() throws Exception {
		List<URI> hosts = Arrays.asList(new URI("http://192.168.1.246:8091/pools"));

		// Name of the Bucket to connect to
		String bucket = "default";

		// Password of the bucket (empty) string if none
		String password = "";

		// Connect to the Cluster
		//CouchbaseClient client = new CouchbaseClient(hosts, bucket, password);
		// Store a Document
		/*User user = new User();
		user.setLastname("Jackson");
		ObjectMapper mapper = new ObjectMapper();
		
		client.set("user1", mapper.writeValueAsString(user)).get();*/

		// Retreive the Document and print it
		//System.out.println(client.get("user1"));

		// Shutting down properly
		//client.shutdown();
	}
	
	public void testQueryView() throws Exception {
		List<URI> hosts = Arrays.asList(new URI("http://192.168.1.246:8091/pools"));

		// Name of the Bucket to connect to
		String bucket = "default";

		// Password of the bucket (empty) string if none
		String password = "";

		// Connect to the Cluster
		/*CouchbaseClient client = new CouchbaseClient(hosts, bucket, password);
		
		String designDoc = "name";
		String viewName = "name";
		View view = client.getView(designDoc, viewName);
		Query query = new Query();
		query.setIncludeDocs(true);
		query.setRangeStart("c");
		query.setRangeEnd("c\\uefff");
		
		ViewResponse response = client.query(view, query);
		
		for (ViewRow row : response) {
		  System.out.println(row.getDocument());
		}*/
	}
}
