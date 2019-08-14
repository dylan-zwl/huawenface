package com.huawen.huawenface.sdk.bean;

public class CreateBean {
//    {
//        "data":{
//        "person_id":"person0",
//                "suc_group":2,
//                "suc_face":1,
//                "session_id":"",
//                "face_id":"1009550071676600319",
//                "group_ids":["tencent", "qq"]
//    },
//        "code":0,
//            "message":"OK"
//    }
    private int code;
    private String message;
    private PersonBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PersonBean getData() {
        return data;
    }

    public void setData(PersonBean data) {
        this.data = data;
    }

    public class PersonBean {
        public String person_id;
        public int  suc_group;
        public int suc_face;
        public String face_id;

        public int getSuc_group() {
            return suc_group;
        }

        public void setSuc_group(int suc_group) {
            this.suc_group = suc_group;
        }

        public int getSuc_face() {
            return suc_face;
        }

        public void setSuc_face(int suc_face) {
            this.suc_face = suc_face;
        }

        public String getFace_id() {
            return face_id;
        }

        public void setFace_id(String face_id) {
            this.face_id = face_id;
        }

        public String getPerson_id() {
            return person_id;
        }

        public void setPerson_id(String person_id) {
            this.person_id = person_id;
        }
    }
}
