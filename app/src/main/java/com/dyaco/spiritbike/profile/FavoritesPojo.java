package com.dyaco.spiritbike.profile;

public class FavoritesPojo {

    private String name;
    private String dateTime;
    private String info;
    private int diagram;

    public FavoritesPojo(String name, String dateTime, String info, int diagram) {
        this.name = name;
        this.dateTime = dateTime;
        this.info = info;
        this.diagram = diagram;
    }

    public FavoritesPojo(String name) {
        this.name = name;
    }

    public FavoritesPojo() {

    }


    public int getDiagram() {
        return diagram;
    }

    public void setDiagram(int diagram) {
        this.diagram = diagram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
