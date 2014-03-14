package cn.v5.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.v5.code.RedisCacheKey;
import cn.v5.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.v5.ex.UnauthorizedException;
import cn.v5.framework.support.spring.ConfigurableInterceptor;
import cn.v5.util.RedisUtil;
import cn.v5.util.SystemConstants;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/** 
 * @author qgan
 * @version 2014年2月20日 上午11:05:26
 */
public class SecInterceptor extends ConfigurableInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SecInterceptor.class);

	@Override
	public boolean internalPreHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String clientSession = request.getHeader(SystemConstants.CLIENT_SESSION);
		log.debug("user client session is {}", clientSession);
//		if(StringUtils.isBlank(clientSession))
//			throw new UnauthorizedException();
//
 		String userName = RedisUtil.get(SystemConstants.USER_SESSION + clientSession);
//		if(StringUtils.isBlank(userName)) {
//			throw new UnauthorizedException();
//		}

        //printRequest( request,response);

		return true;
	}



    public static void printRequest(HttpServletRequest request,HttpServletResponse response){
        if(log.isDebugEnabled()){
            log.debug("-------- [Request Info]:  start --------");
            log.debug("thread id : %s", Thread.currentThread().getName());
            String clientSession = request.getHeader(RedisCacheKey.CLIENT_SESSION);
            if (clientSession != null) {
                //加入redis 缓存
                String userName =  RedisUtil.get(RedisCacheKey.USER_SESSION + clientSession);
                log.debug(" redis userName: {}，clientSession {}", userName,clientSession);

            }
            log.debug("{},{}", request.getMethod(), request.getRequestURI());
            log.debug("----------------");
            log.debug("{}", request.getHeaderNames().hasMoreElements());
            Enumeration enumeration = request.getHeaderNames();
            while(enumeration.hasMoreElements()){
                Object obj = enumeration.nextElement();
                log.debug("{} = {}",obj,request.getHeader(obj.toString()));
            }

            log.debug("----------------");
//            Logger.debug("%s", request.params.toString());
            for(Map.Entry<String, String[]> sub : ((Map<String, String[]>)request.getParameterMap()).entrySet()){
                StringBuilder sb = new StringBuilder();
                sb.append(sub.getKey());
                sb.append("=");
                sb.append(Arrays.toString(sub.getValue()));
                log.debug(sb.toString());
            }

            log.debug("-------- [Request Info]:   end  --------");
        }
    }
}
