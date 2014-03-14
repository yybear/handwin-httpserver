package cn.v5.util;
/** 
 * @author qgan
 * @version 2014年2月25日 下午2:56:43
 */
public class ApiResultCode {

    public static int REG_ERROR = 1000;
    public static int REG_BIND_MOBILE = 1001;
    public static int REG_MOBILE_NO_ERROR = 1002;
    public static int UPLOAD_USER_INFO_OK = 1003;
    public static int UPLOAD_USER_INFO_FAIL = 1004;

    public static int UPLOAD_AVATAR_OK = 2000;
    public static int UPLOAD_AVATAR_FAIL = 2001;

    public static int DEL_CONTACT_OK = 3000;
    public static int ADD_CONTACT_FAIL = 3001;

    public static int ADD_DEVICE_TOKEN_OK = 3100;
    public static int ADD_DEVICE_TOKEN_FAIL = 3101;

    public static int GAME_SCORE_PARAM_ERROR = 4001;


    public static int SUCCESS =  2000;


    /**
     * 超过次数限制
     */
    public static int OVER_LIMIT = 2001;
    
    /**
     * 对象不存在
     */
    public static int OBJECT_NOT_FOUND = 2002;

    /**
     * 登录账号错误，需要绑定
     */
    public static int LOGIN_ACCOUNT_ERROR = 5001;

    /**
     * 验证码错误
     */
    public static int AUTH_CODE_ERROR = 5002;

    /**
     * 缺少验证码
     */
    public static int AUTH_CODE_LOCK = 5003;

    /**
     * 短信验证码发送失败
     */
    public static int AUTH_CODE_SEND_FAIL = 5004;

    /**
     * 短信验证码发送失败
     */
    public static int AUTH_CODE_FORCE_UPGRADE = 5005;

    /**
     *账户没有绑定手机号码
     */
    public static int USER_NO_BIND_MOBILE = 5006;

    /**
     * 用户已经绑定
     */
    public static int USER_ALREADY_BIND = 5007;

    /**
     * 手机号已经绑定过
     */
    public static int MOBILE_ALREADY_BIND = 5008;

    /**
     * 用户禁止删除
     */
    public static int USER_FORBID_DELETE = 5009;

    /**
     * 此用户不存在
     */
    public static int  USER_IS_NOT_EXISTS=5010;

    /**
     * 视频不存在
     */
    public static int  VIDEO_IS_NOT_EXISTS=5011;

    /**
     * TCP地址没有获取到
     */
    public static int  TCP_ADDR_NOT_EXISTS=5012;


    /**
     * 参数错误
     */
    public static int PARAMETER_ERROR = 4001;

    public static int UPLOAD_CRASH_LOG_OK = 8000;
    public static int UPLOAD_CRASH_LOG_FAIL = 8001;

    public static int UPLOAD_FILE_FAIL = 8002;
    public static int DOWNLOAD_FILE_FAIL = 8003;

    //文件不存在
    public static int DOWNLOAD_FILE_NOTEXIST = 8004;

    //文件被删除
    public static int DOWNLOAD_FILE_DELETE = 8005;

   //文件没有审核通过
    public static int DOWNLOAD_FILE_NOTAUDIT = 8006;

    /**
     * 获取短链失败
     */
    public static int SHORT_URL_FAIL = 8005;

}
