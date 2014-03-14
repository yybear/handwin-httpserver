package cn.v5.dao;

import cn.v5.model.GroupInfo;

import cn.v5.model.GroupInfo.GroupInfoMapper;
import cn.v5.model.GroupMembers;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created by sunchj on 14-2-25.
 */
public interface GroupDao {

    @SqlQuery("select id,name,status,creator_name_md5,avatar,create_time  from  group_info where id = :id")
    @Mapper(GroupInfoMapper.class)
    GroupInfo getGroupInfoById(@Bind("id") String groupId);

    @SqlUpdate("insert into group_info (id,name,avatar,create_time,status,creator_name_md5,signature,update_time) " +
            "values (:id,:name,:avatar,:createTime,:status,:creatorNameMd5,:signature,:updateTime)")
    int saveGroupInfo(@BindBean GroupInfo groupInfo);

    @SqlUpdate("update group_info set name = :name,update_time =:updateTime where id = :id")
    int updateGroupInfo(@BindBean GroupInfo groupInfo);

    @SqlUpdate("update group_members set status = :status,update_time =:updateTime where user_name_md5 = :userNameMd5 and group_id = :groupId")
    int updateGroupMember(@BindBean GroupMembers groupMembers);

    @SqlQuery("select user_name_md5  from  group_members where group_id = :group_id and status = :status")
    List<String> getGroupMember(@Bind("group_id") String group_id,@Bind("status") Integer status);

    @SqlQuery("select user_name_md5  from  group_members where group_id = :group_id and status = :status")
    List<String> getGroupMemberByMD5(@Bind("group_id") String group_id,@Bind("status") Integer status,@Bind("user_name_md5") String user_name_md5);

    @SqlUpdate("insert into group_members (group_id,user_name_md5,name,create_time,status,update_time) " +
            "values (:groupId,:userNameMd5,:name,:createTime,:status,:updateTime)")
    int saveGroupMember(@BindBean GroupMembers groupMembers);


    @SqlQuery("select count(id)  from  group_members where group_id = :group_id and user_name_md5 = :user_name_md5")
    int findMemberIsExist(@Bind("group_id") String group_id,@Bind("user_name_md5") String user_name_md5);


    @SqlUpdate("delete from t_user_tosend_message where groupId = :groupId and receiverType = :receiverType and receiver = :receiver")
    void deleteToSendMsgByReceiver(@Bind("groupId") String groupId,@Bind("receiverType") String receiverType,@Bind("receiver") String receiver);

    @SqlUpdate("delete from t_user_tosend_message where groupId = :groupId and receiverType = :receiverType")
    void deleteToSendMsgByGroupId(@Bind("groupId") String groupId,@Bind("receiverType") String receiverType);
}
