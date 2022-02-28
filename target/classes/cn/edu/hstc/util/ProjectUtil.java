package cn.edu.hstc.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 名称格式验证
     *
     * @param name 名称
     * @return 验证结果
     */
    public static boolean isRightName(String name) {
        /*
        P:中文标点符号；
        L：字母；
        M：标记符号（一般不会单独出现）；
        Z：分隔符（比如空格、换行等）；
        S：英文标点符号（比如数学符号、货币符号等）；
        N：数字（比如阿拉伯数字、罗马数字等）；
        C：其他字符
        */
        Pattern pattern = Pattern.compile("\\pP|\\pS|\\pN|\\pZ");
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '(' || name.charAt(i) == ')' || name.charAt(i) == '（' || name.charAt(i) == '）') {
                continue;
            }
            Matcher matcher = pattern.matcher(String.valueOf(name.charAt(i)));
            if (matcher.matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 手机号码/电话验证
     *
     * @param phoneNum 手机号码/电话
     * @return 验证结果
     */
    public static boolean isPhoneNum(String phoneNum) {
        boolean flag = false;
        String regex = "^[1]([3-5]|[7-9])[0-9]\\d{8}$";
        if (phoneNum.matches(regex)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 密码格式验证 密码为8-16个字母和数字组成(可包括特殊字符)
     */
    public static boolean isRightPsw(String psw){
        /*
        P:中文标点符号；
        L：字母；
        M：标记符号（一般不会单独出现）；
        Z：分隔符（比如空格、换行等）；
        S：英文标点符号（比如数学符号、货币符号等）；
        N：数字（比如阿拉伯数字、罗马数字等）；
        C：其他字符
        */
        boolean flag = false;
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[a-zA-Z\\d\\pZ\\pS\\pP]{8,16}$";
        if (psw.matches(regex)) {
            flag = true;
        }
        return flag;
    }
}
