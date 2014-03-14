package cn.v5.framework.support.jackjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

/**
 * @author qgan
 * @version 2014年2月26日 下午1:12:35
 */
public class CustomJacksonObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = -2517665138396621846L;

	public CustomJacksonObjectMapper() {
		super();
		this.setPropertyNamingStrategy(
			    PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		//this.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		//this.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		this.setSerializationInclusion(Include.NON_NULL);
	}
}
