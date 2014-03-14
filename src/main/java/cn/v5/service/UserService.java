package cn.v5.service;

import cn.v5.model.User;

import java.io.IOException;

/**
 * @author qgan
 * @version 2014年2月25日 上午9:48:30
 */
public interface UserService {
    public User getUserFromCache(String name);
}
