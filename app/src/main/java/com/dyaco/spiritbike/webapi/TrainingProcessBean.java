package com.dyaco.spiritbike.webapi;

import java.io.Serializable;
import java.util.List;

public class TrainingProcessBean implements Serializable {

    private List<SysResponseDataBean> sys_response_data;

    public List<SysResponseDataBean> getSys_response_data() {
        return sys_response_data;
    }

    public void setSys_response_data(List<SysResponseDataBean> sys_response_data) {
        this.sys_response_data = sys_response_data;
    }

    public static class SysResponseDataBean implements Serializable {
        private String total_workout_time;
        private String total_timeleft;
        private double now_hr;
        private double total_distance;
        private String now_pace;
        private double total_calorie;
        private double now_speed;
        private double now_incline;
        private double now_level;
        private double now_watt;
        private double total_min_spm;
        private double total_spm;
        private double avg_rpm;
        private double total_floor;
        private double total_elevation;
        private double total_steps;
        private double total_cur_spm;
        private double total_avg_spm;

        public String getTotal_workout_time() {
            return total_workout_time;
        }

        public void setTotal_workout_time(String total_workout_time) {
            this.total_workout_time = total_workout_time;
        }

        public String getTotal_timeleft() {
            return total_timeleft;
        }

        public void setTotal_timeleft(String total_timeleft) {
            this.total_timeleft = total_timeleft;
        }

        public double getNow_hr() {
            return now_hr;
        }

        public void setNow_hr(double now_hr) {
            this.now_hr = now_hr;
        }

        public double getTotal_distance() {
            return total_distance;
        }

        public void setTotal_distance(double total_distance) {
            this.total_distance = total_distance;
        }

        public String getNow_pace() {
            return now_pace;
        }

        public void setNow_pace(String now_pace) {
            this.now_pace = now_pace;
        }

        public double getTotal_calorie() {
            return total_calorie;
        }

        public void setTotal_calorie(double total_calorie) {
            this.total_calorie = total_calorie;
        }

        public double getNow_speed() {
            return now_speed;
        }

        public void setNow_speed(double now_speed) {
            this.now_speed = now_speed;
        }

        public double getNow_incline() {
            return now_incline;
        }

        public void setNow_incline(double now_incline) {
            this.now_incline = now_incline;
        }

        public double getNow_level() {
            return now_level;
        }

        public void setNow_level(double now_level) {
            this.now_level = now_level;
        }

        public double getNow_watt() {
            return now_watt;
        }

        public void setNow_watt(double now_watt) {
            this.now_watt = now_watt;
        }

        public double getTotal_min_spm() {
            return total_min_spm;
        }

        public void setTotal_min_spm(double total_min_spm) {
            this.total_min_spm = total_min_spm;
        }

        public double getTotal_spm() {
            return total_spm;
        }

        public void setTotal_spm(double total_spm) {
            this.total_spm = total_spm;
        }

        public double getAvg_rpm() {
            return avg_rpm;
        }

        public void setAvg_rpm(double avg_rpm) {
            this.avg_rpm = avg_rpm;
        }

        public double getTotal_floor() {
            return total_floor;
        }

        public void setTotal_floor(double total_floor) {
            this.total_floor = total_floor;
        }

        public double getTotal_elevation() {
            return total_elevation;
        }

        public void setTotal_elevation(double total_elevation) {
            this.total_elevation = total_elevation;
        }

        public double getTotal_steps() {
            return total_steps;
        }

        public void setTotal_steps(double total_steps) {
            this.total_steps = total_steps;
        }

        public double getTotal_cur_spm() {
            return total_cur_spm;
        }

        public void setTotal_cur_spm(double total_cur_spm) {
            this.total_cur_spm = total_cur_spm;
        }

        public double getTotal_avg_spm() {
            return total_avg_spm;
        }

        public void setTotal_avg_spm(double total_avg_spm) {
            this.total_avg_spm = total_avg_spm;
        }

        @Override
        public String toString() {
            return "SysResponseDataBean{" +
                    "total_workout_time='" + total_workout_time + '\'' +
                    ", total_timeleft='" + total_timeleft + '\'' +
                    ", now_hr=" + now_hr +
                    ", total_distance=" + total_distance +
                    ", now_pace='" + now_pace + '\'' +
                    ", total_calorie=" + total_calorie +
                    ", now_speed=" + now_speed +
                    ", now_incline=" + now_incline +
                    ", now_level=" + now_level +
                    ", now_watt=" + now_watt +
                    ", total_min_spm=" + total_min_spm +
                    ", total_spm=" + total_spm +
                    ", avg_rpm=" + avg_rpm +
                    ", total_floor=" + total_floor +
                    ", total_elevation=" + total_elevation +
                    ", total_steps=" + total_steps +
                    ", total_cur_spm=" + total_cur_spm +
                    ", total_avg_spm=" + total_avg_spm +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TrainingProcessBean{" +
                "sys_response_data=" + sys_response_data.toString() +
                '}';
    }
}
