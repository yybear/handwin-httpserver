package cn.v5.code;

import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: piguangtao
 * Date: 13-9-4
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
public interface SystemConstant {

    /**
     * 待发送消息表中 发送的消息接受类型 个人
     */
    String TO_SEND_MESSAGE_PERSION ="1";

    /**
     * 待发送消息表中 发送的消息接受类型 群组
     */
    String TO_SEND_MESSAGE_GROUP ="2";

    /**
     * 匹配一个或用,分割的多个手机号码
     */
    Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(147)|(15[^4,\\D])|(18[0,5-9]))\\d{8}(,((13[0-9])|(147)|(15[^4,\\D])|(18[0,5-9]))\\d{8})*$");

    /**
     * 账号登录标示
     */
    Integer LOGIN_FLAG_USERNAME = 0;

    /**
     * 设备登录标示
     */
    Integer LOGIN_FLAG_DEVICE = 1;


    /**
     * 用户状态-有效
     */
    String  USER_STATUS_VALID = "1";


    /**
     * 用户状态-删除
     */
    String USER_STATUS_DELETE = "-1";

    /**
     * 修改用户的mq消息
     */
    byte MQ_TYPE_USER_MODIFY = 1;

    String MESSAGE_TYPE_TEXT = "text";

    String MESSAGE_TYPE_CARDNAME = "cardname";

    String MESSAGE_TYPE_PICURL = "picurl";

    String MESSAGE_TYPE_audio_sm = "audio_sm";

    String MESSAGE_TYPE_video_sm = "video_sm";

    String MESSAGE_TYPE_VIDEO_CALL = "video_call";

    String MESSAGE_TYPE_AUDIO_CALL = "audio_call";


}
