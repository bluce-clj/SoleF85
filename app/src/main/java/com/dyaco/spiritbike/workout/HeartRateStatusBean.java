package com.dyaco.spiritbike.workout;

import androidx.annotation.NonNull;

public class HeartRateStatusBean {
    private WorkoutDashboardFragment.StatusEnum mode; //0:IDLE(閒置) 1:REACHING(到達中), 2:MAINTAINING(維持)
    private double watt;
    private int targetHR;
    private int targetHrMax; //220 - age
    private int currentHR;
    private int rpm;
    private int ROR1;
    private int ROR2;
    private int GOAL;
    private int level;
    private int HR0;
    private int HR60;
    private int HR75;
    private boolean ROR1done;
    private boolean ROR2done;
    private boolean wattDone;
    private boolean ROR1Holding;
    private boolean ROR2Holding;
    private boolean maintaining;

    public boolean isMaintaining() {
        return maintaining;
    }

    public void setMaintaining(boolean maintaining) {
        this.maintaining = maintaining;
    }

    public boolean isROR2done() {
        return ROR2done;
    }

    public void setROR2done(boolean ROR2done) {
        this.ROR2done = ROR2done;
    }

    public boolean isROR2Holding() {
        return ROR2Holding;
    }

    public void setROR2Holding(boolean ROR2Holding) {
        this.ROR2Holding = ROR2Holding;
    }

    public boolean isROR1Holding() {
        return ROR1Holding;
    }

    public void setROR1Holding(boolean ROR1Holding) {
        this.ROR1Holding = ROR1Holding;
    }

    public boolean isWattDone() {
        return wattDone;
    }

    public void setWattDone(boolean wattDone) {
        this.wattDone = wattDone;
    }

    public boolean isROR1done() {
        return ROR1done;
    }

    public void setROR1done(boolean ROR1done) {
        this.ROR1done = ROR1done;
    }

    public WorkoutDashboardFragment.StatusEnum getMode() {
        return mode;
    }

    public void setMode(WorkoutDashboardFragment.StatusEnum mode) {
        this.mode = mode;
    }

    public double getWatt() {
        return watt;
    }

    public void setWatt(double watt) {
        this.watt = watt;
    }

    public int getTargetHR() {
        return targetHR;
    }

    public void setTargetHR(int targetHR) {
        this.targetHR = targetHR;
    }

    public int getTargetHrMax() {
        return targetHrMax;
    }

    public void setTargetHrMax(int targetHrMax) {
        this.targetHrMax = targetHrMax;
    }

    public int getCurrentHR() {
        return currentHR;
    }

    public void setCurrentHR(int currentHR) {
        this.currentHR = currentHR;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public int getROR1() {
        return ROR1;
    }

    public void setROR1(int ROR1) {
        this.ROR1 = ROR1;
    }

    public int getROR2() {
        return ROR2;
    }

    public void setROR2(int ROR2) {
        this.ROR2 = ROR2;
    }

    public int getGOAL() {
        return GOAL;
    }

    public void setGOAL(int GOAL) {
        this.GOAL = GOAL;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHR0() {
        return HR0;
    }

    public void setHR0(int HR0) {
        this.HR0 = HR0;
    }

    public int getHR60() {
        return HR60;
    }

    public void setHR60(int HR60) {
        this.HR60 = HR60;
    }

    public int getHR75() {
        return HR75;
    }

    public void setHR75(int HR75) {
        this.HR75 = HR75;
    }


    @NonNull
    @Override
    public String toString() {
        return "HeartRateStatusBean{" +
                "mode=" + mode +
                ", watt=" + watt +
                ", targetHR=" + targetHR +
                ", targetHrMax=" + targetHrMax +
                ", currentHR=" + currentHR +
                ", rpm=" + rpm +
                ", ROR1=" + ROR1 +
                ", ROR2=" + ROR2 +
                ", GOAL=" + GOAL +
                ", level=" + level +
                ", HR0=" + HR0 +
                ", HR60=" + HR60 +
                ", HR75=" + HR75 +
                ", ROR1done=" + ROR1done +
                ", ROR2done=" + ROR2done +
                ", wattDone=" + wattDone +
                ", ROR1Holding=" + ROR1Holding +
                ", ROR2Holding=" + ROR2Holding +
                ", maintaining=" + maintaining +
                '}';
    }
}
