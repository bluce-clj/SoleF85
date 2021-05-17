package com.dyaco.spiritbike.support.room.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = HistoryEntity.HISTORY,
        indices = {@Index("historyParentUid")},
        foreignKeys = @ForeignKey(entity = UserProfileEntity.class,
        parentColumns = "uid",
        childColumns = "historyParentUid",
        onDelete = CASCADE))

public class HistoryEntity implements Serializable {
    public static final String HISTORY = "history";
    @PrimaryKey(autoGenerate = true)
    private int uid;
    public String historyName;
    public int historyParentUid;
    private int level;
    private int time;
    private String diagramLevel;
    private String diagramIncline;
    private String diagramHR;
    private int someData;
    private Date updateTime;
    private int baseProgramId;

    private String avgSpeed;
    private String calories;
    private String avgRPM;
    private String avgWATT;
    private String avgMET;
    private String totalDistance;
    private long runTime;
    private int unit;
    private String avgLevel;
    private String maxLevel;
    private String avgIncline;
    private String maxIncline;
    private String maxWATT;
    private boolean uploaded; //0 false, 1 true

    private String avgHR;
    private String maxHR;

    private String pace;
    private String avgPace;

    public String getMaxIncline() {
        return maxIncline;
    }

    public void setMaxIncline(String maxIncline) {
        this.maxIncline = maxIncline;
    }

    public String getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getMaxWATT() {
        return maxWATT;
    }

    public void setMaxWATT(String maxWATT) {
        this.maxWATT = maxWATT;
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

    private String trainingProcessData;

    public String getTrainingProcessData() {
        return trainingProcessData;
    }

    public void setTrainingProcessData(String trainingProcessData) {
        this.trainingProcessData = trainingProcessData;
    }

    public String getDiagramHR() {
        return diagramHR;
    }

    public void setDiagramHR(String diagramHR) {
        this.diagramHR = diagramHR;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
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

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
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

    @Ignore
    private List<HistoryEntity> historyEntityList = new ArrayList<>() ;

    public List<HistoryEntity> getHistoryEntityList() {
        return historyEntityList;
    }
    public void setHistoryEntityList(List<HistoryEntity> historyEntityList) {
        this.historyEntityList = historyEntityList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getHistoryName() {
        return historyName;
    }

    public void setHistoryName(String historyName) {
        this.historyName = historyName;
    }

    public int getHistoryParentUid() {
        return historyParentUid;
    }

    public void setHistoryParentUid(int historyParentUid) {
        this.historyParentUid = historyParentUid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getDiagramLevel() {
        return diagramLevel;
    }

    public void setDiagramLevel(String diagramLevel) {
        this.diagramLevel = diagramLevel;
    }

    public String getDiagramIncline() {
        return diagramIncline;
    }

    public void setDiagramIncline(String diagramIncline) {
        this.diagramIncline = diagramIncline;
    }

    public int getSomeData() {
        return someData;
    }

    public void setSomeData(int someData) {
        this.someData = someData;
    }


    public int getBaseProgramId() {
        return baseProgramId;
    }

    public void setBaseProgramId(int baseProgramId) {
        this.baseProgramId = baseProgramId;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public HistoryEntity() {
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int flags) {
//        parcel.writeInt(uid);
//        parcel.writeString(historyName);
//        parcel.writeInt(historyParentUid);
//        parcel.writeInt(level);
//        parcel.writeInt(time);
//        parcel.writeString(diagramLevel);
//        parcel.writeString(diagramIncline);
//        parcel.writeInt(someData);
//        parcel.writeSerializable(updateTime);
//
//        parcel.writeTypedList(historyEntityList);
//        parcel.writeInt(baseProgramId);
//    }
//    private HistoryEntity(Parcel in) {
//        uid = in.readInt();
//        historyName = in.readString();
//        historyParentUid = in.readInt();
//        level = in.readInt();
//        time = in.readInt();
//        diagramLevel = in.readString();
//        diagramIncline = in.readString();
//        someData = in.readInt();
//        updateTime = new Date(in.readLong());
//        historyEntityList = in.createTypedArrayList(HistoryEntity.CREATOR);
//        baseProgramId = in.readInt();
//    }
//
//    public static final Creator<HistoryEntity> CREATOR = new Creator<HistoryEntity>() {
//        @Override
//        public HistoryEntity createFromParcel(Parcel in) {
//            return new HistoryEntity(in);
//        }
//
//        @Override
//        public HistoryEntity[] newArray(int size) {
//            return new HistoryEntity[size];
//        }
//    };
}
