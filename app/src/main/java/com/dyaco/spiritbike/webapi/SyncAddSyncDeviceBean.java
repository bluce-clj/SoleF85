package com.dyaco.spiritbike.webapi;

public class SyncAddSyncDeviceBean {


    private SysResponseMessageBean sys_response_message;
    private String sys_response_data;

    public SysResponseMessageBean getSys_response_message() {
        return sys_response_message;
    }

    public void setSys_response_message(SysResponseMessageBean sys_response_message) {
        this.sys_response_message = sys_response_message;
    }

    public String getSys_response_data() {
        return sys_response_data;
    }

    public void setSys_response_data(String sys_response_data) {
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
}
