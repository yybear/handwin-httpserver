package cn.v5.service.impl;


import cn.v5.dao.GroupDao;
import cn.v5.model.GroupInfo;
import cn.v5.model.GroupMembers;
import cn.v5.service.GroupService;
import org.skife.jdbi.v2.IDBI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by sunchj on 14-2-25.
 */
@Transactional
public class GroupServiceImpl implements GroupService {

    @Resource(name = "dbi")
    private IDBI dbi;

    @Override
    public GroupInfo getGroupInfoById(String groupId) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.getGroupInfoById(groupId);
    }

    @Override
    public List<String> getGroupMember(String group_id, Integer status) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.getGroupMember(group_id, status);
    }

    @Override
    public List<String> getGroupMember(String group_id, Integer status, String user_md5) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.getGroupMemberByMD5(group_id, status, user_md5);
    }

    @Override
    public int saveGroupInfo(GroupInfo groupInfo) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.saveGroupInfo(groupInfo);

    }

    @Override
    public int saveGroupMember(GroupMembers gm) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.saveGroupMember(gm);
    }

    @Override
    public int updateGroupMember(GroupMembers groupMembers) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.updateGroupMember(groupMembers);
    }

    @Override
    public int updateGroupInfo(GroupInfo groupInfo) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.updateGroupInfo(groupInfo);
    }

    @Override
    public int findMemberIsExist(String group_id, String user_name_md5) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        return groupDao.findMemberIsExist(group_id,user_name_md5);
    }

    @Override
    public void deleteToSendMsgByReceiver(String groupId, String receiverType, String receiver) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        groupDao.deleteToSendMsgByReceiver(groupId,receiverType,receiver);
    }

    @Override
    public void deleteToSendMsgByGroupId(String groupId, String receiverType) {
        GroupDao groupDao = dbi.open(GroupDao.class);
        groupDao.deleteToSendMsgByGroupId(groupId,receiverType);
    }
}
