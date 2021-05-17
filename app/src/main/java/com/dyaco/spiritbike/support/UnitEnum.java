package com.dyaco.spiritbike.support;

import static com.dyaco.spiritbike.MyApplication.UNIT_E;

public enum UnitEnum {

    DISTANCE(0,"km","mi",""),
    SPEED(1,"km/h","mph",""),
    HEIGHT(2,"cm","ft","in"),
    WEIGHT(3,"kg","lb","");

    UnitEnum(int code, String metric, String imperial1,String imperial2) {
        this.code = code;
        this.metric = metric;
        this.imperial1 = imperial1;
        this.imperial2 = imperial2;
    }

    private int code;
    private String metric;
    private String imperial1;
    private String imperial2;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getImperial1() {
        return imperial1;
    }

    public void setImperial1(String imperial1) {
        this.imperial1 = imperial1;
    }

    public String getImperial2() {
        return imperial2;
    }

    public void setImperial2(String imperial2) {
        this.imperial2 = imperial2;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public static String getUnit(UnitEnum unitEnum) {
        String unit ="";
        switch (unitEnum) {
            case SPEED:
                unit = UNIT_E == 0 ? SPEED.getMetric() : SPEED.getImperial1();
                break;
            case HEIGHT:
                unit = UNIT_E == 0 ? HEIGHT.getMetric() : HEIGHT.getImperial1();
                break;
            case WEIGHT:
                unit = UNIT_E == 0 ? WEIGHT.getMetric() : WEIGHT.getImperial1();
                break;
            case DISTANCE:
                unit = UNIT_E == 0 ? DISTANCE.getMetric() : DISTANCE.getImperial1();
                break;
        }

        return unit;
    }
}
