package com.dyaco.spiritbike.support;

public class HomeScreenInfo {

    private int m_Item;
    private int m_Type;
    private boolean m_TagChecked;
    private boolean m_DiagramFill;
    private int m_DiagramRes;
    private final int TYPE_FAVORITE = 0;
    private final int TYPE_QUICK_START = 1;
    private String name;

    public HomeScreenInfo(int item, int type, boolean tagChecked, boolean diagramFill, int diagramRes, String name) {

        this.m_Item = item;
        this.m_Type = type;
        this.m_TagChecked = tagChecked;
        this.m_DiagramFill = diagramFill;
        this.m_DiagramRes = diagramRes;
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_Item() {
        return m_Item;
    }

    public void set_Item(int item) {
        this.m_Item = item;
    }

    public boolean is_DiagramFill() {
        return m_DiagramFill;
    }

    public void set_DiagramFill(boolean diagramFill) {
        this.m_DiagramFill = diagramFill;
    }

    public int get_DiagramRes() {
        return m_DiagramRes;
    }

    public void set_DiagramRes(int diagramRes) {
        this.m_DiagramRes = diagramRes;
    }

    public int get_Type() {
        return m_Type;
    }

    public void set_Type(int type) {
        this.m_Type = type;
    }

    public boolean get_TagChecked() {
        return m_TagChecked;
    }

    public void set_TagChecked(boolean tagChecked) {
        this.m_TagChecked = tagChecked;
    }
}
