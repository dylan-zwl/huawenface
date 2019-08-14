package com.huawen.huawenface.sdk.net.request;

import java.util.List;

public class ImageRecogRequest extends BaseRequest {
    private String image;
    private List<String> repositoryIds;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getRepositoryIds() {
        return repositoryIds;
    }

    public void setRepositoryIds(List<String> repositoryIds) {
        this.repositoryIds = repositoryIds;
    }
}
