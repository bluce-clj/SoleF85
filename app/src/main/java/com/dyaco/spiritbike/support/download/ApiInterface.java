package com.dyaco.spiritbike.support.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiInterface {
    /**
     * 下载视频
     *
     * @param fileUrl
     * @return
     */
    @Streaming //大文件时要加不然會OOM
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
