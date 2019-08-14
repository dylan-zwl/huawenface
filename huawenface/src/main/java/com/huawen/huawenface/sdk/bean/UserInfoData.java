package com.huawen.huawenface.sdk.bean;

import com.huawen.huawenface.sdk.net.FitoneResult;

public class UserInfoData extends FitoneResult {
    private UserInfoBean data;

    public UserInfoBean getData() {
        return data;
    }

    public void setData(UserInfoBean data) {
        this.data = data;
    }

    public class UserInfoIdBean{
        private String $id;

        public String get$id() {
            return $id;
        }

        public void set$id(String $id) {
            this.$id = $id;
        }
    }
    public class UserInfoBean{
        UserInfoIdBean _id;
        String userId;
        String realname;
        String gender;
        String phone;
        String cardNumber;
        String isImport;
        String unionId;
        String syncFlag;
        String isUniverseVip;
        String insertTime;
        String masterId;
        String realavatar;
        String personId;
        String id;
        String birthday;
        String idCardNumber;
        String address;
        String cardType;
        String membershipId;
        String cardId;
        String type;
        String faceId;

        public String getFaceId() {
            return faceId;
        }

        public void setFaceId(String faceId) {
            this.faceId = faceId;
        }

        public UserInfoIdBean get_id() {
            return _id;
        }

        public void set_id(UserInfoIdBean _id) {
            this._id = _id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getIsImport() {
            return isImport;
        }

        public void setIsImport(String isImport) {
            this.isImport = isImport;
        }

        public String getUnionId() {
            return unionId;
        }

        public void setUnionId(String unionId) {
            this.unionId = unionId;
        }

        public String getSyncFlag() {
            return syncFlag;
        }

        public void setSyncFlag(String syncFlag) {
            this.syncFlag = syncFlag;
        }

        public String getIsUniverseVip() {
            return isUniverseVip;
        }

        public void setIsUniverseVip(String isUniverseVip) {
            this.isUniverseVip = isUniverseVip;
        }

        public String getInsertTime() {
            return insertTime;
        }

        public void setInsertTime(String insertTime) {
            this.insertTime = insertTime;
        }

        public String getMasterId() {
            return masterId;
        }

        public void setMasterId(String masterId) {
            this.masterId = masterId;
        }

        public String getRealavatar() {
            return realavatar;
        }

        public void setRealavatar(String realavatar) {
            this.realavatar = realavatar;
        }

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getIdCardNumber() {
            return idCardNumber;
        }

        public void setIdCardNumber(String idCardNumber) {
            this.idCardNumber = idCardNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getMembershipId() {
            return membershipId;
        }

        public void setMembershipId(String membershipId) {
            this.membershipId = membershipId;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
