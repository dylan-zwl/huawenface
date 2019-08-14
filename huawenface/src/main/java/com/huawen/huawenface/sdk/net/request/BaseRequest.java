package com.huawen.huawenface.sdk.net.request;

import com.fpa.mainsupport.core.utils.Log;
import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;

import java.lang.reflect.Field;
import java.util.List;

public class BaseRequest {
    public HttpParams toParams(Class clazz) {
        HttpParams data = new HttpParams();
        autoHandleParams(clazz, data);
        Log.e("请求头参数：",data.toString());
        return data;
    }

    /**
     * auto handle the params which  the API needed by reflection
     *
     * @param clazz
     * @param data
     */
    protected HttpParams autoHandleParams(Class clazz, HttpParams data) {
        Field[] fields = this.getClass().getDeclaredFields();
        handleFields(clazz, fields, data);
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            handleFields(clazz, clazz.getDeclaredFields(), data);
        }
        return data;
    }
    /**
     * get the name of getXXX(),base on the field
     *
     * @param field
     */
    public static String generateGetMethod(Field field) {
        String fieldName = field.getName();
        field.setAccessible(true);
        boolean bool = field.getType().equals(boolean.class);//if the type of field is boolean ,get method is isXXX();
        return (bool ? "is" :"get") + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
    }

    /**
     * get the name of setXXX(),generate method name base ?on the field
     *
     * @param field
     */
    public static String generateSetMethod(Field field) {
        String fieldName = field.getName();
        field.setAccessible(true);//set the field to be access while change the field's value
        return "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);//build the setXXX() string rely on the field
    }
    private void handleFields(Class clazz, Field[] fields, HttpParams data) {
        String getMethodName, setMethodName;
        for (Field field : fields) {
            getMethodName = generateGetMethod(field);
            try {
                if ("busy".equals(field.getName()))
                    continue;
                Object out = clazz.getMethod(getMethodName, new Class[]{}).invoke(this, new Object[]{});
                String value = String.valueOf(out);
                if ((Integer.class.equals(field.getType()) && Integer.valueOf(value) == 0))
                    continue;
                if (!containsParams(field.getName(), data)) {
                    if ((List.class.equals(field.getType()))) {
                        data.put(field.getName(), new Gson().toJson(out));

                    } else
                        data.put(field.getName(), value);
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    /**
     * 去除重复参数
     *
     * @param name
     * @param data
     * @return if the Params　List Contains this Key ,return true,else return false
     */
    private boolean containsParams(String name, HttpParams data) {
        return data.urlParamsMap.containsKey(name);
//        for (BasicNameValuePair item : data) {
//            if (item.getName().equals(name))
//                return true;
//        }
//        return false;
    }

    public static HttpParams toString(HttpParams data) {
        if (Log.isOpen()) {
            System.out.println();
            System.out.println("===================Entry Content==================");
//            for (BasicNameValuePair item : data) {
//                System.out.println(item.getName() + ":" + item.getValue() + "  ");
//            }
            Log.d(data.toString());
            System.out.println("===================Entry Content End==================");
        }
        return data;
    }
}
