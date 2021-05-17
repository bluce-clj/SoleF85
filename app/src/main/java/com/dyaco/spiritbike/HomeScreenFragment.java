package com.dyaco.spiritbike;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.corestar.libs.ftms.TrainingStatus;
import com.dyaco.spiritbike.profile.EditAgeActivity;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.HomeScreenAdapter;
import com.dyaco.spiritbike.support.HomeScreenBannerAdapter;
import com.dyaco.spiritbike.support.HomeScreenBannerPojo;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.MyItemDecoration;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.UnitEnum;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.support.banner.Banner;
import com.dyaco.spiritbike.support.banner.indicator.RectangleIndicator;
import com.dyaco.spiritbike.support.banner.util.BannerUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndFavorites;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.isFTMSNotify;
import static com.dyaco.spiritbike.MyApplication.isFtmsConnected;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class HomeScreenFragment extends BaseFragment {
    private UserProfileEntity userProfileEntity;

    private HomeScreenAdapter homeScreenAdapter;
    private RecyclerView recyclerView;
    private Banner banner;
    private RectangleIndicator indicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(true);
        ((DashboardActivity) mActivity).changeSignOutToBack(false, true, 0, -1);

        userProfileEntity = getInstance().getUserProfile();

//        UNIT_E = userProfileEntity.getUnit();

        initView(view);
    }

    private void initView(View view) {

        Button btQuickStart_HomeScreen = view.findViewById(R.id.btQuickStart_HomeScreen);

        //快速啟動
        btQuickStart_HomeScreen.setOnClickListener(v -> {

            WorkoutBean workoutBean = new WorkoutBean();
            workoutBean.setProgramName(MANUAL.getText());
            workoutBean.setProgramId(MANUAL.getCode());
            workoutBean.setOrgMaxLevel(20);
            workoutBean.setBaseProgramId(MANUAL.getCode());
            workoutBean.setInclineDiagramNum(MANUAL.getInclineNum());
            workoutBean.setLevelDiagramNum(MANUAL.getLevelNum());
            workoutBean.setMaxLevel(20);
            workoutBean.setTimeSecond(0);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
            intent.setClass(mActivity, WorkoutDashboardActivity.class);
            startActivity(intent);
            mActivity.finish();
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            MyApplication.SSEB = false;


        });

        banner = view.findViewById(R.id.b_banner);
        indicator = view.findViewById(R.id.indicator);

        updateUserProfileMonth();

        //  initBanner();

//        String totalRun = CommonUtils.formatDecimal(userProfileEntity.getTotalRun(),0);
//       // String avgPaceThisMonth = String.format(Locale.getDefault(), "%.0f", userProfileEntity.getAvgPaceInMonth());
//        String avgPowerThisMonth = String.format(Locale.getDefault(), "%.0f", userProfileEntity.getAvgPaceInMonth()); //2f
//
//
//
//        Log.d("@@@@@@@", "GGGinitView: ");
//
//        String totalDistance = CommonUtils.formatDecimal(UNIT_E == 0 ? userProfileEntity.getTotalDistance_metric() : userProfileEntity.getTotalDistance_imperial(), 1);
//
//        List<HomeScreenBannerPojo> programInfos = new ArrayList<>();
//
//        programInfos.add(new HomeScreenBannerPojo(1, totalDistance, UnitEnum.getUnit(UnitEnum.DISTANCE), "TOTAL DISTANCE"));
//        programInfos.add(new HomeScreenBannerPojo(2, totalRun, "", "TOTAL WORKOUTS"));
//        programInfos.add(new HomeScreenBannerPojo(2, avgPowerThisMonth, "", "AVG POWER THIS MONTH"));
//
//        Banner banner = view.findViewById(R.id.b_banner);
//        RectangleIndicator indicator = view.findViewById(R.id.indicator);
//        HomeScreenBannerAdapter adapter = new HomeScreenBannerAdapter(mActivity, programInfos);
//        banner.setAdapter(adapter);
//        banner.isAutoLoop(true);
//        banner.setLoopTime(5000);
//
//        banner.setIndicator(indicator, false);
//        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(4));
//        banner.setIndicatorWidth((int) BannerUtils.dp2px(4), (int) BannerUtils.dp2px(4));
//        banner.setIndicatorHeight((int) BannerUtils.dp2px(4));
//        banner.setIndicatorNormalColorRes(R.color.colorFFFFFF);
//        banner.setIndicatorSelectedColorRes(R.color.colorFFFFFF);
//        banner.setIndicatorSpace((int) BannerUtils.dp2px(24));
//        banner.setIndicatorRadius(0);

        LinearLayoutManager loopLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);

        //  loopLayoutManager.setLooperEnable(false);
        recyclerView = view.findViewById(R.id.rcvFavorite_HomerScreen);
        recyclerView.setLayoutManager(loopLayoutManager);
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setHasFixedSize(true);

        homeScreenAdapter = new HomeScreenAdapter(mActivity);
        recyclerView.setAdapter(homeScreenAdapter);

        homeScreenAdapter.setOnItemClickListener(bean -> {

            Bundle bundleSend = new Bundle();
            int programId = ProgramsEnum.getProgram(bean.getFavoriteType()).getCode();
            bundleSend.putInt("item", programId);
            if (programId == -2)
                bundleSend.putBoolean("Open_Templates", true);
            ((DashboardActivity) mActivity).rbPrograms_Dashboard.setChecked(true);
            ((DashboardActivity) mActivity).navController.navigate(programId >= 0 ? R.id.programsDetailsFragment : R.id.programsFragment, bundleSend);
        });

        getData();
    }

    private void initBanner() {

        String totalRun = CommonUtils.formatDecimal(userProfileEntity.getTotalRun(), 0);
        String avgPowerThisMonth = String.format(Locale.getDefault(), "%.0f", userProfileEntity.getAvgPaceInMonth()); //2f

        String totalDistance = CommonUtils.formatDecimal(UNIT_E == 0 ? userProfileEntity.getTotalDistance_metric() : userProfileEntity.getTotalDistance_imperial(), 1);

        List<HomeScreenBannerPojo> programInfos = new ArrayList<>();

        programInfos.add(new HomeScreenBannerPojo(1, totalDistance, UnitEnum.getUnit(UnitEnum.DISTANCE), "TOTAL DISTANCE"));
        programInfos.add(new HomeScreenBannerPojo(2, totalRun, "", "TOTAL WORKOUTS"));
        programInfos.add(new HomeScreenBannerPojo(2, avgPowerThisMonth, "", "AVG POWER THIS MONTH"));

        HomeScreenBannerAdapter adapter = new HomeScreenBannerAdapter(mActivity, programInfos);
        banner.setAdapter(adapter);
        banner.isAutoLoop(true);
        banner.setLoopTime(5000);

        banner.setIndicator(indicator, false);
        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(4));
        banner.setIndicatorWidth((int) BannerUtils.dp2px(4), (int) BannerUtils.dp2px(4));
        banner.setIndicatorHeight((int) BannerUtils.dp2px(4));
        banner.setIndicatorNormalColorRes(R.color.colorFFFFFF);
        banner.setIndicatorSelectedColorRes(R.color.colorFFFFFF);
        banner.setIndicatorSpace((int) BannerUtils.dp2px(24));
        banner.setIndicatorRadius(0);
    }

    List<FavoritesEntity> favoritesEntityList = new ArrayList<>();

    private void getData() {
        DatabaseManager.getInstance(MyApplication.getInstance()).getFavoriteFromUserProfile(userProfileEntity.getUid(),
                new DatabaseCallback<UserProfileAndFavorites>() {
                    @Override
                    public void onDataLoadedList(List<UserProfileAndFavorites> userProfileAndFavoritesList) {
                        super.onDataLoadedList(userProfileAndFavoritesList);

                        for (UserProfileAndFavorites userProfileAndFavorites : userProfileAndFavoritesList) {
                            favoritesEntityList.addAll(userProfileAndFavorites.favoritesEntityList);
                        }

                        FavoritesEntity favoritesEntity = new FavoritesEntity();
                        favoritesEntity.setFavoriteName("See More Programs");
                        favoritesEntity.setFavoriteType(-1);
                        favoritesEntityList.add(favoritesEntity);

                        FavoritesEntity favoritesEntity2 = new FavoritesEntity();
                        favoritesEntity2.setFavoriteName("Choose from Saved");
                        favoritesEntity2.setFavoriteType(-2);
                        favoritesEntityList.add(favoritesEntity2);

                        homeScreenAdapter.setData2View(favoritesEntityList);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (recyclerView != null) {
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        banner = null;
        super.onDestroyView();
    }


    private void updateUserProfileMonth() {

        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month != userProfileEntity.getWorkoutMonth()) {

            userProfileEntity.setAvgPaceInMonth(0d);
            userProfileEntity.setWorkoutMonth(month);
            userProfileEntity.setWattFrequency(0);
            userProfileEntity.setWattAccumulate(0d);

            DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity,
                    new DatabaseCallback<UserProfileEntity>() {
                        @Override
                        public void onUpdated() {
                            super.onUpdated();
                            initBanner();
                        }

                        @Override
                        public void onError(String err) {
                            super.onError(err);
                        }
                    });
        } else {
            initBanner();
        }

    }
}