package cn.v5.framework.support.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import cn.v5.framework.Constants;

//import com.google.gson.Gson;

/** 
 * @author qgan
 * @version 2013年12月16日 下午7:32:48
 */
public class MappingGsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public MappingGsonHttpMessageConverter() {
        super(new MediaType("application", "json", Constants.CHARSET));
    }

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		/*Reader br = new BufferedReader(new InputStreamReader(inputMessage.getBody(), "UTF-8"));
		Gson gson = new Gson();
		return gson.fromJson(br, clazz);*/
		return null;
	}

	@Override
	protected void writeInternal(Object t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		/*Gson gson = new Gson();
		String json = gson.toJson(t);
		
		Writer writer = new OutputStreamWriter(outputMessage.getBody(), Constants.DEFAULT_CHARSET);
		try {
			writer.write(json);
            writer.flush();
        } finally {
        	writer.close();
        }*/
	}

}
