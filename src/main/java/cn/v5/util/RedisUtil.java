package cn.v5.util;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/** 
 * @author qgan
 * @version 2014年2月26日 下午3:09:57
 */
public class RedisUtil {
	private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
	
	private static JedisPool POOL = null;
	
	public static void setPool(JedisPool pool) {
		POOL = pool;
	}
	public static Jedis getJedis() {
        return POOL.getResource();
    }

    public static void releaseJedis(Jedis jedis) {
    	POOL.returnResource(jedis);
    }
    
    public static long setStringData(String key, String field, String data) {

        long ret = 0l;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            ret = jedis.hset(key, field, data);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }

        return ret;
    }

    public static long delStringData(String key, String field) {
        long ret = 0l;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            ret = jedis.hdel(key, field);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }

        return ret;
    }

    public static String getStringData(String key, String field) {

        String data = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            data = jedis.hget(key, field);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return data;
    }

    public static Map<String, String> getAllStringData(String key) {
        Map<String, String> data = new HashMap<String, String>();
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            data = jedis.hgetAll(key);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }

        return data;
    }


    /**
     * 向队列里插入数据
     *
     * @param key
     * @param value
     */
    public static void rpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.rpush(key, value);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    /**
     * 查询出队列中有多少数组
     *
     * @param key
     * @return
     */
    public static long llen(String key) {
        Long res = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            res = jedis.llen(key);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return res;
    }

    /**
     * 查询出队列中的数据
     *
     * @param key
     * @param start 0表示头部的第一个元素
     * @param end
     * @return
     */
    public static List<String> lrange(String key, long start, long end) {
        List<String> listres = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            listres = jedis.lrange(key, start, end);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return listres;
    }


    /**
     * 查询出队列中的数据
     *
     * @param key
     * @return
     */
    public static String lpop(String key) {
        String res = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            res = jedis.lpop(key);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return res;
    }

    /**
     * 从插入队列处取出数据
     *
     * @param key key
     * @return 数据
     */
    public static String rpop(String key) {
        String res = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            res = jedis.rpop(key);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return res;
    }

    public static void set(String key, String data) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.set(key, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }

    }

    public static void set(String key, int second, String data) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.setex(key, second, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return result;
    }


    public static <T> T pull(String key, Class<T> classOfT) {
        if (key != null) {
            String result = get(key);
            if (result != null) {
            	Jedis jedis = RedisUtil.getJedis();
            	try {
            		return JsonUtil.fromJson(result, classOfT);
            	} catch (Exception e) {
            		log.error(e.getMessage(), e);
            	} finally {
                    RedisUtil.releaseJedis(jedis);
                }
            }
        }
        return null;
    }

    public static void push(String key, Object obj) {
        if (obj != null) {
            String value = null;
			try {
				value = JsonUtil.toJson(obj);
			} catch (IOException e) {
			}
			if(null != value)
				set(key, value);
        }
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public static void remove(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.del(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }

    }

    /**
     * 指定域自增1
     *
     * @param key   键
     * @param field 域
     */
    public static long fieldIncrByOne(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.hincrBy(key, field, 1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return -1;
    }

    /**
     * set二进制数据
     *
     * @param key   键
     * @param value 二进制值
     */
    public static void set(String key, byte[] value) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.set(key.getBytes(), value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    /**
     * get二进制数据
     *
     * @param key   键
     * @param dummy 用于区别方法签名，无实际用途
     * @return 数据
     */
    public static byte[] get(String key, int dummy) {
        Jedis jedis = null;
        byte[] result = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.get(key.getBytes());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return result;
    }

    /**
     * 删除二进制数据
     *
     * @param key 键
     */
    public static void del(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            jedis.del(key.getBytes());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    /**
     * 自增数据
     *
     * @param key 键
     * @return 自增后的数据
     */
    public static long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            return jedis.incr(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return -1;
    }


    /**
     * 向集合中添加一个成员
     *
     * @param key
     * @param member
     */
    public static boolean addSet(String key, String member) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.sadd(key, member) == 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }

        return result;
    }


    /**
     * 随机获取并删除一个元素
     *
     * @param key
     * @return
     */
    public static String spop(String key) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.spop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return result;
    }


    /**
     * 在集合中移除指定元素
     * 从key对应set中移除给定元素，成功返回true，如果member在集合中不存在或者key不存在返回false
     *
     * @param key
     * @param member
     * @return
     */
    public static boolean srem(String key, String member) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = RedisUtil.getJedis();
            result = jedis.srem(key, member) == 1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
        return result;
    }

    /**
     * 添加元素到集合
     *
     * @param key    key
     * @param member 值
     * @return 添加结果（-1为非redis返回结果，表示出现为定义错误）
     */
    public static long sadd(String key, String member) {
        Jedis jedis = getJedis();
        try {
            Long addResult = jedis.sadd(key, member);
            if (addResult != null) {
                return addResult;
            } else {
                return -1;
            }
        } catch (Exception e) {
            log.error("redis sadd exception.", e);
            return -1;
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    /**
     * 删除集合中的指定元素
     *
     * @param key    key
     * @param member 值
     * @return 删除结果（-1为非redis返回结果，表示出现为定义错误）
     */
    public static long sremove(String key, String member) {
        Jedis jedis = getJedis();
        try {
            Long addResult = jedis.srem(key, member);
            if (addResult != null) {
                return addResult;
            } else {
                return -1;
            }
        } catch (Exception e) {
            log.error("redis sremove exception.", e);
            return -1;
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    /**
     * 判断元素是否在集合中
     *
     * @param key    key
     * @param member 要判断的元素
     * @return 是否在集合中（false同时也代表出现错误）
     */
    public static boolean sismember(String key, String member) {
        Jedis jedis = getJedis();
        try {
            Boolean result = jedis.sismember(key, member);
            if (result != null) {
                return result;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("redis sismember exception.", e);
            return false;
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }

    /**
     * 获取集合中的所有元素
     *
     * @param key key
     * @return 元素的集合（空集合同时也代表出现异常）
     */
    public static Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        try {
            Set<String> resultSet = jedis.smembers(key);
            if (resultSet != null) {
                return resultSet;
            } else {
                return Collections.emptySet();
            }
        } catch (Exception e) {
            log.error("redis smembers exception.", e);
            return Collections.emptySet();
        } finally {
            RedisUtil.releaseJedis(jedis);
        }
    }
}
