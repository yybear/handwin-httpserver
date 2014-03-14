package cn.v5.code;

/**
 * User: chenxy
 * Date: 13-7-8
 * Time: 上午11:03
 */
public interface RedisCacheKey {

     String USER_SESSION_JSON = "USER_SESSION_JSON";

     String USER_ONLINE_NAME = "USER_ONLINE_NAME";

     String USER_DEVICE_INFO = "USER_DEVICE_INFO";

    /**
     * 格式 0|{group id}|{to user}|body 0系统消息  1解散  2被删除人
     */
     String USER_MSG_PUSH = "USER_MSG_PUSH";

     String USER_SESSION = "USER_SESSION_";


     String CLIENT_SESSION = "client-session";

     String USER_CACHE_ID = "USER_CACHE_ID_";

     String USER_NAME_IN_RENDER = "USER_NAME_IN_RENDER";

     String USER_IN_RENDER = "USER_IN_RENDER";

    /**
     * 随机配对的key
     */
     String USER_RANDOM_PAIRLIST = "USER_RANDOM_PAIRLIST";
    /**
     * 性别男随机配对的key
     */
     String USER_RANDOM_PAIRLIST_MALE = "USER_RANDOM_PAIRLIST_MALE";
    /**
     * 性别女随机配对的Key
     */
     String USER_RANDOM_PAIRLIST_FEMALE = "USER_RANDOM_PAIRLIST_FEMALE";


    /**
     * 根据nameMD5获取对应frontServer地址名
     */
     String USER_FROM_SERVER = "USER_FROM_SERVER_";

    /**
     * 用户所在的udpServer名
     */
     String USER_FROM_UDPSERVER = "USER_FROM_UDPSERVER_";

    /**
     * 用户信息
     */
     String USER_INFO = "USER_INFO_";

    /**
     * 用户的公共锁
     */
     String USER_SHARED_LOCK = "USER_SHARED_LOCK_";

    /**
     * 用户配对的公共数据
     * 存放已配对用户公共字段的数据
     */
     String USER_PAIR_DATA = "USER_PAIR_DATA_";

    /**
     * 用户SDP信息
     * 存放用户的SDP数据（二进制）
     */
     String USER_SDP = "USER_SDP_";

    /**
     * 用户的gameId
     */
     String USER_GAMEID = "USER_GAMEID_";

    /**
     * udp地址与用户对应
     */
     String UDP_ADDR_USER_PAIR = "UDP_ADDR_USER_PAIR_";

    /**
     *  用户 由nameMD5 找session
     */
     String USER_NAMEMD5_SESSION = "USER_NAMEMD5_SESSION_";


    /**
     *  给嘟嘟小秘书打视频或语音电话 key 此标记用来挂断后停止发包
     */
     String USER_DUDU_VIDEO = "USER_DUDU_VIDEO_";

    /**
     *  用户的离线通话记录
     */
     String USER_CALL_QUEUE = "USER_CALL_QUEUE_";

    /**
     * 用户P2P的信息转发
     */
     String USER_P2P_INFO = "USER:P2P:";

    /**
     * 用户配对的前缀
     */
     String USER_MATCH_PREFIX ="Match_Result_";

    /**
     * 用户打电话的roomId
     */
     String USER_CALL_ROOM = "Call_Room_Id";

}
