package com.huawen.huawenface.sdk.bean;

/**
 * Created by ZengYinan.
 * Date: 2018/9/18 0:28
 * Email: 498338021@qq.com
 * Desc:
 */
public class DataConfig {
    public String groupId;
    public int width;
    public int height;
    public int marginLeft;
    public int marginTop;
    public int waitSecond;//等待多少秒开始检测
    public int sensitivity;//灵敏度，从开始检测时间到跳转到另一个页面再回来的时间
    public int recognitionDegree;//本地过滤
    public int scale;//指定支持检测的最小人脸尺寸,有效值范围[2,32],推荐值16

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getRecognitionDegree() {
        return recognitionDegree;
    }

    public void setRecognitionDegree(int recognitionDegree) {
        this.recognitionDegree = recognitionDegree;
    }

    public int getWaitSecond() {
        return waitSecond;
    }

    public void setWaitSecond(int waitSecond) {
        this.waitSecond = waitSecond;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

}
