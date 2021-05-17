package com.dyaco.spiritbike.support;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;


public enum ProgramsEnum {

    MANUAL(0, MyApplication.getInstance().getString(R.string.manual), R.drawable.diagram_preview_inversion_strength, R.id.manualAdjustFragment, R.drawable.diagram_preview_manual, R.drawable.diagram_cardio_incline, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0"),
    HILL(1, MyApplication.getInstance().getString(R.string.hill), R.drawable.diagram_preview_inversion_strength, R.id.adjustHillFragment, R.drawable.diagram_hill_level, R.drawable.diagram_hill_incline, "1#2#2#3#3#4#4#5#5#7#7#5#5#4#4#3#3#3#2#1", "2#2#6#6#6#8#8#8#8#10#10#8#8#8#8#6#6#6#2#2"),
    FATBURN(2, MyApplication.getInstance().getString(R.string.fatburn), R.drawable.diagram_preview_inversion_strength, R.id.adjustHillFragment, R.drawable.diagram_fatburn_level, R.drawable.diagram_fatburn_incline, "1#2#3#5#5#5#5#5#5#5#5#5#5#5#5#5#5#3#2#1", "3#3#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#10#3#3"),
    CARDIO(3, MyApplication.getInstance().getString(R.string.cardio), R.drawable.diagram_preview_inversion_strength, R.id.adjustHillFragment, R.drawable.diagram_cardio_level, R.drawable.diagram_cardio_incline, "1#2#3#5#6#7#6#6#6#7#6#5#6#7#6#5#6#5#2#1", "2#2#8#8#10#4#4#4#10#4#4#4#10#2#2#2#8#2#2#2"),
    STRENGTH(4, MyApplication.getInstance().getString(R.string.strength), R.drawable.diagram_preview_inversion_strength, R.id.adjustHillFragment, R.drawable.diagram_strength_level, R.drawable.diagram_strength_incline, "1#2#2#3#3#4#4#5#5#6#7#7#8#8#8#8#8#6#4#1", "2#2#8#8#8#8#10#10#10#10#10#10#10#10#6#6#6#6#2#2"),
    HIIT(5, MyApplication.getInstance().getString(R.string.hiit), R.drawable.diagram_preview_inversion_strength, R.id.adjustHillFragment, R.drawable.diagram_hiit_level, R.drawable.diagram_hiit_incline, "1#2#2#7#7#2#2#7#7#2#2#7#7#2#2#7#7#2#2#1", "2#2#4#10#10#4#4#10#10#4#4#10#10#4#4#10#10#4#2#2"),
    HEART_RATE(8, MyApplication.getInstance().getString(R.string.heart_rate), R.drawable.diagram_preview_inversion_strength, R.id.adjustHeartRateFragment, 0, 0, "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1", "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0"),
    CUSTOM(9, MyApplication.getInstance().getString(R.string.custom), R.drawable.diagram_preview_inversion_strength, R.id.adjustCustom_3Fragment, 0, 0, "16#12#3#4#6#7#5#7#1#1#1#6#1#1#1#1#1#1#1#1", "16#12#3#2#2#2#2#3#3#3#3#2#2#2#2#2#2#2#2#1"),
    OTHER(-1, MyApplication.getInstance().getString(R.string.custom), R.drawable.diagram_preview_inversion_strength, R.id.adjustCustomFragment, 0, 0, "16#12#3#4#6#7#5#7#1#1#1#6#1#1#1#1#1#1#1#1", "16#12#3#2#2#2#2#3#3#3#3#2#2#2#2#2#2#2#2#1"),
    OTHER2(-2, MyApplication.getInstance().getString(R.string.custom), R.drawable.diagram_preview_inversion_strength, R.id.adjustCustomFragment, 0, 0, "16#12#3#4#6#7#5#7#1#1#1#6#1#1#1#1#1#1#1#1", "16#12#3#2#2#2#2#3#3#3#3#2#2#2#2#2#2#2#2#1");

//    ProgramsEnum() {
//    }


    ProgramsEnum(int code, String text, int diagramRes, int adjustFragmentId, int diagramResLevel, int diagramResIncline, String levelNum, String inclineNum) {
        this.code = code;
        this.text = text;
        this.diagramRes = diagramRes;
        this.adjustFragmentId = adjustFragmentId;
        this.diagramResLevel = diagramResLevel;
        this.diagramResIncline = diagramResIncline;
        this.levelNum = levelNum;
        this.inclineNum = inclineNum;
    }

    private int code;
    private String text;
    private int diagramRes;
    private int adjustFragmentId;
    private int diagramResLevel;
    private int diagramResIncline;

    private String levelNum;
    private String inclineNum;

    public String getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(String levelNum) {
        this.levelNum = levelNum;
    }

    public String getInclineNum() {
        return inclineNum;
    }

    public void setInclineNum(String inclineNum) {
        this.inclineNum = inclineNum;
    }

    public int getDiagramResLevel() {
        return diagramResLevel;
    }

    public void setDiagramResLevel(int diagramResLevel) {
        this.diagramResLevel = diagramResLevel;
    }

    public int getDiagramResIncline() {
        return diagramResIncline;
    }

    public void setDiagramResIncline(int diagramResIncline) {
        this.diagramResIncline = diagramResIncline;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDiagramRes() {
        return diagramRes;
    }

    public void setDiagramRes(int diagramRes) {
        this.diagramRes = diagramRes;
    }

    public int getAdjustFragmentId() {
        return adjustFragmentId;
    }

    public void setAdjustFragmentId(int adjustFragmentId) {
        this.adjustFragmentId = adjustFragmentId;
    }

    public static ProgramsEnum getProgram(int i) {
        for (ProgramsEnum programsEnum : values()) {
            if (programsEnum.getCode() == i) {
                return programsEnum;
            }
        }
        return ProgramsEnum.getProgram(9);
    }
}