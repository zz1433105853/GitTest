package com.ty.common.utils;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tykfkf02 on 2016/6/29.
 */
public class MD5Utils {
    public static String getMD5(String content){
        MessageDigest md5= null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bytes2Hex(md5.digest(content.getBytes()));
    }

    public static String getMD5(String content, String charset){
        MessageDigest md5= null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            return bytes2Hex(md5.digest(content.getBytes(charset)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
}
