package cn.v5.model;


import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * User: chenxy
 * Date: 13-7-19
 * Time: 上午10:39
 */

public class GroupMembers {


    public Long id;

    /**
     * 成员ID
     */

    public String userNameMd5;


    /**
     * 加入群时间
     */
    public Date createTime;

    /**
     * 群组ID
     */
    public String groupId;


    /**
     * 群组ID
     */
    public String name;

    /**
     * 用户信息
     */
    public User user;

    /**
     * 更新时间
     */
    public Long updateTime;

    /**
     * 状态
     */
    public Integer status;

    /**
     * 用户信息
     */
    public GroupInfo groupInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserNameMd5() {
        return userNameMd5;
    }

    public void setUserNameMd5(String userNameMd5) {
        this.userNameMd5 = userNameMd5;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
