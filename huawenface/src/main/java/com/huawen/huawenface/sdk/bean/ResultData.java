package com.huawen.huawenface.sdk.bean;

import java.util.List;

/**
 * Created by ZengYinan.
 * Date: 2018/9/11 22:51
 * Email: 498338021@qq.com
 * Desc:
 */
public class ResultData {

    /**
     * code : 0
     * message : OK
     * data : {"session_id":"","candidates":[{"person_id":"nsVGj9jCFBUp.1735.8a80a80e4359a29e1b69c2da4159ba9a","face_id":"2723331580851453421","confidence":39,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.69d941d1bde9f0b86e68dc100c9a3779","face_id":"2685288435169675578","confidence":34,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.4750baa6590ba704d4c588d3a51a2902","face_id":"2537383169407083302","confidence":33,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.bd5cfd910c8acfe591dfa21133ad816d","face_id":"2659420528292451686","confidence":25,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.a6db2f34a997409a1f86cfc5e9cdf1ad","face_id":"2685293487318944133","confidence":23,"tag":""}],"time_ms":207,"group_size":47}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * session_id :
         * candidates : [{"person_id":"nsVGj9jCFBUp.1735.8a80a80e4359a29e1b69c2da4159ba9a","face_id":"2723331580851453421","confidence":39,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.69d941d1bde9f0b86e68dc100c9a3779","face_id":"2685288435169675578","confidence":34,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.4750baa6590ba704d4c588d3a51a2902","face_id":"2537383169407083302","confidence":33,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.bd5cfd910c8acfe591dfa21133ad816d","face_id":"2659420528292451686","confidence":25,"tag":""},{"person_id":"nsVGj9jCFBUp.1735.a6db2f34a997409a1f86cfc5e9cdf1ad","face_id":"2685293487318944133","confidence":23,"tag":""}]
         * time_ms : 207
         * group_size : 47
         */

        private String session_id;
//        private int time_ms;
//        private int group_size;
        private List<CandidatesBean> candidates;

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        public List<CandidatesBean> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<CandidatesBean> candidates) {
            this.candidates = candidates;
        }

        public static class CandidatesBean {
            /**
             * person_id : nsVGj9jCFBUp.1735.8a80a80e4359a29e1b69c2da4159ba9a
             * face_id : 2723331580851453421
             * confidence : 39
             * tag :
             */

            private String person_id;
            private String face_id;
            private int confidence;
            private String tag;

            public String getPerson_id() {
                return person_id;
            }

            public void setPerson_id(String person_id) {
                this.person_id = person_id;
            }

            public String getFace_id() {
                return face_id;
            }

            public void setFace_id(String face_id) {
                this.face_id = face_id;
            }

            public int getConfidence() {
                return confidence;
            }

            public void setConfidence(int confidence) {
                this.confidence = confidence;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }
        }
    }
}
