package com.dyaco.spiritbike.workout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.corestar.libs.ftms.TrainingStatus;
import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.programs.TemplateFullActivity;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.LogS;
import com.dyaco.spiritbike.support.UnitEnum;
import com.dyaco.spiritbike.support.banner.Banner;
import com.dyaco.spiritbike.support.banner.indicator.RectangleIndicator;
import com.dyaco.spiritbike.support.banner.util.BannerUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.profile.SaveProgramAsTemplateActivity;
import com.dyaco.spiritbike.programs.ProgramSummary4Pojo;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.dyaco.spiritbike.webapi.SyncUploadTrainingDataBean;
import com.dyaco.spiritbike.webapi.TrainingProcessBean;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.convertUnit;

public class WorkoutResultSummaryFragment extends BaseFragment {
    UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
    private WorkoutBean workoutBean;

    public WorkoutResultSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workoutBean = (WorkoutBean) getArguments().getSerializable("WorkoutBean");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_programs_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getInstance().mFTMSManager.notifyTrainingStatus(TrainingStatus.IDLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(false, false, 0, -1);

        Button btNo_DialogDataLost = view.findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = view.findViewById(R.id.btYes_DialogDataLost);

        TextView tvSummaryTitle = view.findViewById(R.id.tv_summary_title);
        TextView tvDatetime = view.findViewById(R.id.tv_datetime);

        tvSummaryTitle.setText(workoutBean.getProgramName());

        String date = new SimpleDateFormat("hh:mm a, MMM dd, yyyy", Locale.ENGLISH).format(workoutBean.getUpdateTime());
        tvDatetime.setText(date);

        //回首頁
        btNo_DialogDataLost.setOnClickListener(v -> {
                    cancelCountDown();
                    done();
                }
        );

        //儲存成Template
        btYes_DialogDataLost.setOnClickListener(v -> {
                    cancelCountDown();

                    checkTemplateSum();
                }
        );

        RectangleIndicator indicator = view.findViewById(R.id.indicator);
        Banner banner = view.findViewById(R.id.rcvPrograms_ProgramsDetails);

        WorkoutResultSummaryAdapter adapter = new WorkoutResultSummaryAdapter(mActivity, getProgramInfo(), workoutBean);
        //  banner.setStartPosition(i);
        //  banner.infi
        banner.setAdapter(adapter);
        banner.isAutoLoop(false);
        banner.setBannerGalleryEffect(118, 118, 24, 1);

        banner.setIndicator(indicator, false);
        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(4));
        banner.setIndicatorWidth((int) BannerUtils.dp2px(4), (int) BannerUtils.dp2px(4));
        banner.setIndicatorHeight((int) BannerUtils.dp2px(4));
        banner.setIndicatorNormalColorRes(R.color.colorB4BEC7);
        banner.setIndicatorSelectedColorRes(R.color.colorE4002B);
        banner.setIndicatorSpace((int) BannerUtils.dp2px(24));
        banner.setIndicatorRadius(0);

        //Guest 不儲存 歷史紀錄
        if (userProfileEntity.getUserType() == 1) {
            //已連結sole帳號，上傳運動資料
            //   apiSyncUploadTrainingData();
            saveHistory(checkStr(userProfileEntity.getSoleAccountNo()));

//            if (checkStr(userProfileEntity.getSoleAccountNo())) {
//                //已連結sole帳號，上傳運動資料
//                //   apiSyncUploadTrainingData();
//                saveHistory(true);
//            } else {
//                saveHistory(false);
//            }
        }


        try {
            //存裝置設定

            double kk = workoutBean.getUnit() == 1 ? CommonUtils.mi2km(Double.parseDouble(workoutBean.getTotalDistance())) : Double.parseDouble(workoutBean.getTotalDistance());
            double k = deviceSettingBean.getODO_distance() + kk;

            double newDsKM = Double.parseDouble(CommonUtils.formatDecimal(k, 4));

            double h = deviceSettingBean.getODO_time() + ((double) workoutBean.getRunTime() / 60 / 60);
            double newDsHour = Double.parseDouble(CommonUtils.formatDecimal(h, 4));

            deviceSettingBean.setODO_time(newDsHour);
            deviceSettingBean.setODO_distance(newDsKM);
            getInstance().setDeviceSettingBean(deviceSettingBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 儲存歷史紀錄
     */
    private void saveHistory(boolean isUpload) {

        Date date = Calendar.getInstance().getTime();

        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setHistoryName(workoutBean.getProgramName());
        //  historyEntity.setBaseProgramId(workoutBean.getProgramId());
        historyEntity.setBaseProgramId(workoutBean.getBaseProgramId());
        historyEntity.setHistoryParentUid(userProfileEntity.getUid());
        historyEntity.setUpdateTime(date);
        historyEntity.setDiagramIncline(workoutBean.getInclineDiagramNum());
        historyEntity.setDiagramLevel(workoutBean.getLevelDiagramNum());

        historyEntity.setDiagramHR(workoutBean.getHrDiagramNum());

        historyEntity.setUnit(workoutBean.getUnit());
        historyEntity.setAvgSpeed(workoutBean.getAvgSpeed());
        historyEntity.setCalories(workoutBean.getCalories());
        historyEntity.setAvgRPM(workoutBean.getAvgRPM());
        historyEntity.setAvgWATT(workoutBean.getAvgWATT());
        historyEntity.setAvgMET(workoutBean.getAvgMET());
        historyEntity.setTotalDistance(workoutBean.getTotalDistance());
        historyEntity.setRunTime(workoutBean.getRunTime()); //sec
        historyEntity.setAvgIncline(workoutBean.getAvgIncline());
        historyEntity.setAvgLevel(workoutBean.getAvgLevel());

        historyEntity.setMaxIncline(String.valueOf(workoutBean.getMaxIncline()));
        historyEntity.setMaxWATT(workoutBean.getMaxWATT());
        historyEntity.setMaxLevel(String.valueOf(workoutBean.getMaxLevel()));

        historyEntity.setAvgHR(workoutBean.getAvgHR());
        historyEntity.setMaxHR(workoutBean.getMaxHR());

        historyEntity.setPace(workoutBean.getPace());
        historyEntity.setAvgPace(workoutBean.getAvgPace());

        historyEntity.setUploaded(false); // 0false, 1true
        String trainingProcess = new Gson().toJson(workoutBean.getTrainingProcessBean());
        //  historyEntity.setTrainingProcessData(isUpload ? "" : trainingProcess);
        historyEntity.setTrainingProcessData(trainingProcess);

        DatabaseManager.getInstance(MyApplication.getInstance().getApplicationContext()).insertHistory(historyEntity,
                new DatabaseCallback<HistoryEntity>() {
                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);

                        saveTotalNumToUserProfile();

                        Log.d("WEB_API-ROOM歷史紀錄", "歷史紀錄儲存成功");
                        //已連結sole帳號，上傳運動資料
                        historyEntity.setUid((int)rowId);
                        if (isUpload) apiSyncUploadTrainingData(historyEntity);
                    }

                    @Override
                    public void onError(String err) {
                        Log.d("WEB_API-ROOM歷史紀錄", "歷史紀錄儲存失敗");
                        super.onError(err);
                    }
                });
    }

    private List<ProgramSummary4Pojo> getProgramInfo() {
        List<ProgramSummary4Pojo> programInfo = new ArrayList<>();

        programInfo.add(new ProgramSummary4Pojo(1));
        programInfo.add(new ProgramSummary4Pojo(2));
        if (MODE == ModeEnum.XE395ENT) programInfo.add(new ProgramSummary4Pojo(3));
        programInfo.add(new ProgramSummary4Pojo(4));
        return programInfo;
    }

    /**
     * 儲存 TotalDistance 到 UserProfile
     */
    private void saveTotalNumToUserProfile() {

        double totalDistanceImperial;
        double totalDistanceMetric;
        if (workoutBean.getUnit() == 0) {
            totalDistanceMetric = userProfileEntity.getTotalDistance_metric() + Double.parseDouble(workoutBean.getTotalDistance());
            totalDistanceImperial = userProfileEntity.getTotalDistance_imperial() + CommonUtils.km2mi(Double.parseDouble(workoutBean.getTotalDistance()));
        } else {
            totalDistanceImperial = userProfileEntity.getTotalDistance_imperial() + Double.parseDouble(workoutBean.getTotalDistance());
            totalDistanceMetric = userProfileEntity.getTotalDistance_metric() + CommonUtils.mi2km(Double.parseDouble(workoutBean.getTotalDistance()));
        }

        userProfileEntity.setTotalDistance_imperial(totalDistanceImperial);
        userProfileEntity.setTotalDistance_metric(totalDistanceMetric);
        userProfileEntity.setTotalRun(userProfileEntity.getTotalRun() + 1);


        //TTWatt=TotalWatt1 + TotalWatt2+ TotalWatt3...
        //TTCnt = Cnt1+Cnt2+Cnt3...
        //當月 AvgWatt_Moon = TTWatt/ TTCnt
        //跨月時,  TTWatt Clear為0 , TTCnt Clear為0
        if (userProfileEntity.getWorkoutMonth() == workoutBean.getWorkoutMonth()) {
            double wattAccumulate = userProfileEntity.getWattAccumulate() + workoutBean.getWattAccumulate();
            userProfileEntity.setWattAccumulate(wattAccumulate);
            int wattFrequency = userProfileEntity.getWattFrequency() + workoutBean.getWattFrequency();
            userProfileEntity.setWattFrequency(wattFrequency);
        } else {
            userProfileEntity.setWattFrequency(workoutBean.getWattFrequency());
            userProfileEntity.setWattAccumulate(workoutBean.getWattAccumulate());
            userProfileEntity.setWorkoutMonth(workoutBean.getWorkoutMonth());
        }

        double avgWatMoon = userProfileEntity.getWattAccumulate() / userProfileEntity.getWattFrequency();
        avgWatMoon = Double.isNaN(avgWatMoon) ? 0.0 : avgWatMoon;
        userProfileEntity.setAvgPaceInMonth(avgWatMoon);

        DatabaseManager.getInstance(MyApplication.getInstance().getApplicationContext()).
                updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();

                        //超過10筆刪除
                        new Thread(() ->
                                DatabaseManager.getInstance(MyApplication.getInstance().getApplicationContext()).deleteMoreHistory(userProfileEntity.getUid())).start();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Log.d("SAVEEEEE", "onError: " + err + "," + "," + userProfileEntity.getWattAccumulate() / userProfileEntity.getWattFrequency());
                        //   Toast.makeText(mActivity, "Failure:" + err, Toast.LENGTH_LONG).show();
                    }
                });
    }

    CountDownTimer countDownTimer = new CountDownTimer(60 * 1000 * 3, 1000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            mActivity.runOnUiThread(() -> done());
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        cancelCountDown();
    }

    private void done() {

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .build();

        ((DashboardActivity) mActivity).rbHomeScreen_Dashboard.setChecked(true);
        ((DashboardActivity) mActivity).navController.navigate(userProfileEntity.getUserType() != 0 ? R.id.homeScreenFragment : R.id.homeScreenGuestFragment, null, navOptions);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountDown();
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelCountDown();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (countDownTimer != null) countDownTimer.start();
    }

    private void cancelCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            //   countDownTimer = null;
        }
    }

    /**
     * 上傳運動資料
     */
    private void apiSyncUploadTrainingData(HistoryEntity historyEntity) {

        if (workoutBean.getTimeSecond() == 0) {
            //上數

            int i = 0;
          //  String workout_time = String.valueOf(workoutBean.getRunTime());
            int left_time = (int)workoutBean.getRunTime();
            for (TrainingProcessBean.SysResponseDataBean sysResponseDataBean : workoutBean.getTrainingProcessBean().getSys_response_data()) {
              //  sysResponseDataBean.setTotal_workout_time(workout_time);
                sysResponseDataBean.setTotal_timeleft(String.valueOf(left_time -= 10));
                workoutBean.getTrainingProcessBean().getSys_response_data().set(i, sysResponseDataBean);
                i++;
            }
        }

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        int timeZoneHour = (int) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String training_datetime = dateFormat.format(workoutBean.getUpdateTime());

        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("account_no", userProfileEntity.getSoleAccountNo());
        param.put("training_datetime", training_datetime);//訓練日期時間
        param.put("training_timezone_hour", timeZoneHour);//UTC時差
        param.put("training_timezone_name", TimeZone.getDefault().getID());//時區名稱
        param.put("brand_code", "spirit");
        param.put("model_code", deviceSettingBean.getModel_name());
        param.put("category_code", deviceSettingBean.getCategoryCode());
        param.put("brand_type", "0");
        param.put("in_out", "0");
        param.put("unit", workoutBean.getUnit());
        param.put("sales_version", "0");
        param.put("program_name", workoutBean.getProgramName());
        param.put("total_time", workoutBean.getRunTime());
        //   param.put("total_distance", convertUnit(UnitEnum.DISTANCE, workoutBean.getUnit(), Double.parseDouble(workoutBean.getTotalDistance())));
        param.put("total_distance", workoutBean.getUnit() == 0 ? Double.parseDouble(workoutBean.getTotalDistance()) : CommonUtils.mi2km(Double.parseDouble(workoutBean.getTotalDistance())));
        param.put("total_calories", workoutBean.getCalories());
        param.put("avg_hr", workoutBean.getAvgHR());
        param.put("avg_rpm", workoutBean.getAvgRPM());
        param.put("avg_speed", workoutBean.getAvgSpeed());
        param.put("avg_watt", workoutBean.getAvgWATT());
        param.put("avg_met", workoutBean.getAvgMET());
        param.put("avg_level", workoutBean.getAvgLevel());
        param.put("avg_incline", CommonUtils.textCheckNull(workoutBean.getAvgIncline()));
        param.put("total_step", 0.0);
        param.put("avg_cadence", 0.0);
        param.put("device_os_name", "Android");
        param.put("device_os_version", "10.1.0");
        param.put("mac_address", CommonUtils.getMacAddress());
        param.put("trainh_process_data", new Gson().toJson(workoutBean.getTrainingProcessBean()));


        LogS.printJson("WEB_API-上傳運動資料",new Gson().toJson(workoutBean.getTrainingProcessBean()),"TrainingProcess");
     //   Log.d("WEB_API-上傳運動資料", "param: " + new Gson().toJson(workoutBean.getTrainingProcessBean()));
         // Log.d("WEB_API-上傳運動資料:參數", "param: " + param.toString());

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiSyncUploadTrainingData(param),
                new BaseApi.IResponseListener<SyncUploadTrainingDataBean>() {
                    @Override
                    public void onSuccess(SyncUploadTrainingDataBean data) {

                        boolean isUpload = data.getSys_response_message().getCode().equals("1");

                      //  Log.d("WEB_API-上傳運動資料", "isUpload: " + isUpload + "::::" + new Gson().toJson(data));

                        LogS.printJson("WEB_API-上傳運動資料:結果",new Gson().toJson(data),"isUpload: " + isUpload);

                        //   saveHistory(isUpload);

                        //上傳成功，更新狀態
                        if (isUpload) updateHistoryUploadStatus(historyEntity);
                        //此次上傳成功，檢查之前的上傳紀錄
                        //  if (isUpload) getNotUploadHistoryList();

                    }

                    @Override
                    public void onFail() {

                        saveHistory(false);
                        Log.d("WEB_API-上傳運動資料", "失敗");
                    }
                });
    }

    /**
     * 檢查Template數量
     */
    private void checkTemplateSum() {

        DatabaseManager.getInstance(MyApplication.getInstance()).checkTemplateSum(MyApplication.getInstance().getUserProfile().getUid(),
                new DatabaseCallback<Integer>() {
                    @Override
                    public void onCount(Integer i) {
                        super.onCount(i);
                        Log.i("@@@@@@@@@@", "onCount: " + i);

                        if (i >= 12) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            intent.putExtras(bundle);
                            intent.setClass(mActivity, TemplateFullActivity.class);
                            startActivity(intent);
                        } else {
                            goSave();
                        }
                    }
                });
    }

    private void goSave() {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("WorkoutBean", workoutBean);
        intent.putExtras(bundle);
        intent.setClass(mActivity, SaveProgramAsTemplateActivity.class);
        startActivity(intent);
    }

    /**
     * 上傳成功，更新狀態
     * @param historyEntity historyEntity
     */
    private void updateHistoryUploadStatus(HistoryEntity historyEntity) {
        new BaseCallWebApi().updateHistoryUploadStatus(historyEntity);
    }
}