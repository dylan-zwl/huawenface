package com.huawen.huawenface.sdk.bean;

import com.huawen.huawenface.sdk.net.Result;

import java.util.List;

public class ImageRecogData extends Result {
    private List<ImageRecogItem> data;

    public List<ImageRecogItem> getData() {
        return data;
    }

    public void setData(List<ImageRecogItem> data) {
        this.data = data;
    }

    public class ImageRecogItem{
        private String baseImagePath;
        private String deviceId;
        private String deviceName;
        private String faceId;
        private String personId;
        private String personName;
        private String recogCost;
        private String recogImagePath;
        private String recogTime;
        private String repositoryName;
        private String similarity;

        public String getBaseImagePath() {
            return baseImagePath;
        }

        public void setBaseImagePath(String baseImagePath) {
            this.baseImagePath = baseImagePath;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getFaceId() {
            return faceId;
        }

        public void setFaceId(String faceId) {
            this.faceId = faceId;
        }

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getPersonName() {
            return personName;
        }

        public void setPersonName(String personName) {
            this.personName = personName;
        }

        public String getRecogCost() {
            return recogCost;
        }

        public void setRecogCost(String recogCost) {
            this.recogCost = recogCost;
        }

        public String getRecogImagePath() {
            return recogImagePath;
        }

        public void setRecogImagePath(String recogImagePath) {
            this.recogImagePath = recogImagePath;
        }

        public String getRecogTime() {
            return recogTime;
        }

        public void setRecogTime(String recogTime) {
            this.recogTime = recogTime;
        }

        public String getRepositoryName() {
            return repositoryName;
        }

        public void setRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
        }

        public String getSimilarity() {
            return similarity;
        }

        public void setSimilarity(String similarity) {
            this.similarity = similarity;
        }
    }
}
