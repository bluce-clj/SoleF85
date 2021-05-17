package com.dyaco.spiritbike.support;


import java.io.Serializable;

public class ProgramInfo implements Serializable {
    private int m_Item;
    private int m_Desc;
    private boolean m_PhotoFill;
    private int m_PhotoResId;
    private int m_DiagramRes1;
    private int m_DiagramRes2;
    private boolean check;

    private byte[] imgLevel;
    private byte[] imgIncline;

    public byte[] getImgLevel() {
        return imgLevel;
    }

    public void setImgLevel(byte[] imgLevel) {
        this.imgLevel = imgLevel;
    }

    public byte[] getImgIncline() {
        return imgIncline;
    }

    public void setImgIncline(byte[] imgIncline) {
        this.imgIncline = imgIncline;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    private ProgramsEnum programsEnum;

    public ProgramsEnum getProgramsEnum() {
        return programsEnum;
    }

    public void setProgramsEnum(ProgramsEnum programsEnum) {
        this.programsEnum = programsEnum;
    }

    public ProgramInfo(int item, int desc, boolean photoFill, int photoResId, int diagramRes1, int diagramRes2, ProgramsEnum programsEnum ,boolean isCheck ,byte[] imgLevel,byte[] imgIncline) {
        this.m_Item = item;
        this.m_Desc = desc;
        this.m_PhotoFill = photoFill;
        this.m_PhotoResId = photoResId;
        this.m_DiagramRes1 = diagramRes1;
        this.m_DiagramRes2 = diagramRes2;
        this.programsEnum = programsEnum;
        this.check = isCheck;
        this.imgIncline = imgIncline;
        this.imgLevel = imgLevel;

    }

    public int get_Item() {
        return m_Item;
    }

    public void set_Item(int item) {
        this.m_Item = item;
    }

    public int get_Desc() {
        return m_Desc;
    }

    public void set_Desc(int desc) {
        this.m_Desc = desc;
    }

    public int get_PhotoResId() {
        return m_PhotoResId;
    }

    public void set_PhotoResId(int photoResId) {
        this.m_PhotoResId = photoResId;
    }

    public boolean is_PhotoFill() {
        return m_PhotoFill;
    }

    public void set_PhotoFill(boolean photoFill) {
        this.m_PhotoFill = photoFill;
    }

    public int get_DiagramRes1() {
        return m_DiagramRes1;
    }

    public void set_DiagramRes1(int diagramRes1) {
        this.m_DiagramRes1 = diagramRes1;
    }

    public int get_DiagramRes2() {
        return m_DiagramRes2;
    }

    public void set_DiagramRes2(int diagramRes2) {
        this.m_DiagramRes2 = diagramRes2;
    }

}
