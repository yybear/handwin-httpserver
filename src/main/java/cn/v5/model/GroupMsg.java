package cn.v5.model;



/**
 * User: sunchj
 * Date: 13-7-19
 * Time: 上午10:39
 */
public class GroupMsg {


    public String id;

    /**
     * 发送者ID
     */
    public String senderNameMd5;

    /**
     * 群组id
     */
    public String groupId;


    /**
     * 创建时间
     */
    public Long createTime;

    /**
     * 群组内容
     */
    public byte[] content;

    /**
     * 创建时间
     */
    public Integer status;
}
