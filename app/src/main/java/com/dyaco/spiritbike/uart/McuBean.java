package com.dyaco.spiritbike.uart;

import androidx.annotation.NonNull;

import com.corestar.libs.device.Device;

import java.util.List;

public class McuBean implements isBusEvent {
    private int eventType;
    private Device.MODEL model;
    private List<Device.MCU_ERROR> errors;
    private int hp;
    private int wp;
    private Device.ACTION_STATUS inclineStatus;
    private Device.ACTION_STATUS levelStatus;
    private int level;
    private int incline;
    private Device.SAFE_KEY saveKeyStatus;
    private int speed;
    private int stepCount;
    private Device.DIRECTION directStatus;
    private int rpm;
    private int res;
    private int rpm1;
    private int rpm2;

    private int rpmCount;

    public int getRpmCount() {
        return rpmCount;
    }

    public void setRpmCount(int rpmCount) {
        this.rpmCount = rpmCount;
    }

    public McuBean() {
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Device.MODEL getModel() {
        return model;
    }

    public void setModel(Device.MODEL model) {
        this.model = model;
    }

    public List<Device.MCU_ERROR> getErrors() {
        return errors;
    }

    public void setErrors(List<Device.MCU_ERROR> errors) {
        this.errors = errors;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getWp() {
        return wp;
    }

    public void setWp(int wp) {
        this.wp = wp;
    }

    public Device.ACTION_STATUS getInclineStatus() {
        return inclineStatus;
    }

    public void setInclineStatus(Device.ACTION_STATUS inclineStatus) {
        this.inclineStatus = inclineStatus;
    }

    public Device.ACTION_STATUS getLevelStatus() {
        return levelStatus;
    }

    public void setLevelStatus(Device.ACTION_STATUS levelStatus) {
        this.levelStatus = levelStatus;
    }

    public int getIncline() {
        return incline;
    }

    public void setIncline(int incline) {
        this.incline = incline;
    }

    public Device.SAFE_KEY getSaveKeyStatus() {
        return saveKeyStatus;
    }

    public void setSaveKeyStatus(Device.SAFE_KEY saveKeyStatus) {
        this.saveKeyStatus = saveKeyStatus;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public Device.DIRECTION getDirectStatus() {
        return directStatus;
    }

    public void setDirectStatus(Device.DIRECTION directStatus) {
        this.directStatus = directStatus;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getRpm1() {
        return rpm1;
    }

    public void setRpm1(int rpm1) {
        this.rpm1 = rpm1;
    }

    public int getRpm2() {
        return rpm2;
    }

    public void setRpm2(int rpm2) {
        this.rpm2 = rpm2;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    @NonNull
    @Override
    public String toString() {
        return "McuBean{" +
                "model=" + model +
                ", errors=" + errors +
                ", hp=" + hp +
                ", wp=" + wp +
                ", inclineStatus=" + inclineStatus +
                ", levelStatus=" + levelStatus +
                ", level=" + level +
                ", incline=" + incline +
                ", saveKeyStatus=" + saveKeyStatus +
                ", speed=" + speed +
                ", stepCount=" + stepCount +
                ", directStatus=" + directStatus +
                ", rpm=" + rpm +
                ", res=" + res +
                ", rpm1=" + rpm1 +
                ", rpm2=" + rpm2 +
                '}';
    }
}
