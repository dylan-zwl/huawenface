package com.huawen.huawenface.sdk.net;

public class FitoneResult extends Result {
    private String cn;

    @Override
    public boolean isSuccess() {
        return "0".equals(cn)||(null!=getCode()&&super.isSuccess());
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }
}
