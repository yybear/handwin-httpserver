package cn.v5.web.api;

import cn.v5.code.RedisCacheKey;
import cn.v5.code.SystemConstant;
import cn.v5.ex.HttpServerException;
import cn.v5.model.GroupInfo;
import cn.v5.model.GroupMembers;
import cn.v5.model.User;
import cn.v5.service.GroupService;
import cn.v5.service.UserService;
import cn.v5.util.*;
import cn.v5.web.vo.ApiResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.util.*;


/**
 * Created by sunchj on 14-2-25.
 */
@Controller
@RequestMapping("/api/group")
public class GroupApi {

    private static final Logger Logger = LoggerFactory.getLogger(GroupApi.class);



    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    public User getLoginUser(HttpServletRequest request) {
        String clientSession = request.getHeader(SystemConstants.CLIENT_SESSION);
        String userName = RedisUtil.get(SystemConstants.USER_SESSION + clientSession);
        if (userName != null) {
            return userService.getUserFromCache(userName);
        }

        return userService.getUserFromCache("d74f9d978ccd71168574b53cb619cd3a");
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ApiResult create(String name, String signature, String member, HttpServletResponse response, HttpServletRequest request) {
        User you = getLoginUser(request);

        ApiResult result = new ApiResult();
        if (member == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =  ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  =  "没有选择成员";
            return result;
        }
        member += "," + you.nameMd5;

        Set<String> set = new TreeSet<String>();
        String[] users = member.split(",");
        if (users != null && users.length > 0) {
            for (String userMd5 : users) {
                set.add(userMd5);
            }
        }


        String fileName = DigestUtils.md5Hex(System.currentTimeMillis() + "" + UUID.randomUUID());


        //保存群组
        GroupInfo group = new GroupInfo();

        group.avatar = nineRect(set, fileName);

        if (name != null && name.length() > 100) {
            name = name.substring(0, 90) + "...的群";
        }

        group.id = UUID.randomUUID().toString().replace("-","");
        group.createTime = new Date();
        group.status = 0;
        group.name = name;
        group.signature = signature;
        group.creatorNameMd5 = you.nameMd5;
        group.updateTime = System.currentTimeMillis();


        groupService.saveGroupInfo(group);


        String nicknames = "";
        //添加成员
        String[] user = member.split(",");

        if (user != null && user.length > 0) {
            int n = 0;
            for (String name_md5 : user) {
                User u = userService.getUserFromCache(name_md5);
                if (u != null && !"".equals(u.nickname)) {
                    n++;
                    nicknames += "、" + u.nickname;
                    if (n == 5) {
                        if (user.length > 7) {
                            nicknames += "等";
                        }
                        break;
                    }
                }

            }


            Long duduUserId = Long.parseLong(ConfigUtils.getString("dudu.user.id") == null ? "25" : ConfigUtils.getString("dudu.user.id").toString());
            for (String name_md5 : user) {
                User u = userService.getUserFromCache(name_md5);
                if (u != null) {

                    if (u.id == duduUserId.longValue()) {
                        continue;
                    }

                    if (groupService.findMemberIsExist(group.id, u.nameMd5) == 0) {

                        GroupMembers gm = new GroupMembers();
                        gm.groupId = group.id;
                        gm.userNameMd5 = name_md5;
                        gm.status = 0;
                        gm.createTime = new Date();
                        gm.name = u.nickname;
                        gm.updateTime = System.currentTimeMillis();

                        groupService.saveGroupMember(gm);

                        //本处不考虑给群主发消息
                        if (u.id.longValue() != you.id.longValue()) {
                            String name_nick = nicknames.replace("、" + u.nickname, "");
                            String msg = you.nickname + "邀请你" + name_nick + "加入群聊";
                            RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + u.nameMd5 + "|" + msg);
                        }


                    }
                }
            }


        }


        //给群主发消息


        if (!nicknames.equals("")) {
            nicknames = nicknames.substring(1);
        }

        if (name == null || "".equals(name)) {
            name = nicknames.trim();
            if ("".equals(name)) {
                name = "未命名群";
            } else {
                if (nicknames.length() > 50) {
                    nicknames = nicknames.substring(0, 47) + "...";
                }
                name = nicknames + "群";
            }
            group.updateTime = System.currentTimeMillis();
            group.name = name;

            groupService.updateGroupInfo(group);
        }

        nicknames = nicknames.replace("、" + you.nickname, "");
        String msg = "您邀请了" + nicknames + "加入了群";
        RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + you.nameMd5 + "|" + msg);


        if (group.avatar == null || group.avatar.isEmpty() || group.avatar.equals("default")) {
            group.avatarUrl = ConfigUtils.getString("default.avatar");
        } else {
            group.avatarUrl = PictureUtil.genPictureThumbUrl(group.avatar);
        }


        result.resultCode = ApiResultCode.UPLOAD_AVATAR_OK;
        result.resultMsg = group.id;
        result.group = group;

        return result;
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ApiResult update(String groupId, String name, String avatar_url, HttpServletRequest request, HttpServletResponse response) {
        User you = getLoginUser(request);
        ApiResult result = new ApiResult();
        if (groupId == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =  ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  =  "群不存在";
            return result;
        }

        //查询群组
        GroupInfo group = groupService.getGroupInfoById(groupId);
        if (group == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =  ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  =  "群不存在";
            return result;

        }

        //如果用户不是群组的创建者，则不能创建群组
        if (!group.creatorNameMd5.equals(you.nameMd5)) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =  ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  =  "用户没有权限更新群信息";
            return result;

        }

        //组装更改的提示语
        boolean isChanged = false;
        StringBuilder stringBuilder = new StringBuilder();
        String commonInfo = "";

        //群组普通成员的提示信息
        String commonMemeberInfo = "";

        //群主的提示信息
        String managerInfo = "";

        if (name != null && !name.equals(group.name)) {
            group.name = name;
            isChanged = true;
            stringBuilder.append("群名称更改为" + name);
        }
        if (avatar_url != null && !avatar_url.equals(group.avatarUrl)) {
            isChanged = true;
            stringBuilder.append("更新了群头像、");
        }

        if (isChanged) {
            String msg = stringBuilder.toString();
            commonInfo = msg.substring(0, msg.length() - 1);
            commonMemeberInfo = you.nickname + commonInfo;
            managerInfo = "您" + commonInfo;
        }

        List<String> membersList = groupService.getGroupMember(groupId, 0);
        for (String md5name : membersList) {
            User u = userService.getUserFromCache(md5name);
            if (u != null) {
                //本处不考虑给群主发消息
                if (u.id != you.id.longValue()) {
                    if (isChanged) {
                        RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + u.nameMd5 + "|" + commonMemeberInfo);
                    }
                }
            }
        }

        if (isChanged) {
            RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + you.nameMd5 + "|" + managerInfo);
        }

        if (avatar_url != null) {
            int len = avatar_url.lastIndexOf("avatar/");
            if (len != -1) {
                avatar_url = avatar_url.substring(len + 7);
            }

            group.avatar = avatar_url;
        }


        group.updateTime = System.currentTimeMillis();
        groupService.updateGroupInfo(group);

        return result;

    }

    /**
     * 获取群组的相关信息
     *
     * @param groupId 群组ID
     * @param exclude 排除什么字段 用逗号相隔
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Object get(String groupId, String exclude, HttpServletResponse response) {

        GroupInfo group = groupService.getGroupInfoById(groupId);

        if (groupId == null || group == null || group.status == 1) {
            ApiResult result = new ApiResult();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  ="群不存在";
            return result;
        }

        List<User> users = new ArrayList();
        if (exclude == null || "".equals(exclude) || !exclude.contains("member")) {
            List<String> member = groupService.getGroupMember(groupId, 0);
            if (!member.isEmpty()) {
                for (String memb : member) {
                    User u = userService.getUserFromCache(memb);
                    if (u != null) {
                        users.add(u);
                    }

                }
            }

        }


        Map<String, Object> gpinfo = new HashMap();
        gpinfo.put("groupId", group.id);
        gpinfo.put("name", group.name);
        gpinfo.put("avatar_url", group.avatarUrl);
        gpinfo.put("creator_md5", group.creatorNameMd5);
        gpinfo.put("member", users);
        gpinfo.put("number", users.size());

        return gpinfo;
    }

    @RequestMapping(value = "/exit", method = RequestMethod.POST)
    public ApiResult exit(String groupId, HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        User you = getLoginUser(request);

        //查询群组
        GroupInfo group = groupService.getGroupInfoById(groupId);
        if (groupId == null || group == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  ="非群主,没有权限";
            return result;

        }

        //如果是群主
        if (group.creatorNameMd5.equals(you.nameMd5)) {
            group.updateTime = System.currentTimeMillis();
            group.status = 1;

            groupService.updateGroupInfo(group);

            //向群组成员发送群组解散指令
            String msg = "群解散";
            List<String> membersList = groupService.getGroupMember(groupId, 0);
            for (String userNameMd5 : membersList) {
                User us = userService.getUserFromCache(userNameMd5);
                if (us != null && us.id.longValue() != you.id.longValue()) {
                    RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "1|" + group.id + "|" + us.nameMd5 + "|" + msg);
                }

                GroupMembers members = new GroupMembers();
                members.status = -1;
                members.updateTime = System.currentTimeMillis();
                members.userNameMd5 = userNameMd5;
                members.groupId = groupId;

                groupService.updateGroupMember(members);
            }

            //群组解散时，需要删除该群组相关的待发送消息
            groupService.deleteToSendMsgByGroupId(groupId, SystemConstant.TO_SEND_MESSAGE_GROUP);

        } else {
            //成员退出

            GroupMembers members = new GroupMembers();
            members.status = -1;
            members.updateTime = System.currentTimeMillis();
            members.userNameMd5 = you.nameMd5;
            members.groupId = groupId;
            groupService.updateGroupMember(members);


            Set<String> set = new TreeSet<>();
            List<String> membersList = groupService.getGroupMember(groupId, 0);
            for (String userNameMd5 : membersList) {
                User us = userService.getUserFromCache(userNameMd5);
                if (us != null) {
                    String msg = you.nickname + "退出了群";
                    RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + us.nameMd5 + "|" + msg);

                    set.add(userNameMd5);
                }


            }

            //个人退出群组时，需要删除该用户还未从该群组未读取的消息
            groupService.deleteToSendMsgByReceiver(groupId, SystemConstant.TO_SEND_MESSAGE_GROUP, you.nameMd5);

            String fileName = DigestUtils.md5Hex(System.currentTimeMillis() + "" + UUID.randomUUID());

            group.updateTime = System.currentTimeMillis();
            group.avatar = nineRect(set, fileName) == null ? "default" : nineRect(set, fileName);
            groupService.updateGroupInfo(group);


        }


        result.resultCode = ApiResultCode.UPLOAD_AVATAR_OK;
        return result;
    }

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public ApiResult invite(String groupId, String member, HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        User you = getLoginUser(request);
        Set<String> set = new TreeSet<String>();

        //查询群组
        GroupInfo group = groupService.getGroupInfoById(groupId);
        if (groupId == null || group == null || group.status == 1) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  ="群不存在";
            return result;
        }

        if(member == null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  ="群不存在";
            return result;

        }
        String nicknames = "";
        //添加成员
        String[] user = member.split(",");
        if (user != null && user.length > 0) {

            int n = 0;
            for (String name_md5 : user) {
                User u = userService.getUserFromCache(name_md5);
                if (u != null && !"".equals(u.nickname)) {
                    n++;

                    set.add(u.nameMd5);

                    nicknames += "、" + u.nickname;
                    if (n == 5 && n < user.length) {
                        nicknames += "等";
                        break;
                    }
                }

            }
            //嘟嘟小秘书
            Long duduUserId = Long.parseLong(ConfigUtils.getString("dudu.user.id") == null ? "25" : ConfigUtils.getString("dudu.user.id").toString());

            //头像相关
            List<String> listgm = groupService.getGroupMember(groupId, 0);
            for (String userNameMd5 : listgm) {
                set.add(userNameMd5);
            }

            String mbs = "";
            for (String str : set) {
                mbs += str + ",";
            }

            String fileName = DigestUtils.md5Hex(System.currentTimeMillis() + "" + UUID.randomUUID());

            group.updateTime = System.currentTimeMillis();
            group.avatar = nineRect(set, fileName);
            groupService.updateGroupInfo(group);

            String na = nicknames;
            if (!na.equals("")) {
                na = na.substring(1);
            }


            for (String userNameMd5 : listgm) {


                if (userNameMd5.equals(you.nameMd5)) {

                    String msg = "您邀请了" + na + "加入本群";
                    RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + you.nameMd5 + "|" + msg);

                } else {
                    String msg = na + "加入本群";
                    RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + userNameMd5 + "|" + msg);
                }

            }


            for (String name_md5 : user) {
                User u = userService.getUserFromCache(name_md5);
                if (u != null) {

                    if (u.id == duduUserId.longValue()) {
                        continue;
                    }


                    if (groupService.findMemberIsExist(groupId, u.nameMd5) == 0) {
                        GroupMembers gm = new GroupMembers();
                        gm.createTime = new Date();
                        gm.groupId = group.id;
                        gm.userNameMd5 = name_md5;
                        gm.name = u.nickname;
                        gm.status = 0;
                        gm.updateTime = System.currentTimeMillis();

                        groupService.saveGroupMember(gm);

                        if (u.id != you.id.longValue()) {
                            String name = nicknames.replace("、" + u.nickname, "");
                            String msg = name + "加入本群";
                            RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + u.nameMd5 + "|" + msg);


                        }


                    }
//                    else{
//
//                        gm.name = u.nickname;
//                        gm.status = 0;
//                        gm.updateTime = System.currentTimeMillis();
//                        gm.save();
//
//                        if (u.id != you.id.longValue()) {
//                            String name = nicknames.replace("、"+u.nickname,"");
//                            String msg =  name + "加入本群";
//                            RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + u.nameMd5 + "|" + msg);
//
//                        }
//
//                    }
                }
            }
        }
        result.resultCode =ApiResultCode.UPLOAD_AVATAR_OK;
        return  result;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public ApiResult remove(String groupId, String member, HttpServletRequest request, HttpServletResponse response) {
        ApiResult result = new ApiResult();
        User you = getLoginUser(request);

        //查询群组
        GroupInfo group = groupService.getGroupInfoById(groupId);
        if (groupId == null || group == null || group.status == 1) {

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  ="群不存在";
            return result;
        }


        //待删除的成员
        String[] user = member.split(",");

        String name_str = "";
        //如果是群主
        if (group.creatorNameMd5.equals(you.nameMd5)) {

            if (user != null && user.length > 0) {

                //检查是否是自己
                for (String name : user) {
                    if (name.equals(you.nameMd5)) {
                        response.setStatus(HttpStatus.BAD_REQUEST.value());
                        result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
                        result.resultMsg  ="不能删除自己";
                        return result;
                    }
                }

                for (String name_md5 : user) {

                    User u = userService.getUserFromCache(name_md5);

                    if (u != null) {

                        GroupMembers members = new GroupMembers();
                        members.status = -1;
                        members.updateTime = System.currentTimeMillis();
                        members.userNameMd5 = u.nameMd5;
                        members.groupId = groupId;
                        groupService.updateGroupMember(members);

                        //删除待发消息
                        groupService.deleteToSendMsgByReceiver(groupId, SystemConstant.TO_SEND_MESSAGE_GROUP, u.nameMd5);

                        name_str += "、" + u.nickname;
                    }

                }


            }


        } else {

            response.setStatus(HttpStatus.BAD_REQUEST.value());

            result.resultCode =ApiResultCode.ADD_CONTACT_FAIL;
            result.resultMsg  ="非群主，没有权限！";
            return result;
        }

        if (!"".equals(name_str)) {
            name_str = name_str.substring(1);
        }

        Set<String> set = new TreeSet<String>();

        List<String> membersList = groupService.getGroupMember(groupId, 0, you.nameMd5);
        for (String userNameMd5 : membersList) {
            User us = userService.getUserFromCache(userNameMd5);

            String msg = name_str + "被移出了本群";
            RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + us.nameMd5 + "|" + msg);
            set.add(userNameMd5);
        }

        String msg = "您将" + name_str + "移出了本群";
        RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "0|" + group.id + "|" + you.nameMd5 + "|" + msg);

        //向被删除人发的主要是删除指令 内容不会被发送
        if (user != null && user.length > 0) {
            for (String name : user) {
                RedisUtil.rpush(RedisCacheKey.USER_MSG_PUSH, "2|" + group.id + "|" + name + "|" + "");
            }
        }


        set.add(you.nameMd5);

        String fileName = DigestUtils.md5Hex(System.currentTimeMillis() + "" + UUID.randomUUID());

        group.updateTime = System.currentTimeMillis();
        group.avatar = nineRect(set, fileName);
        groupService.updateGroupInfo(group);
        result.resultCode = ApiResultCode.UPLOAD_AVATAR_OK;
        return result;

    }


    public String nineRect(Set<String> user, final String fileName) {
        if (user == null || user.isEmpty()) {
            return "default";
        }
        String avatarUrl;
        if (!fileName.contains(".")) {

            File destPath = new File(PictureUtil.genPicturePath(fileName));
            if (!destPath.exists()) {
                destPath.mkdirs();
            }

            avatarUrl = fileName.substring(0, 2) + "/" + fileName.substring(2, 4) + "/" + fileName + ".jpg";

        } else {
            avatarUrl = fileName;
        }


        String result = "";
        int i = 0;


        if (user.size() > 1) {
            for (String name_md5 : user) {

                User u = userService.getUserFromCache(name_md5);
                if (u != null && u.avatar != null && !"".equals("default")) {
                    int len = u.avatar.lastIndexOf(".");
                    if (len == -1) {
                        continue;
                    }
                    String picfile = ConfigUtils.getString("avatar.storage.path") + u.avatar.substring(0, len) + PictureUtil.SUFFIX_THUMB + u.avatar.substring(len);
                    File f = new File(picfile);
                    if (f.exists()) {
                        result += picfile + ",";
                        i++;
                        if (i == 10) {
                            break;
                        }
                    }
                }
            }
        }


        if (!result.equals("")) {
            int len = result.lastIndexOf(",");
            if (len != -1) {
                result = result.substring(0, len);
                String[] photo = result.split(",");
                if (photo.length > 1) {

                    String comePathFile = "";
                    switch (photo.length) {
                        case 2: {
                            comePathFile = photo[0] + "  -geometry 150X150+0+79 -composite ";
                            comePathFile += photo[1] + " -geometry 150X150+158+79 -composite ";
                            break;
                        }
                        case 3: {
                            comePathFile = photo[0] + "  -geometry 150X150+79+0 -composite ";
                            comePathFile += photo[1] + " -geometry 150X150+0+158 -composite ";
                            comePathFile += photo[2] + " -geometry 150X150+158+158 -composite ";
                            break;
                        }
                        case 4: {
                            comePathFile = photo[0] + "  -geometry 150X150+0+0 -composite ";
                            comePathFile += photo[1] + " -geometry 150X150+158+0 -composite ";
                            comePathFile += photo[2] + " -geometry 150X150+0+158 -composite ";
                            comePathFile += photo[3] + " -geometry 150X150+158+158 -composite ";
                            break;
                        }
                        case 5: {
                            comePathFile = photo[0] + "  -geometry 100X100+52+50 -composite ";
                            comePathFile += photo[1] + " -geometry 100X100+156+50 -composite ";

                            comePathFile += photo[2] + " -geometry 100X100+0+158 -composite ";
                            comePathFile += photo[3] + " -geometry 100X100+104+158 -composite ";
                            comePathFile += photo[4] + " -geometry 100X100+208+158 -composite ";
                            break;
                        }
                        case 6: {
                            comePathFile = photo[0] + " -geometry 100X100+0+50 -composite ";
                            comePathFile += photo[1] + " -geometry 100X100+104+50 -composite ";
                            comePathFile += photo[2] + " -geometry 100X100+208+50 -composite ";

                            comePathFile += photo[3] + " -geometry 100X100+0+158 -composite ";
                            comePathFile += photo[4] + " -geometry 100X100+104+158 -composite ";
                            comePathFile += photo[5] + " -geometry 100X100+208+158 -composite ";
                            break;
                        }
                        case 7: {
                            comePathFile = photo[0] + "  -geometry 100X100+104+0 -composite ";


                            comePathFile += photo[1] + " -geometry 100X100+0+104 -composite ";
                            comePathFile += photo[2] + " -geometry 100X100+104+104 -composite ";
                            comePathFile += photo[3] + " -geometry 100X100+208+104 -composite ";

                            comePathFile += photo[4] + " -geometry 100X100+0+208 -composite ";
                            comePathFile += photo[5] + " -geometry 100X100+104+208 -composite ";
                            comePathFile += photo[6] + " -geometry 100X100+208+208 -composite ";
                            break;
                        }
                        case 8: {
                            comePathFile = photo[0] + " -geometry 100X100+52+1 -composite ";
                            comePathFile += photo[1] + " -geometry 100X100+156+1 -composite ";


                            comePathFile += photo[2] + " -geometry 100X100+0+104 -composite ";
                            comePathFile += photo[3] + " -geometry 100X100+104+104 -composite ";
                            comePathFile += photo[4] + " -geometry 100X100+208+104 -composite ";

                            comePathFile += photo[5] + " -geometry 100X100+0+208 -composite ";
                            comePathFile += photo[6] + " -geometry 100X100+104+208 -composite ";
                            comePathFile += photo[7] + " -geometry 100X100+208+208 -composite ";
                            break;
                        }
                        default: {
                            comePathFile = photo[0] + " -geometry 100X100+0+0 -composite ";
                            comePathFile += photo[1] + " -geometry 100X100+104+0 -composite ";
                            comePathFile += photo[2] + " -geometry 100X100+208+0 -composite ";

                            comePathFile += photo[3] + " -geometry 100X100+0+104 -composite ";
                            comePathFile += photo[4] + " -geometry 100X100+104+104 -composite ";
                            comePathFile += photo[5] + " -geometry 100X100+208+104 -composite ";

                            comePathFile += photo[6] + " -geometry 100X100+0+208 -composite ";
                            comePathFile += photo[7] + " -geometry 100X100+104+208 -composite ";
                            comePathFile += photo[8] + " -geometry 100X100+208+208 -composite ";
                            break;
                        }

                    }

                    final String finalComePathFile = comePathFile;

                    //todo 异步支持
                    JmagickUtil.ninePic(finalComePathFile, PictureUtil.genPicturePath(fileName) + fileName + ".jpg", PictureUtil.genPicturePath(fileName) + fileName + PictureUtil.SUFFIX_THUMB + ".jpg");


                }

            }

        } else {
            avatarUrl = "default";
        }


        return avatarUrl;
    }

}
