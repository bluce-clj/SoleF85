package com.dyaco.spiritbike.workout;

import android.widget.SeekBar;

import java.io.Serializable;

public class DiagramBean implements Serializable {

    public DiagramBean(SeekBar seekBar01, SeekBar seekBar02, int status, int progressLevel,int progressLevelOrigin, int progressIncline, int progressInclineOrigin) {
        this.seekBar01 = seekBar01;
        this.seekBar02 = seekBar02;
        this.status = status;
        this.progressLevel = progressLevel;
        this.progressLevelOrigin = progressLevelOrigin;
        this.progressIncline = progressIncline;
        this.progressInclineOrigin = progressInclineOrigin;
    }

    public DiagramBean(int hr) {
        this.hr = hr;
    }

    public DiagramBean() {

    }

    private String type;
    private int status; //0 pending , 1 running , 2 finish
    private int progressLevel;
    private int progressLevelOrigin;
    private int progressIncline;
    private int progressInclineOrigin;
    private int hr;
    private SeekBar seekBar01; //細
    private SeekBar seekBar02; //粗

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public int getProgressLevelOrigin() {
        return progressLevelOrigin;
    }

    public void setProgressLevelOrigin(int progressLevelOrigin) {
        this.progressLevelOrigin = progressLevelOrigin;
    }

    public int getProgressInclineOrigin() {
        return progressInclineOrigin;
    }

    public void setProgressInclineOrigin(int progressInclineOrigin) {
        this.progressInclineOrigin = progressInclineOrigin;
    }

    public SeekBar getSeekBar01() {
        return seekBar01;
    }

    public void setSeekBar01(SeekBar seekBar01) {
        this.seekBar01 = seekBar01;
    }

    public SeekBar getSeekBar02() {
        return seekBar02;
    }

    public void setSeekBar02(SeekBar seekBar02) {
        this.seekBar02 = seekBar02;
    }

    public int getProgressLevel() {
        return progressLevel;
    }

    public void setProgressLevel(int progressLevel) {
        this.progressLevel = progressLevel;
    }

    public int getProgressIncline() {
        return progressIncline;
    }

    public void setProgressIncline(int progressIncline) {
        this.progressIncline = progressIncline;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
