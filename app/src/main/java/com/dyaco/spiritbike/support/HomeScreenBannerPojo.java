package com.dyaco.spiritbike.support;

public class HomeScreenBannerPojo {
    public HomeScreenBannerPojo(int bannerType, String text1, String text2, String text3) {
        this.bannerType = bannerType;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    int bannerType;
    String text1;
    String text2;
    String text3;

    public int getBannerType() {
        return bannerType;
    }

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }
}
