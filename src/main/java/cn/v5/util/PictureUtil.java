package cn.v5.util;


/**
 * Created by IntelliJ IDEA.
 * User:sunchj
 * Date: 13-9-5
 * Time: 下午3:13
 */
public class PictureUtil {

    public static final String SUFFIX_THUMB = ".thumb";

    public static String genPicturePath(String fileName) {
        return ConfigUtils.getString("avatar.storage.path") + fileName.substring(0, 2) + "/" + fileName.substring(2, 4) + "/";
    }

    public static String genPictureThumbUrl(String fileName) {
        return ConfigUtils.getString("base.url") + ConfigUtils.getString("avatar.url") + fileName;
    }

}
