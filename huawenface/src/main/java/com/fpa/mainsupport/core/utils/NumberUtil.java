package com.fpa.mainsupport.core.utils;

import android.text.format.Formatter;

import com.fpa.mainsupport.core.android.GlobalApp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 */
public final class NumberUtil {

    // 系统函数，字符串转换 long -String (kb)
    public static String formatFileSize(long size) {
        return Formatter.formatFileSize(GlobalApp.getContext(), size);
    }

    public static boolean isNaN(double d) {
        return Math.abs(d) < 0.00001 || "NaN".equals(String.valueOf(d));
    }

    public static boolean isNull(long d) {
        return d == 0;
    }

    public static int randomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max + 1) % (max - min + 1) + min;
    }

    public static String decimalFormat(double number) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(number);
    }

    public static String formatDistance(double distance) {
        if (distance < 10 && distance - 1.0 > 0.001) {
            return Double.toString(distance).substring(0, 3) + "km";
        } else if (distance >= 10) {
            return (int) distance + "km";
        } else {
            String result = Integer.toString((int) (distance * 1000));
            return (result.length() > 3 ? result.substring(0, 3) : result) + "m";
        }
    }

    /**
     * allNumber 总数，chooseNumber 随机数目，返回随机数的数组
     */
    public static int[] randomNumArr(int allNumber, int chooseNumber) {
        if (allNumber >= chooseNumber) {
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < allNumber; i++) {
                list.add(i);
            }
            Collections.shuffle(list);
            list = list.subList(0, chooseNumber);
            int[] ins = new int[chooseNumber];
            for (int i = 0; i < chooseNumber; i++) {
                ins[i] = list.get(i);
            }
            return ins;
        }
        return null;
    }

    public static double formatDouble(double num, int scale) {
        if ("NaN".equals(String.valueOf(num)))
            return 0.0;
        BigDecimal b = new BigDecimal(num);
        Double f1 = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     * @param string
     * @return return true if string is a number,
     */
    public static boolean isNumber(String string) {
        if (null == string)
            return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }

    public static int parseInt(String status) {
        try {
            return Integer.parseInt(status);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double parseDouble(String appSize) {
        try {
            return Double.parseDouble(appSize);
        } catch (Exception e) {
            return 0;
        }
    }
}
