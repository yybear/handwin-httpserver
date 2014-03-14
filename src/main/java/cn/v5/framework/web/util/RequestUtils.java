package cn.v5.framework.web.util;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {
    public static UrlPathHelper URL_PATH_HELPER = new UrlPathHelper() {
        @Override
        public String getLookupPathForRequest(HttpServletRequest request) {
            String key = request.getRequestURI() + "_lookupPath";
            String path = (String) request.getAttribute(key);
            if (path == null) {
                request.setAttribute(key, path = super.getLookupPathForRequest(request));
            }
            return path;
        }
    };

    public static PathMatcher PATH_MATCHER = new AntPathMatcher();

    public static String getClientIP(HttpServletRequest request) {
        String xForwardedFor;
        xForwardedFor = StringUtils.trimToNull(request.getHeader("$wsra"));
        if (xForwardedFor != null) {
            return xForwardedFor;
        }
        xForwardedFor = StringUtils.trimToNull(request.getHeader("X-Real-IP"));
        if (xForwardedFor != null) {
            return xForwardedFor;
        }
        xForwardedFor = StringUtils.trimToNull(request.getHeader("X-Forwarded-For"));
        if (xForwardedFor != null) {
            int spaceIndex = xForwardedFor.indexOf(',');
            if (spaceIndex > 0) {
                return xForwardedFor.substring(0, spaceIndex);
            } else {
                return xForwardedFor;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getDomain(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        int end = url.indexOf(".");
        if (end == -1)
            return "";
        int start = url.indexOf("//");
        return url.substring(start + 2, end);
    }

    public static boolean matchAny(HttpServletRequest request, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, String[] patterns) {
        if (ArrayUtils.isNotEmpty(patterns)) {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, lookupPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean matchAll(HttpServletRequest request, UrlPathHelper urlPathHelper, PathMatcher pathMatcher, String[] patterns) {
        if (ArrayUtils.isNotEmpty(patterns)) {
            String lookupPath = urlPathHelper.getLookupPathForRequest(request);
            for (String pattern : patterns) {
                if (!pathMatcher.match(pattern, lookupPath)) {
                    return false;
                }
            }
        }
        return true;
    }
}

