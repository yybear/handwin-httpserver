package cn.v5.service;

import cn.v5.model.GroupInfo;
import cn.v5.model.GroupMembers;


import java.util.List;

/**
 * Created by sunchj on 14-2-25.
 */
public interface GroupService {

    public GroupInfo getGroupInfoById(String group_id);
    public List<String> getGroupMember(String group_id,Integer status);
    public List<String> getGroupMember(String group_id,Integer status,String user_md5);
    public int saveGroupInfo(GroupInfo groupInfo);
    public int saveGroupMember(GroupMembers groupMembers);
    public int updateGroupMember(GroupMembers groupMembers);
    public int updateGroupInfo(GroupInfo groupInfo);
    public int findMemberIsExist(String group_id,String user_name_md5);
    public void deleteToSendMsgByReceiver(String groupId,String receiverType,String receiver);
    public void deleteToSendMsgByGroupId(String groupId,String receiverType);
}
