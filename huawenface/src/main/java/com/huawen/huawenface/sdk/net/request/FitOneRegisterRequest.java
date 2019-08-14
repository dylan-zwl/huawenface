package com.huawen.huawenface.sdk.net.request;

public class FitOneRegisterRequest extends FitOneBaseRequest{
    private String file;
    private String faceId;

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
