package cn.v5.dao;

import cn.v5.model.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/** 
 * @author qgan
 * @version 2014年2月24日 下午6:25:23
 */
public interface UserDao {

    @SqlQuery("select id,name,name_md5,nickname,avatar,mobile,status,hide_time,last_login_time,app_id,last_update_time,reg_source,signature,create_time  from  User where name_md5 = :name_md5")
    @Mapper(User.UserMapper.class)
    User getUserInfoById(@Bind("id") Long user_id);

    @SqlQuery("select id,name,name_md5,nickname,avatar,mobile,status,hide_time,last_login_time,app_id,last_update_time,reg_source,signature,create_time  from  User where name_md5 = :name_md5")
    @Mapper(User.UserMapper.class)
    User getUserInfoByNameMd5(@Bind("name_md5") String  name_md5);


}
