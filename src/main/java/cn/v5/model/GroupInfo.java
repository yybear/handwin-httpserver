package cn.v5.model;


import cn.v5.util.ConfigUtils;
import cn.v5.util.PictureUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: chenxy
 * Date: 13-7-19
 * Time: 上午10:39
 */

public class GroupInfo {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorNameMd5() {
        return creatorNameMd5;
    }

    public void setCreatorNameMd5(String creatorNameMd5) {
        this.creatorNameMd5 = creatorNameMd5;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String id;

    /**
     * 群组
     */

    public String creatorNameMd5;

    /**
     * 群组头像地址
     */
    @JsonIgnore
    public String avatar;


    /**
     * 群组头像地址
     */
    public String avatarUrl;

    /**
     * 创建时间
     */
    public Date createTime;

    /**
     * 创建时间
     */
    public Long updateTime;

    /**
     * 群组签名
     */

    @JsonIgnore
    public String signature;


    /**
     * 状态
     */
    public int status;


    /**
     * 群名称
     */

    public String name;


    /**
     * 群成员列表
     */
    public List<GroupMembers> member = new ArrayList<GroupMembers>();


    public static class GroupInfoMapper implements ResultSetMapper<GroupInfo> {
        public GroupInfo map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.id = r.getString("id");
            groupInfo.name = r.getString("name");
            groupInfo.status = r.getInt("status");
            groupInfo.creatorNameMd5 = r.getString("creator_name_md5");
            groupInfo.avatar = r.getString("avatar");
            groupInfo.createTime = r.getDate("create_time");
            if (groupInfo.avatar == null || groupInfo.avatar.isEmpty() || groupInfo.avatar.equals("default")) {
                groupInfo.avatarUrl = ConfigUtils.getString("default.avatar");

            } else {
                groupInfo.avatarUrl = PictureUtil.genPictureThumbUrl(groupInfo.avatar);
            }

            return groupInfo;
        }

    }

}
