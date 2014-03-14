package cn.v5.framework.dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.base.CaseFormat;

/** 
 * @author qgan
 * @version 2013年12月17日 下午3:52:06
 */
public class DbHelper {
	public static byte[] handleBlob(Blob blob) throws SQLException {
		if (blob == null)
			return null;
		
		InputStream is = null;
		try {
			is = blob.getBinaryStream();
			byte[] data = new byte[(int)blob.length()];		// byte[] data = new byte[is.available()];
			is.read(data);
			is.close();
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {is.close();} catch (IOException e) {throw new RuntimeException(e);}
		}
	}
	
	public static String handleClob(Clob clob) throws SQLException {
		if (clob == null)
			return null;
		
		Reader reader = null;
		try {
			reader = clob.getCharacterStream();
			char[] buffer = new char[(int)clob.length()];
			reader.read(buffer);
			return new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {reader.close();} catch (IOException e) {throw new RuntimeException(e);}
		}
	}
	
	private static final String TABLE_NAME_PREFIX = "t_";
	public static String getTableName(String className) {
		
		return TABLE_NAME_PREFIX + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
	}
	
	public static String getColumnName(String fieldName) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
	}
	 
	/**
	 * 将驼峰风格替换为下划线风格 
	 * @param str
	 * @return
	 */
	public static String toUnderline(String str) { 
	    Matcher matcher = Pattern.compile("[A-Z]").matcher(str); 
	    StringBuilder builder = new StringBuilder(str); 
	    for (int i = 0; matcher.find(); i++) { 
	        builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase()); 
	    } 
	    if (builder.charAt(0) == '_') { 
	        builder.deleteCharAt(0); 
	    } 
	    return builder.toString(); 
	} 


	/**
	 * 将下划线风格替换为驼峰风格 
	 * @param str
	 * @return
	 */
	public static String toCamelhump(String str) { 
	    Matcher matcher = Pattern.compile("_[a-z]").matcher(str); 
	    StringBuilder builder = new StringBuilder(str); 
	    for (int i = 0; matcher.find(); i++) { 
	        builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase()); 
	    } 
	    if (Character.isUpperCase(builder.charAt(0))) { 
	        builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0)))); 
	    } 
	    return builder.toString(); 
	} 
	
	public static String replaceFormatSqlOrderBy(String sql) {
		sql = sql.replaceAll("(\\s)+", " ");
		int index = sql.toLowerCase().lastIndexOf("order by");
		if (index > sql.toLowerCase().lastIndexOf(")")) {
			String sql1 = sql.substring(0, index);
			String sql2 = sql.substring(index);
			sql2 = sql2.replaceAll("[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.]+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.]+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*", "");
			return sql1 + sql2;
		}
		return sql;
	}
	
	public static void main(String[] args) {
		System.out.println(getTableName("SendMsg"));
		
		System.out.println(getColumnName("sendMsg"));
		
		System.out.println(replaceFormatSqlOrderBy("where h=? and ldf=dd order by d desc"));
	}
}
