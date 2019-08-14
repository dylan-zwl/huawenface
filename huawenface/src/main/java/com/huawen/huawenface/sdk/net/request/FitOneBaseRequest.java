package com.huawen.huawenface.sdk.net.request;

import com.huawen.huawenface.sdk.Constants;
import com.huawen.huawenface.sdk.Global;

public class FitOneBaseRequest extends BaseRequest {
    private String group_id= Global.getSpString(Constants.Sp.SP_GROUP_ID,"1735");//"1735";//测试group_id
    private String groupId= Global.getSpString(Constants.Sp.SP_GROUP_ID,"1735");//"1735";//测试group_id

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
