package com.huawen.huawenface.sdk.utils;

import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ZengYinan.
 * Date: 2018/9/10 13:35
 * Email: 498338021@qq.com
 * Desc:
 */
public class HMACSHA1 {
    /**
     * 算法标识
     */
    private static final String HMAC_SHA1 = "HmacSHA1";
    /**
     * 加密
     * @param data 要加密的数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static byte[] getSignature(String data, String key) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA1);
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
                mac.getAlgorithm());
        mac.init(signingKey);
        return mac.doFinal(data.getBytes());
    }
    /**
     * 生成随机数字
     * @param length
     * @return
     */
    public static String genRandomNum(int length){
        int  maxNum = 62;
        int i;
        int count = 0;
        char[] str = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < length){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count ++;
            }
        }
        return pwd.toString();
    }
}
