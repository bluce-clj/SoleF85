package com.dyaco.spiritbike.webapi;

import android.text.TextUtils;
import android.util.Log;
import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.LogS;
import com.dyaco.spiritbike.support.UnitEnum;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.convertUnit;

public class BaseCallWebApi {

    /**
     * 更新雲端使用者資料
     * @param userProfileEntity 本機使用者資料
     */
    public void callSyncUserDataApi(UserProfileEntity userProfileEntity) {
        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("account_no", userProfileEntity.getSoleAccountNo());
        param.put("name", userProfileEntity.getUserName());
        param.put("sex", userProfileEntity.getGender() == 0 ? "F" : "M");
        param.put("birthday", userProfileEntity.getBirthday());
        param.put("height", String.valueOf(userProfileEntity.getHeight_metric()));
        param.put("weight", String.valueOf(userProfileEntity.getWeight_metric()));

       // Log.d("WEB_API-更新雲端使用者資料", "callSyncUserDataApi: " + param.toString());
        LogS.printJson("WEB_API-更新雲端使用者資料",new Gson().toJson(param.toString()),"callSyncUserDataApi");
        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiSyncUpdateUserProfile(param),
                new BaseApi.IResponseListener<SyncUpdateUserProfileBean>() {
                    @Override
                    public void onSuccess(SyncUpdateUserProfileBean data) {
                        Log.d("WEB_API-更新雲端使用者資料", "Response: " + new Gson().toJson(data));
                        //   BaseApi.clearDispose();
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-更新雲端使用者資料", "失敗");
                        //   BaseApi.clearDispose();
                    }
                });
    }


    /**
     * 取得尚未上傳的History
     */
    public void getNotUploadHistoryList(UserProfileEntity userProfileEntity) {
        DatabaseManager.getInstance(MyApplication.getInstance().getApplicationContext()).getNotUploadHistoryList(userProfileEntity.getUid(),
                new DatabaseCallback<HistoryEntity>() {
                    @Override
                    public void onDataLoadedList(List<HistoryEntity> historyEntityList) {
                        super.onDataLoadedList(historyEntityList);

                        for (HistoryEntity historyEntity : historyEntityList) {
                            uploadPastHistory(historyEntity, userProfileEntity);
                        }
                    }
                });
    }


    /**
     * 上傳尚未同步的history
     * @param historyEntity historyEntity
     */
    private void uploadPastHistory(HistoryEntity historyEntity, UserProfileEntity userProfileEntity) {

        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        int timeZoneHour = (int) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String training_datetime = dateFormat.format(historyEntity.getUpdateTime());
        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("account_no", userProfileEntity.getSoleAccountNo());
        param.put("training_datetime", training_datetime);//訓練日期時間
        param.put("training_timezone_hour", timeZoneHour);//UTC時差
        param.put("training_timezone_name", TimeZone.getDefault().getID());//時區名稱
        param.put("brand_code", "spirit");
        param.put("model_code", deviceSettingBean.getModel_name());
        /**
         * category_code
         * 0=Treadmill,
         * 1=Bike,
         * 2=Elliptical
         * 3=Stepper
         * 4=Rower
         */
        param.put("category_code", deviceSettingBean.getCategoryCode());
        param.put("brand_type", "0");
        param.put("in_out", "0");
        param.put("unit", historyEntity.getUnit());
        param.put("sales_version", "0");
        param.put("program_name", historyEntity.getHistoryName());
        param.put("total_time", historyEntity.getRunTime());
        //  param.put("total_distance", convertUnit(UnitEnum.DISTANCE, historyEntity.getUnit(), Double.parseDouble(historyEntity.getTotalDistance())));
        param.put("total_distance", historyEntity.getUnit() == 0 ? Double.parseDouble(historyEntity.getTotalDistance()) : CommonUtils.mi2km(Double.parseDouble(historyEntity.getTotalDistance())));
        param.put("total_calories", historyEntity.getCalories());
        param.put("avg_hr", historyEntity.getAvgHR());
        param.put("avg_rpm", historyEntity.getAvgRPM());
        param.put("avg_speed", historyEntity.getAvgSpeed());
        param.put("avg_watt", historyEntity.getAvgWATT());
        param.put("avg_met", historyEntity.getAvgMET());
        param.put("avg_level", historyEntity.getAvgLevel());
        param.put("avg_incline", CommonUtils.textCheckNull(historyEntity.getAvgIncline()));
        param.put("total_step", 0.0);
        param.put("avg_cadence", 0.0);
        param.put("device_os_name", "Android");
        param.put("device_os_version", "10.1.0");
        param.put("mac_address", CommonUtils.getMacAddress());
        param.put("trainh_process_data", new Gson().toJson(historyEntity.getTrainingProcessData()));

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiSyncUploadTrainingData(param),
                new BaseApi.IResponseListener<SyncUploadTrainingDataBean>() {
                    @Override
                    public void onSuccess(SyncUploadTrainingDataBean data) {

                        LogS.printJson("WEB_API-上傳過去的運動資料",new Gson().toJson(data),"");
                       // Log.d("WEB_API-上傳過去的運動資料", "S: " + new Gson().toJson(data));
                        if (data.getSys_response_message().getCode().equals("1")) {
                            //更新資料庫History的上傳狀態
                            updateHistoryUploadStatus(historyEntity);
                        } else if (data.getSys_response_message().getMessage() != null && data.getSys_response_message().getMessage().equals("Training data is duplicate.")){
                            updateHistoryUploadStatus(historyEntity);
                        }
                    }
                    @Override
                    public void onFail() {
                        Log.d("WEB_API-上傳過去的運動資料", "失敗");
                    }
                });
    }

    /**
     * 更新資料庫 History 的上傳狀態
     * @param historyEntity historyEntity
     */
    public void updateHistoryUploadStatus(HistoryEntity historyEntity) {
        historyEntity.setUploaded(true);
        historyEntity.setTrainingProcessData("");
        DatabaseManager.getInstance(MyApplication.getInstance()).updateHistory(historyEntity,
                new DatabaseCallback<HistoryEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();
                        Log.d("WEB_API-ROOM歷史紀錄", "上傳裝態更新成功");
                    }
                    @Override
                    public void onError(String err) {
                        Log.d("WEB_API-ROOM歷史紀錄", "上傳裝態更新失敗");
                        super.onError(err);
                    }
                });
    }
}
