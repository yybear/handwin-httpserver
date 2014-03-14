package cn.v5.web.api;

import java.io.File;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.v5.util.ConfigUtils;
/** 
 * @author qgan
 * @version 2014年2月26日 上午8:58:06
 */
@Controller
@RequestMapping("/api")
@Validated
public class ApiTest implements ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(ApiTest.class);
	
	@Autowired 
	ServletContext servletContext;
	
	private ApplicationContext ctx;
	
	boolean isHandler(Class<?> beanType) {
		return ((AnnotationUtils.findAnnotation(beanType, Controller.class) != null) ||
				(AnnotationUtils.findAnnotation(beanType, RequestMapping.class) != null));
	}
	
	@RequestMapping("/test")
	public <T> void test(@NotNull Integer u) throws Exception {
		String base = ConfigUtils.getString("base.url");
		String root = servletContext.getRealPath("/");
		
		log.debug("root path is {}", root);
		ParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		File testPage = new File(root+File.separator+"static"+File.separator+"apiTest.html");
		if(u == null && testPage.exists() && testPage.length() > 0)
			return;
		
		String[] beanNames = ctx.getBeanNamesForType(Object.class);
		for (String beanName : beanNames) {
			Class<?> clazz = ctx.getType(beanName);
			String className = clazz.getCanonicalName();
			if(className.indexOf(".web.api.") < 0 || !isHandler(clazz))
				continue;
			log.debug("class name is {}", className);
			Method[] methods = clazz.getMethods();
			for(Method method : methods) {
				RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
				if(methodAnnotation == null)
					continue;
				String[] paramsNames = nameDiscoverer.getParameterNames(method);
                log.debug("paramNames is {}", paramsNames);
				String[] values = methodAnnotation.value();
				//methodAnnotation.method();
				log.debug("methodAnnotation params is {}", values[0]);
			}
		}
		
		/*if(u!=null || !testPage.exists() || testPage.length() == 0) {
			log.debug("create new apiTest.html");
			testPage.createNewFile();
			
			List<String> lines = new ArrayList<String>();
			lines.add("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><link rel=\"stylesheet\" type=\"text/css\" "
					+ "href=\"/public/stylesheets/base.css\"><script type=\"text/javascript\" src=\"/public/bootstrap/jquery-1.10.2.min.js\">"
					+ "</script><link rel=\"stylesheet\" type=\"text/css\" href=\"/public/bootstrap/css/bootstrap.min.css\">"
					+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"/public/bootstrap/css/bootstrap-theme.min.css\">"
					+ "<script type=\"text/javascript\" src=\"/public/bootstrap/js/bootstrap.min.js\"></script>"
					+ "<script type=\"text/javascript\" src=\"/public/bootstrap/jquery.cookie.js\"></script>"
					+ "</head>"
					+ "<body>"
					+ "<div class=\"container\"><nav class=\"navbar navbar-default\" role=\"navigation\">"
					+ "<div class=\"navbar-header\">"
					+  "<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#bs-example-navbar-collapse-1\">"
					+      "<span class=\"sr-only\">Toggle navigation</span>"
					+      "<span class=\"icon-bar\"></span>"
					+      "<span class=\"icon-bar\"></span>"
					+      "<span class=\"icon-bar\"></span>"
					+    "</button>"
					+    "<a class=\"navbar-brand\" href=\"#\">HttpServer Api Test</a>"
					+  "</div></nav>"
					+ "<div class='alert alert-warning alert-dismissable'>"
					+ "<strong>说明！</strong>"
					+ "<ul>"
					+ "<li>不支持部分方法（上传文件，下载文件等）测试</li>"
					+ "<li>先登录后测试</li>"
					+ "</ul>"
					+ "</div>"
					+ "</div>"
					+ "<div id=\"wrap\">"
					+ "<div class=\"container\"><div class=\"row\">");
			
			StringBuilder left = new StringBuilder("<div class=\"col-md-3\"><ul class=\"nav nav-pills nav-stacked\">");
			StringBuilder right = new StringBuilder("<div class=\"col-md-9\">");
			int i = 0;
			for (Route route : Router.routes) {
				String path = route.path;
				if(StringUtils.indexOf(path, "/api/") < 0 || "/api/test".equals(path))
					continue;
				// 左侧导航
				left.append("<li "+(i==0?"class=\"active\"":"")+">");
				left.append("<a href=\"#"+route.action+"\">"+route.method.toUpperCase() + "&nbsp;&nbsp;" + path+"</a>");
				left.append("</li>");
				
				
				String method = route.method;
				if("*".equals(method))
					method = "GET";
				
				String action = route.action;
				String actionClass = StringUtils.substringBetween(action, ".");
				String actionMethod = StringUtils.substringAfterLast(action, ".");
				log.debug("action class is " + actionClass + " and method is " + actionMethod);
				// 右侧内容
				right.append("<div id='"+route.action+"' class=\"panel panel-default\">");
				right.append("<div class=\"panel-heading\"><h4>"+method+"&nbsp;&nbsp;"+ path +"</h4></div>");
				right.append("<div class=\"panel-body\">");
				
				Object[] ca = ActionInvoker.getActionMethod(route.action);
				Method aMethod = (Method) ca[1];
				String mName = aMethod.getName();
				
				boolean uploadFile = false;
				if("POST".equals(method)) {
					Class[] types = aMethod.getParameterTypes();
					for(Class t : types) {
						if(!t.isArray() && (t.getName().equals("java.io.File"))) {
							uploadFile = true; break;
						}
					}
				}
				if(uploadFile) {
					right.append("<form class=\"form-horizontal\" action=\""+base + path+"\" role=\"form\" method='post' enctype=\"multipart/form-data\">");
					right.append("<lable>选择要上传的文件：</lable><input type=\"file\" name=\"file\">");
					right.append("<input type=\"submit\" name=\"smt\" value=\"上传\">");
					i++;
					right.append("</form>");
					right.append("</div></div>");
				} else {
					right.append("<div class=\"form-horizontal\" role=\"form\">");
					Annotation[][] ann = aMethod.getParameterAnnotations();
					String[] paramsNames = Java.parameterNames(aMethod);
					
					if(paramsNames != null && paramsNames.length > 0) {
						int index = 0 ;
						for(String paramName : paramsNames) {
							right.append("<div class=\"form-group\">");
							boolean flag = false;
							if(ann[index].length > 0) {
								for(Annotation annotation : ann[index]) {
									if(annotation instanceof Required) {
										flag = true; break;
									}
								}
							}
							right.append("<label for=\""+mName+paramName+"\" class=\"col-sm-2 control-label\""+(flag?"style='color:red'":"")+">"+paramName+"</label>");
							right.append("<div class=\"col-sm-10\">");
							right.append("<input type=\"text\" class=\"form-control\" id=\""+mName+paramName+"\" placeholder=\""+paramName+"\">");
							right.append("</div></div>");
							index++;
						}
					}
					
					right.append("<div class=\"form-group\">");
					right.append("<label class=\"col-sm-2 control-label\">&nbsp;</label>");
					right.append("<div class=\"col-sm-10\">");
					right.append("<button type=\"submit\" class=\"btn btn-default\" id=\""+actionClass+mName+"\">发送请求</button>");
					right.append("</div></div>");
					
					right.append("</div></div>");
					
					right.append("<div class=\"alert alert-success\" style='display:none;' id='"+actionClass+mName+"ok'></div>");
					right.append("<div class=\"alert alert-danger\" style='display:none;' id='"+actionClass+mName+"fail'></div>");
					right.append("</div>");
					
					right.append("<script type=\"text/javascript\">");
					right.append("$('#"+actionClass+mName+"').click(function(){");
					right.append("$('#"+actionClass+mName+"ok').css('display', 'none');");
					right.append("$('#"+actionClass+mName+"fail').css('display', 'none');");
					right.append("$.ajax({");
					right.append("    type: \""+method.toUpperCase()+"\",");
					if(path.indexOf("{") > 0) {
						right.append("    url: '" + base + path.substring(0, path.indexOf("{")) + "'"); // rest方式
						int j = 0;
						for(String pName : paramsNames) {
							if(j > 0)
								right.append("/");
							right.append("+$('#"+mName+pName+"').val()");
							j++;
						}
					} else {
						right.append("    url: '" + base + path +"'");
						if(paramsNames!= null && paramsNames.length > 0) {
							right.append(",data: {");
							int j = 0;
							for(String pName : paramsNames) {
								if(j > 0)
									right.append(",");
								right.append(pName + ":$('#"+mName+pName+"').val()");
								j++;
							}
							right.append("}");
						}
					}
					right.append(",headers:{'client-session':$.cookie('client-session')}");// header
					
					right.append("}).done(function(msg, status, xhr){"); // 请求成功
					right.append("var cnt = 'response header:<br/>' + xhr.getAllResponseHeaders()+'<br/>--------------<br/>response:' + xhr.responseText;");
					right.append("$('#"+actionClass+mName+"ok').html(cnt);");
					right.append("$('#"+actionClass+mName+"ok').css('display', 'block');");
					if("login".equals(actionMethod) || "register".equals(actionMethod)) {
						right.append("$.cookie('client-session', xhr.getResponseHeader('client-session'))");
					}
					right.append("}).fail(function(xhr, textStatus) {"); // 请求失败
					right.append("var cnt = 'response status:'+xhr.status+'<br/>response header:<br/>' + xhr.getAllResponseHeaders()+'<br/>--------------<br/>response:' + xhr.responseText;");
					right.append("$('#"+actionClass+mName+"fail').html(cnt);");
					right.append("$('#"+actionClass+mName+"fail').css('display', 'block');");
					right.append("});");
					right.append("});");
					right.append("</script>");
					i++;
				}
			}
			
			left.append("</ul></div>");
			right.append("</div>");
			lines.add(left.toString());
			lines.add(right.toString());
			lines.add("</div></div></div></body>");
			FileUtils.writeLines(testPage, lines);
		}*/
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
		
	}
}
