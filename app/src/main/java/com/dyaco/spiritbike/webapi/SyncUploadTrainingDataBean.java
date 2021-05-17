package com.dyaco.spiritbike.webapi;

public class SyncUploadTrainingDataBean {


    private SysResponseMessageBean sys_response_message;
    private SysResponseDataBean sys_response_data;

    public SysResponseMessageBean getSys_response_message() {
        return sys_response_message;
    }

    public void setSys_response_message(SysResponseMessageBean sys_response_message) {
        this.sys_response_message = sys_response_message;
    }

    public SysResponseDataBean getSys_response_data() {
        return sys_response_data;
    }

    public void setSys_response_data(SysResponseDataBean sys_response_data) {
        this.sys_response_data = sys_response_data;
    }

    public static class SysResponseMessageBean {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class SysResponseDataBean {
        private String trainh_no;

        public String getTrainh_no() {
            return trainh_no;
        }

        public void setTrainh_no(String trainh_no) {
            this.trainh_no = trainh_no;
        }
    }
}
