package cn.v5.service.impl;


import cn.v5.code.RedisCacheKey;
import cn.v5.model.User;
import cn.v5.util.JsonUtil;
import cn.v5.util.RedisUtil;

import org.skife.jdbi.v2.IDBI;
import org.springframework.transaction.annotation.Transactional;

import cn.v5.dao.UserDao;
import cn.v5.service.UserService;

import javax.annotation.Resource;

import java.io.IOException;

/**
 * @author qgan
 * @version 2014年2月25日 上午9:49:14
 */
@Transactional
public class UserServiceImpl implements UserService {

    @Resource(name = "dbi")
    private IDBI dbi;


    public User getUserFromCache(String name) {
        String key = RedisCacheKey.USER_CACHE_ID + name;
        String userjson = RedisUtil.get(key);
        User user = null;
        if (userjson == null) {
            UserDao userDao = dbi.open(UserDao.class);
            user = userDao.getUserInfoByNameMd5(name);

            try {
            	RedisUtil.set(key, JsonUtil.toJson(user));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {
                user = JsonUtil.fromJson(userjson, User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return user;
    }
}
