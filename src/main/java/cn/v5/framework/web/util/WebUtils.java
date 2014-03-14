package cn.v5.framework.web.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * @author qgan
 * @version 2013年12月26日 下午4:19:29
 */
public class WebUtils extends org.springframework.web.util.WebUtils {
	public static final Pattern EMAIL_PATTERN = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public static final Pattern MOBILE_PATTERN = Pattern
			.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	
	private static final String[] MOBILE_SPECIFIC_SUBSTRING = { "ipad",
			"iphone", "android", "midp", "opera mobi", "opera mini",
			"blackberry", "hp ipaq", "iemobile", "msiemobile", "windows phone",
			"htc", "lg", "mot", "nokia", "symbian", "fennec", "maemo", "tear",
			"midori", "armv", "windows ce", "windowsce", "smartphone",
			"240x320", "176x220", "320x320", "160x160", "webos", "palm",
			"sagem", "samsung", "sgh", "siemens", "sonyericsson", "mmp",
			"ucweb" };

	public static boolean isMobile(String mobiles) {

		if (StringUtils.isBlank(mobiles))
			return false;

		Matcher m = MOBILE_PATTERN.matcher(mobiles);
		return m.matches();
	}

	public static boolean isEmail(String email) {

		if (StringUtils.isBlank(email))
			return false;

		Matcher m = EMAIL_PATTERN.matcher(email);
		return m.matches();
	}

	public static boolean isWapRequest(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent").toLowerCase();
		for (String mobile : MOBILE_SPECIFIC_SUBSTRING) {
			if (userAgent.contains(mobile)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		return header != null && "XMLHttpRequest".equals(header);
	}

	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private static String domain = null;

	public static String getDomain(HttpServletRequest request) {
		if (null == domain) {
			StringBuilder sb = new StringBuilder("http://");
			sb.append(request.getServerName());
			if (request.getServerPort() != 80) {
				sb.append(":").append(request.getServerPort());
			}
			sb.append(request.getContextPath());
			domain = sb.toString();
		}

		return domain;

	}
}
