package com.dyaco.spiritbike.webapi;

public class SyncGetUserInfoBean {


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
        private String account_no;
        private String account;
        private String password;
        private String name;
        private String email;
        private String sex;
        private double height;
        private double weight;
        private String birthday;
        private String isadmin;
        private Object authority;
        private Object regist_type;
        private String unit_type;
        private Object lang_code;
        private Object edm_favorite_brand;
        private String head_photo_url;
        private Object device_id;
        private Object ORG_NO;
        private Object api_token;

        public String getAccount_no() {
            return account_no;
        }

        public void setAccount_no(String account_no) {
            this.account_no = account_no;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getIsadmin() {
            return isadmin;
        }

        public void setIsadmin(String isadmin) {
            this.isadmin = isadmin;
        }

        public Object getAuthority() {
            return authority;
        }

        public void setAuthority(Object authority) {
            this.authority = authority;
        }

        public Object getRegist_type() {
            return regist_type;
        }

        public void setRegist_type(Object regist_type) {
            this.regist_type = regist_type;
        }

        public String getUnit_type() {
            return unit_type;
        }

        public void setUnit_type(String unit_type) {
            this.unit_type = unit_type;
        }

        public Object getLang_code() {
            return lang_code;
        }

        public void setLang_code(Object lang_code) {
            this.lang_code = lang_code;
        }

        public Object getEdm_favorite_brand() {
            return edm_favorite_brand;
        }

        public void setEdm_favorite_brand(Object edm_favorite_brand) {
            this.edm_favorite_brand = edm_favorite_brand;
        }

        public String getHead_photo_url() {
            return head_photo_url;
        }

        public void setHead_photo_url(String head_photo_url) {
            this.head_photo_url = head_photo_url;
        }

        public Object getDevice_id() {
            return device_id;
        }

        public void setDevice_id(Object device_id) {
            this.device_id = device_id;
        }

        public Object getORG_NO() {
            return ORG_NO;
        }

        public void setORG_NO(Object ORG_NO) {
            this.ORG_NO = ORG_NO;
        }

        public Object getApi_token() {
            return api_token;
        }

        public void setApi_token(Object api_token) {
            this.api_token = api_token;
        }
    }
}
