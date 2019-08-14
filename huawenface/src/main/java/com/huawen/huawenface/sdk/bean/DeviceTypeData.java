package com.huawen.huawenface.sdk.bean;

import com.huawen.huawenface.sdk.net.FitoneResult;

import java.util.List;

public class DeviceTypeData extends FitoneResult {
    private List<DeviceTypeItemBean> data;

    public List<DeviceTypeItemBean> getData() {
        return data;
    }

    public void setData(List<DeviceTypeItemBean> data) {
        this.data = data;
    }
}
