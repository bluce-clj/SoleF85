package com.dyaco.spiritbike.webapi;

import com.dyaco.spiritbike.settings.UpdateBean;
import com.dyaco.spiritbike.settings.appupdate.AppUpdateData;
import com.dyaco.spiritbike.support.TimeZoneBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IServiceApi {

    //2.4.1.會員上傳裝置ID
    @FormUrlEncoded
    @POST("Member/Sync_AddSynceDevice")
    Observable<SyncAddSyncDeviceBean> apiSyncAddSyncDevice(@FieldMap Map<String, Object> params);

    //2.4.2.專用機取得使用者資料 (QRCode 用)
    @FormUrlEncoded
    @POST("Member/Sync_DeviceSyncUser")
    Observable<SyncDeviceSyncUserBean> apiSyncDeviceSyncUser(@FieldMap Map<String, Object> params);

    //2.4.3.專用機會員資料更新雲端會員資料
    @FormUrlEncoded
    @POST("Member/Sync_UpdateUserProfile")
    Observable<SyncUpdateUserProfileBean> apiSyncUpdateUserProfile(@FieldMap Map<String, Object> params);

    //2.4.4.專用機運動資料上傳
    @FormUrlEncoded
    @POST("TrainingDC/Sync_UploadTrainingData")
    Observable<SyncUploadTrainingDataBean> apiSyncUploadTrainingData(@FieldMap Map<String, Object> params);

    //2.4.5.專用機取得使用者資料
    @FormUrlEncoded
    @POST("Member/Sync_GetUserInfo")
    Observable<SyncGetUserInfoBean> apiSyncGetUserInfo(@FieldMap Map<String, Object> params);

    //2.4.6.專用機主動移除裝置與會員連結
    @FormUrlEncoded
    @POST("Member/DeleteSyncLink")
    Observable<DeleteSyncLinkBean> apiDeleteSyncLink(@FieldMap Map<String, Object> params);

   // @GET("test_1/update.json")
   // @GET("spirit/xt785/production/update.json")
  //  @GET("spirit/xe395/debug/update.json")
    @GET("update.json")
    Observable<UpdateBean> apiCheckUpdate();

    @GET("app_update.json")
    Observable<AppUpdateData> apiCheckAppUpdate();

    @GET("json.gp")
    Observable<TimeZoneBean> apiGetTimeZone();

//    @Multipart
//    @POST("UploadWorkoutDetail")
//    Observable<Eee> apiUploadFile(@Part MultipartBody.Part multipartFile, @Part("params") RequestBody requestBodyParams);

//    RequestBody requestBodyParams = RequestBody.create(params, MediaType.parse("text/plain"));
//    File file = readFileFromAssets(this);
//    RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
//    MultipartBody.Part multipartFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
}
