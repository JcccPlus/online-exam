package cn.edu.hstc.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 项目专用工具类
 */
public class ProjectUtil {
    /**
     * MD5加密
     */
    public static String getMD5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * UUID随机码
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
