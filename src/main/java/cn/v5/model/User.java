package cn.v5.model;


import cn.v5.util.ConfigUtils;
import cn.v5.util.PictureUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.digest.DigestUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class User {



    public Long id;


    /** 用户登陆名 */
    @JsonIgnore
    public String name;

    /** name md5值 */
    public String nameMd5;

    /** 密码 */
    public String password;

    /** 密码salt */
    public String passwdSalt;

    /** 用户昵称 */
    public String nickname;

    /** 手机号码 */
    public String mobile;

    /** 性别 */
    public Integer sex;

    /** 头像地址 */

    @JsonIgnore
    public String avatar;

    public String avatar_url;

    /** 魅力值 */
    public Integer charm;

    /** 创建时间 */
    public Timestamp createTime;

    /** 最近一次登陆时间 */
    public Timestamp lastLoginTime;

    /** 最近更新时间 */
    public Long lastUpdateTime;

    /**注册来源 */
    public String regSource;

    /**
     * 用户签名
     */
    public String signature;

    /**
     * 国家代码
     */
    public String countrycode;



    /**
     * 语言版本
     */
    public String language;


    /**
     * 免打扰时间段
     */
    public String hideTime;

    /** 用户类型 */
    public Integer userType;



    public String sessionId;

    public String tcp_server;

    public String status;

    /**
     * 用户注册来自哪个app
     */
    public Integer appId;


    public static String genPassword(String password, String salt) {
        return DigestUtils.md5Hex(password + salt);
    }

    public static String genSalt() {
        return DigestUtils.sha256Hex(Long.toHexString(System.currentTimeMillis()));
    }

    public static String genSessionId(User user) {
        return DigestUtils.md5Hex(user.name + Long.toHexString(System.currentTimeMillis()));
    }



    public static class UserMapper implements ResultSetMapper<User> {
        public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            User user = new User();
            user.id = r.getLong("id");
            user.name = r.getString("name");
            user.nameMd5 = r.getString("name_md5");
            user.nickname = r.getString("nickname");
            user.avatar = r.getString("avatar");
            user.mobile = r.getString("mobile");
            user.status = r.getString("status");
            user.hideTime = r.getString("hide_time");
            user.lastLoginTime = r.getTimestamp("last_login_time");
            user.appId = r.getInt("app_id");
            user.lastUpdateTime = r.getLong("last_update_time");
            user.regSource = r.getString("reg_source");
            user.signature = r.getString("signature");
            user.createTime = r.getTimestamp("create_time");

            if (user.avatar == null || user.avatar.isEmpty() || user.avatar.equals("default")) {
                user.avatar_url = ConfigUtils.getString("default.avatar");
            } else {
                user.avatar_url= PictureUtil.genPictureThumbUrl(user.avatar);
            }

            return user;
        }

    }

//    @PostUpdate
//    public void postUpdate() {
//        this.collections = null;
////        play.cache.Cache.set(CacheKey.USER_CACHE_ID + this.nameMd5, this);
//
//        if (this.avatar == null || this.avatar.isEmpty() || this.avatar.equals("default")) {
//            this.avatar_url = Play.configuration.getProperty("default.avatar");
//        } else {
//            this.avatar_url = PictureUtil.genPictureThumbUrl(this.avatar);
//        }
//        RedisUtil.push(RedisCacheKey.USER_CACHE_ID + this.nameMd5, this);
//    }

//    @PostRemove
//    public void postRemove() {
//        this.collections = null;
////        play.cache.Cache.delete(CacheKey.USER_CACHE_ID + this.nameMd5);
//        RedisUtil.remove(RedisCacheKey.USER_CACHE_ID + this.nameMd5);
//    }


    public void postLoad() {
        if (this.avatar == null || this.avatar.isEmpty() || this.avatar.equals("default")) {
            this.avatar_url = ConfigUtils.getString("default.avatar");
        } else {
            this.avatar_url = PictureUtil.genPictureThumbUrl(this.avatar);
        }
    }


    public static boolean isAllowSend(String time){
        boolean result = false;   //可以发
        if(time== null || time.length() != 11){
            return result;
        }

        if(time.length() != 11){
            return true;
        }

        time = time.replaceAll(":","");
        int len = time.indexOf("-");

        DateFormat format = new SimpleDateFormat("HHmm");
        Integer nowTime = Integer.parseInt(format.format(new Date()));
        Integer startTime = Integer.parseInt(time.substring(0,len));
        Integer endTime   = Integer.parseInt(time.substring(len+1));

        //开始和结束时间再同一天
        if(startTime < endTime ){
            //在限制区间内 不发
            if(startTime  < nowTime && nowTime < endTime){
                result = true;
            }

        }else{

            //在限制区间内 不发
            if((startTime < nowTime && nowTime < 2400) || nowTime <  endTime){
                result = true;
            }

        }


        return  result;

    }

//    public static PaginationSupport findByCondition(int page, int pageSize) {
//
//        long count = count("from User");
//        PaginationSupport ps = new PaginationSupport(page, count, pageSize);
//        List<User> items = find("from User order by id desc").from((page - 1) * pageSize).fetch(pageSize);
//        ps.setItems(items);
//
//        return ps;
//
//    }
}
