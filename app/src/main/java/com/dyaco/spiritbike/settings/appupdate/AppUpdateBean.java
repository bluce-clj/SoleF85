package com.dyaco.spiritbike.settings.appupdate;

import com.google.gson.annotations.SerializedName;

public class AppUpdateBean {

    @SerializedName("Version Code")
    private int VersionCode;
    private String Version;
    @SerializedName("Package Name")
    private String packageName;
    private String MD5;
    @SerializedName("Download URL")
    private String downloadURL;
    @SerializedName("Force updates")
    private String forceUpdate;
    @SerializedName("Path")
    private String path;
}
