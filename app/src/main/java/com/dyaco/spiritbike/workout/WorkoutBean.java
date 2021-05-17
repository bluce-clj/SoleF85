package com.dyaco.spiritbike.workout;

import androidx.annotation.NonNull;

import com.dyaco.spiritbike.webapi.TrainingProcessBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkoutBean implements Serializable {
    int baseProgramId;
    int programId;
    String levelDiagramNum;
    String inclineDiagramNum;
    String hrDiagramNum;
    int timeSecond;
    int maxLevel;
    int orgMaxLevel; //一開始選擇的max level
    int maxIncline;
    int orgMaxIncline; //program 的預設 max incline，圖中最長的那根
    private Date updateTime;
    private String programName;
    private String targetHeartRate;
    private long runTime; //秒
    private String avgSpeed;
    private String calories;
    private String avgRPM;
    private String avgWATT;
    private String maxWATT;
    private String avgMET;
    private String pace;
    private String avgPace;
    private String totalDistance;
    private int unit; //0公 1英

    private String avgLevel;
    private String avgIncline;

    private String avgHR;
    private String maxHR;

    private double wattAccumulate;
    private int wattFrequency;
    private int workoutMonth;

    private boolean isTemplate;

    private int currentSegment;

    private int reCount;

    public int getReCount() {
        return reCount;
    }

    public void setReCount(int reCount) {
        this.reCount = reCount;
    }

    public int getCurrentSegment() {
        return currentSegment;
    }

    public void setCurrentSegment(int currentSegment) {
        this.currentSegment = currentSegment;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public int getOrgMaxIncline() {
        return orgMaxIncline;
    }

    public void setOrgMaxIncline(int orgMaxIncline) {
        this.orgMaxIncline = orgMaxIncline;
    }

    public int getOrgMaxLevel() {
        return orgMaxLevel;
    }

    public void setOrgMaxLevel(int orgMaxLevel) {
        this.orgMaxLevel = orgMaxLevel;
    }

    private List<DiagramBean> diagramInclineList = new ArrayList<>();
    private List<DiagramBean> diagramLevelList = new ArrayList<>();
    private List<DiagramBean> diagramHRList = new ArrayList<>();


    public double getWattAccumulate() {
        return wattAccumulate;
    }

    public void setWattAccumulate(double wattAccumulate) {
        this.wattAccumulate = wattAccumulate;
    }

    public int getWattFrequency() {
        return wattFrequency;
    }

    public void setWattFrequency(int wattFrequency) {
        this.wattFrequency = wattFrequency;
    }

    public int getWorkoutMonth() {
        return workoutMonth;
    }

    public void setWorkoutMonth(int workoutMonth) {
        this.workoutMonth = workoutMonth;
    }

    public int getBaseProgramId() {
        return baseProgramId;
    }

    public void setBaseProgramId(int baseProgramId) {
        this.baseProgramId = baseProgramId;
    }

    public String getMaxWATT() {
        return maxWATT;
    }

    public void setMaxWATT(String maxWATT) {
        this.maxWATT = maxWATT;
    }

    public List<DiagramBean> getDiagramHRList() {
        return diagramHRList;
    }

    public void setDiagramHRList(List<DiagramBean> diagramHRList) {
        this.diagramHRList = diagramHRList;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public String getAvgPace() {
        return avgPace;
    }

    public void setAvgPace(String avgPace) {
        this.avgPace = avgPace;
    }

    public String getHrDiagramNum() {
        return hrDiagramNum;
    }

    public void setHrDiagramNum(String hrDiagramNum) {
        this.hrDiagramNum = hrDiagramNum;
    }

    public String getAvgHR() {
        return avgHR;
    }

    public void setAvgHR(String avgHR) {
        this.avgHR = avgHR;
    }

    public String getMaxHR() {
        return maxHR;
    }

    public void setMaxHR(String maxHR) {
        this.maxHR = maxHR;
    }

    private TrainingProcessBean trainingProcessBean;

    public TrainingProcessBean getTrainingProcessBean() {
        return trainingProcessBean;
    }

    public void setTrainingProcessBean(TrainingProcessBean trainingProcessBean) {
        this.trainingProcessBean = trainingProcessBean;
    }

    public String getAvgLevel() {
        return avgLevel;
    }

    public void setAvgLevel(String avgLevel) {
        this.avgLevel = avgLevel;
    }

    public String getAvgIncline() {
        return avgIncline;
    }

    public void setAvgIncline(String avgIncline) {
        this.avgIncline = avgIncline;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getAvgRPM() {
        return avgRPM;
    }

    public void setAvgRPM(String avgRPM) {
        this.avgRPM = avgRPM;
    }

    public String getAvgWATT() {
        return avgWATT;
    }

    public void setAvgWATT(String avgWATT) {
        this.avgWATT = avgWATT;
    }

    public String getAvgMET() {
        return avgMET;
    }

    public void setAvgMET(String avgMET) {
        this.avgMET = avgMET;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public String getTargetHeartRate() {
        return targetHeartRate;
    }

    public void setTargetHeartRate(String targetHeartRate) {
        this.targetHeartRate = targetHeartRate;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<DiagramBean> getDiagramInclineList() {
        return diagramInclineList;
    }

    public void setDiagramInclineList(List<DiagramBean> diagramInclineList) {
        this.diagramInclineList = diagramInclineList;
    }

    public List<DiagramBean> getDiagramLevelList() {
        return diagramLevelList;
    }

    public void setDiagramLevelList(List<DiagramBean> diagramLevelList) {
        this.diagramLevelList = diagramLevelList;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getLevelDiagramNum() {
        return levelDiagramNum;
    }

    public void setLevelDiagramNum(String levelDiagramNum) {
        this.levelDiagramNum = levelDiagramNum;
    }

    public String getInclineDiagramNum() {
        return inclineDiagramNum;
    }

    public void setInclineDiagramNum(String inclineDiagramNum) {
        this.inclineDiagramNum = inclineDiagramNum;
    }

    public int getTimeSecond() {
        return timeSecond;
    }

    public void setTimeSecond(int timeSecond) {
        this.timeSecond = timeSecond;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMaxIncline() {
        return maxIncline;
    }

    public void setMaxIncline(int maxIncline) {
        this.maxIncline = maxIncline;
    }

    @NonNull
    @Override
    public String toString() {
        return "WorkoutBean{" +
                "programId=" + programId +
                ", levelDiagramNum='" + levelDiagramNum + '\'' +
                ", inclineDiagramNum='" + inclineDiagramNum + '\'' +
                ", timeSecond=" + timeSecond +
                ", maxLevel=" + maxLevel +
                ", updateTime=" + updateTime +
                ", programName='" + programName + '\'' +
                ", targetHeartRate='" + targetHeartRate + '\'' +
                ", runTime=" + runTime +
                ", avgSpeed='" + avgSpeed + '\'' +
                ", calories='" + calories + '\'' +
                ", avgRPM='" + avgRPM + '\'' +
                ", avgWATT='" + avgWATT + '\'' +
                ", avgMET='" + avgMET + '\'' +
                ", totalDistance='" + totalDistance + '\'' +
                ", unit=" + unit +
                ", avgLevel='" + avgLevel + '\'' +
                ", avgIncline='" + avgIncline + '\'' +
                ", avgHR='" + avgHR + '\'' +
                ", maxHR='" + maxHR + '\'' +
                ", trainingProcessBean=" + trainingProcessBean +
                ", diagramInclineList=" + diagramInclineList +
                ", diagramLevelList=" + diagramLevelList +
                '}';
    }
}
