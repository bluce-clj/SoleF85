package com.dyaco.spiritbike.support.room;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = UserProfileEntity.USER_PROFILE,
        indices = {@Index("uid")}
)
public class UserProfileEntity implements Parcelable {
    public static final String USER_PROFILE = "user_profile";

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String userName;
    private int userId;
    private int userImage;
    private int userType; // 0 add ,1 user, 2 guest
    private int weight_metric; //公
    private int height_metric;
    private int weight_imperial; //英
    private int height_imperial;
    private String birthday;
    private int gender; //0 女; 1男
    private int unit; //0公 1英

    private String customLevelNum;
    private String customInclineNum;

    private double totalDistance_metric;
    private double totalDistance_imperial;
    private double totalRun; //total workout
    private double avgPaceInMonth;

    private int sleepMode;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] levelDiagram;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] inclineDiagram;

    //Sole+ Data
    private String soleAccountNo;
    private String soleAccount;
    private String solePassword;
    private String soleEmail;
    private String soleSyncPassword;
    private String soleRegistType;
    private String soleHeaderImgUrl;

    private double wattAccumulate;
    private int wattFrequency;
    private int workoutMonth;

    public int getSleepMode() {
        return sleepMode;
    }

    public void setSleepMode(int sleepMode) {
        this.sleepMode = sleepMode;
    }

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

    public String getSoleHeaderImgUrl() {
        return soleHeaderImgUrl;
    }

    public void setSoleHeaderImgUrl(String soleHeaderImgUrl) {
        this.soleHeaderImgUrl = soleHeaderImgUrl;
    }

    public String getSoleAccountNo() {
        return soleAccountNo;
    }

    public void setSoleAccountNo(String soleAccountNo) {
        this.soleAccountNo = soleAccountNo;
    }

    public String getSoleAccount() {
        return soleAccount;
    }

    public void setSoleAccount(String soleAccount) {
        this.soleAccount = soleAccount;
    }

    public String getSolePassword() {
        return solePassword;
    }

    public void setSolePassword(String solePassword) {
        this.solePassword = solePassword;
    }

    public String getSoleEmail() {
        return soleEmail;
    }

    public void setSoleEmail(String soleEmail) {
        this.soleEmail = soleEmail;
    }

    public String getSoleSyncPassword() {
        return soleSyncPassword;
    }

    public void setSoleSyncPassword(String soleSyncPassword) {
        this.soleSyncPassword = soleSyncPassword;
    }

    public String getSoleRegistType() {
        return soleRegistType;
    }

    public void setSoleRegistType(String soleRegistType) {
        this.soleRegistType = soleRegistType;
    }

    public double getTotalDistance_metric() {
        return totalDistance_metric;
    }

    public void setTotalDistance_metric(double totalDistance_metric) {
        this.totalDistance_metric = totalDistance_metric;
    }

    public double getTotalDistance_imperial() {
        return totalDistance_imperial;
    }

    public void setTotalDistance_imperial(double totalDistance_imperial) {
        this.totalDistance_imperial = totalDistance_imperial;
    }

    public String getCustomLevelNum() {
        return customLevelNum;
    }

    public void setCustomLevelNum(String customLevelNum) {
        this.customLevelNum = customLevelNum;
    }

    public String getCustomInclineNum() {
        return customInclineNum;
    }

    public void setCustomInclineNum(String customInclineNum) {
        this.customInclineNum = customInclineNum;
    }

    public byte[] getLevelDiagram() {
        return levelDiagram;
    }

    public void setLevelDiagram(byte[] levelDiagram) {
        this.levelDiagram = levelDiagram;
    }

    public byte[] getInclineDiagram() {
        return inclineDiagram;
    }

    public void setInclineDiagram(byte[] inclineDiagram) {
        this.inclineDiagram = inclineDiagram;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getWeight_metric() {
        return weight_metric;
    }

    public void setWeight_metric(int weight_metric) {
        this.weight_metric = weight_metric;
    }

    public int getHeight_metric() {
        return height_metric;
    }

    public void setHeight_metric(int height_metric) {
        this.height_metric = height_metric;
    }

    public int getWeight_imperial() {
        return weight_imperial;
    }

    public void setWeight_imperial(int weight_imperial) {
        this.weight_imperial = weight_imperial;
    }

    public int getHeight_imperial() {
        return height_imperial;
    }

    public void setHeight_imperial(int height_imperial) {
        this.height_imperial = height_imperial;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public static String getUserProfile() {
        return USER_PROFILE;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }



    public double getTotalRun() {
        return totalRun;
    }

    public void setTotalRun(double totalRun) {
        this.totalRun = totalRun;
    }

    public double getAvgPaceInMonth() {
        return avgPaceInMonth;
    }

    public void setAvgPaceInMonth(double avgPaceInMonth) {
        this.avgPaceInMonth = avgPaceInMonth;
    }

    public UserProfileEntity() {
    }



    @Override
    public int describeContents() {
        return 0;
    }

    //順序要一樣
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeString(userName);
        parcel.writeInt(userId);
        parcel.writeInt(userImage);
        parcel.writeInt(userType);
        parcel.writeInt(weight_metric);
        parcel.writeInt(height_metric);
        parcel.writeInt(weight_imperial);
        parcel.writeInt(height_imperial);
        parcel.writeString(birthday);
        parcel.writeInt(gender);
        parcel.writeInt(unit);
        parcel.writeString(customLevelNum);
        parcel.writeString(customInclineNum);
        parcel.writeByteArray(levelDiagram);
        parcel.writeByteArray(inclineDiagram);
        parcel.writeDouble(totalDistance_imperial);
        parcel.writeDouble(totalDistance_metric);
        parcel.writeDouble(totalRun);
        parcel.writeDouble(avgPaceInMonth);
        parcel.writeString(soleAccountNo);
        parcel.writeString(soleAccount);
        parcel.writeString(solePassword);
        parcel.writeString(soleEmail);
        parcel.writeString(soleSyncPassword);
        parcel.writeString(soleRegistType);
        parcel.writeString(soleHeaderImgUrl);

        parcel.writeDouble(wattAccumulate);
        parcel.writeInt(wattFrequency);
        parcel.writeInt(workoutMonth);
        parcel.writeInt(sleepMode);
    }

    //順序要一樣
    private UserProfileEntity(Parcel in) {
        uid = in.readInt();
        userName = in.readString();
        userId = in.readInt();
        userImage = in.readInt();
        userType = in.readInt();
        weight_metric = in.readInt();
        height_metric = in.readInt();
        weight_imperial = in.readInt();
        height_imperial = in.readInt();
        birthday = in.readString();
        gender = in.readInt();
        unit = in.readInt();
        customLevelNum = in.readString();
        customInclineNum = in.readString();
        levelDiagram = in.createByteArray();
        inclineDiagram = in.createByteArray();
        totalDistance_imperial = in.readDouble();
        totalDistance_metric = in.readDouble();
        totalRun = in.readDouble();
        avgPaceInMonth = in.readDouble();
        soleAccountNo = in.readString();
        soleAccount = in.readString();
        solePassword = in.readString();
        soleEmail = in.readString();
        soleSyncPassword = in.readString();
        soleRegistType = in.readString();
        soleHeaderImgUrl = in.readString();

        wattAccumulate = in.readDouble();
        wattFrequency = in.readInt();
        workoutMonth = in.readInt();
        sleepMode = in.readInt();
    }

    public static final Creator<UserProfileEntity> CREATOR = new Creator<UserProfileEntity>() {
        @Override
        public UserProfileEntity createFromParcel(Parcel in) {
            return new UserProfileEntity(in);
        }

        @Override
        public UserProfileEntity[] newArray(int size) {
            return new UserProfileEntity[size];
        }
    };
}