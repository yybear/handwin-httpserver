package cn.v5.framework.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import cn.v5.framework.ex.CodeException;

import com.google.common.collect.Maps;

/**
 * @author qgan
 * @version 2013年12月23日 下午7:13:03
 */
public class JsonExceptionResolver extends SimpleMappingExceptionResolver {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		int errorCode = -1;

		//String viewName = determineViewName(ex, request);
		String msg = ex.getMessage();
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		Map attributes = Maps.newHashMap();
		attributes.put("msg", msg);
		if (logger.isDebugEnabled())
			attributes.put("detail", ex.getStackTrace());
		attributes.put("data", null);
		if (ex instanceof CodeException) {
			errorCode = ((CodeException) ex).getCode();
		}
		attributes.put("errno", errorCode);

		view.setAttributesMap(attributes);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setView(view);

		response.setContentType("application/json");
		return modelAndView;
	}
}
