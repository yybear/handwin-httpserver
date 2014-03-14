package cn.v5.framework.dao.jdbi;


import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.SqlObjectBuilder;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

/** 
 * @author qgan
 * @version 2014年2月26日 下午4:51:12
 */
public class MyDBI extends DBI {

	public MyDBI(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}
	
	

	public <SqlObjectType> SqlObjectType attach(Class<SqlObjectType> sqlObjectType, Handle handle)
    {
        return SqlObjectBuilder.attach(handle, sqlObjectType);
    }
}
