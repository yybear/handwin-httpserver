package cn.v5;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.v5.util.RedisUtil;
import cn.v5.util.SystemConstants;
/** 
 * @author qgan
 * @version 2014年2月20日 下午6:45:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext.xml")
@ActiveProfiles("test")
public class RedisTest {
	
	String session = "09df401f395a310706f48acf28055fa7";
	@Test
	public void testRedis() {
		System.out.println("testRedis");
		final String key = SystemConstants.USER_SESSION + session;
		String nameMd5 = "";
		nameMd5 = RedisUtil.get(key);
		System.out.println(nameMd5);
	}
}
