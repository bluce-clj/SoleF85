package com.dyaco.spiritbike.programs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.product_flavors.BaseFragment2;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.ProgramInfo;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.banner.Banner;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndFavorites;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;


public class ProgramsDetailsFragment extends BaseFragment2 {
    private UserProfileEntity userProfileEntity;
    private int programId;
    private Banner banner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_programs_details, container, false);
    }

    @Override
    protected int requireLayoutId() {
        return 0;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, R.id.action_programsDetailsFragment_to_programsFragment, -1);

        Bundle bundle = getArguments();
        if (bundle != null) {
            programId = bundle.getInt("item");
        }

        banner = view.findViewById(R.id.rcvPrograms_ProgramsDetails);

        getData();

    }

    @Override
    protected void onFindView(View rootView) {

    }

    @Override
    protected void onBindListener() {

    }

//    private final Observer mViaPoiResultObserver = new Observer<Pair<Integer, Object>>() {
//        @Override
//        public void onChanged(Pair<Integer, Object> integerObjectPair) {
//            Log.i("KKKKKK", "RequestCode: [ " + integerObjectPair.first + " ] " + " ResultData: [ " + integerObjectPair.second + " ] ");
//        }
//    };

    List<FavoritesEntity> favoritesEntityList = new ArrayList<>();

    private void getData() {

        userProfileEntity = getInstance().getUserProfile();

        DatabaseManager.getInstance(MyApplication.getInstance()).getFavoriteFromUserProfile(userProfileEntity.getUid(), new DatabaseCallback<UserProfileAndFavorites>() {
            @Override
            public void onDataLoadedList(List<UserProfileAndFavorites> userProfileAndFavoritesList) {
                super.onDataLoadedList(userProfileAndFavoritesList);

                for (UserProfileAndFavorites userProfileAndFavorites : userProfileAndFavoritesList) {
                    favoritesEntityList.addAll(userProfileAndFavorites.favoritesEntityList);
                }
                initData();
            }
        });
    }

    private void initData() {
        List<ProgramInfo> programInfos = getProgramInfos();
        int i = 0;
        switch (ProgramsEnum.getProgram(programId)) {
            case MANUAL:
                i = 1;
                break;
            case HILL:
                i = 2;
                break;
            case FATBURN:
                i = 3;
                break;
            case CARDIO:
                i = 4;
                break;
            case STRENGTH:
                i = 5;
                break;
            case HIIT:
                i = 6;
                break;
            case HEART_RATE:
                i = 7;
                break;
            case CUSTOM:
                i = 8;
                break;
        }

        ProgramsDetailAdapter adapter = new ProgramsDetailAdapter(mActivity, programInfos);
        banner.setStartPosition(i);
        banner.setAdapter(adapter);
        banner.isAutoLoop(false);
        banner.setBannerGalleryEffect(108, 108, 24, 1);

        ((DashboardActivity) mActivity).setIndicator(banner);

        //  adapter.setData2View(programInfos);

        //Choose
        adapter.setOnItemClickListener(programsEnum -> {
//            NavOptions builder = new NavOptions.Builder()
//                    .setLaunchSingleTop(true).build();

            Bundle bundle1 = new Bundle();
            bundle1.putInt("ProgramId", programsEnum.getCode());
            ((DashboardActivity) mActivity).navController.navigate(programsEnum.getAdjustFragmentId(), bundle1);

//            startFragmentForResult(programsEnum.getAdjustFragmentId(), 2, bundle1)
//                    .observe(this, mViaPoiResultObserver);


        });

        adapter.setOnImageClickListener(type -> {
            Intent intent;
            if ("LEVEL".equals(type)) {
                intent = new Intent(mActivity, AdjustCustomLevelActivity.class);
            } else {
                intent = new Intent(mActivity, AdjustCustomInclineActivity.class);
            }
            MyApplication.SSEB = false;
            startActivityForResult(intent, 0);
        });

        adapter.setOnCheckItemClickListener(this::saveFavorite);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                programId = ProgramsEnum.CUSTOM.getCode();
                getData();
            }
        }
    }

    private void saveFavorite(ProgramsEnum programsEnum, boolean isCheck) {

        if (isCheck) {

            FavoritesEntity favoritesEntity = new FavoritesEntity();
            favoritesEntity.setFavoriteName(programsEnum.getText());
            favoritesEntity.setFavoriteType(programsEnum.getCode());
            favoritesEntity.setFavoriteParentUid(userProfileEntity.getUid());
            DatabaseManager.getInstance(MyApplication.getInstance()).insertFavorite(favoritesEntity, new DatabaseCallback<FavoritesEntity>() {
                @Override
                public void onAdded(long rowId) {
                    super.onAdded(rowId);
                }

                @Override
                public void onError(String err) {
                    super.onError(err);
                }
            });
        } else {

            DatabaseManager.getInstance(MyApplication.getInstance()).
                    deleteFavorite(userProfileEntity.getUid(), programsEnum.getCode(),
                            new DatabaseCallback<FavoritesEntity>() {
                                @Override
                                public void onDeleted() {
                                    super.onDeleted();
                                }

                                @Override
                                public void onError(String err) {
                                    super.onError(err);
                                }
                            });
        }
    }

    private static final String TAG = "ProgramsDetailsFragment";

    private List<ProgramInfo> getProgramInfos() {
        boolean check0 = false, check1 = false, check2 = false, check3 = false, check4 = false, check5 = false, check8 = false, check9 = false;

        for (FavoritesEntity favoritesEntity : favoritesEntityList) {
            switch (favoritesEntity.getFavoriteType()) {
                case 0:
                    check0 = true;
                    break;
                case 1:
                    check1 = true;
                    break;
                case 2:
                    check2 = true;
                    break;
                case 3:
                    check3 = true;
                    break;
                case 4:
                    check4 = true;
                    break;
                case 5:
                    check5 = true;
                    break;
                case 8:
                    check8 = true;
                    break;
                case 9:
                    check9 = true;
                    break;
            }
        }
        List<ProgramInfo> programInfos = new ArrayList<>();
        int manualDesc = MODE == XE395ENT ? R.string.manual_desc : R.string.manual_desc2;
        int hillDesc = MODE == XE395ENT ? R.string.hill_desc : R.string.hill_desc2;
        int fatburnDesc = MODE == XE395ENT ? R.string.fatburn_desc : R.string.fatburn_desc2;
        int cardioDesc = MODE == XE395ENT ? R.string.cardio_desc : R.string.cardio_desc2;
        int strengthDesc = MODE == XE395ENT ? R.string.strength_desc : R.string.strength_desc2;
        int hiitDesc = MODE == XE395ENT ? R.string.hiit_desc : R.string.hiit_desc2;
        int heartRateDesc = MODE == XE395ENT ? R.string.heart_rate_desc : R.string.heart_rate_desc2;
        int customDesc = MODE == XE395ENT ? R.string.custom_desc : R.string.custom_desc2;
        programInfos.add(new ProgramInfo(R.string.manual, manualDesc, true, R.drawable.photo_programs_manual, 0, 0, ProgramsEnum.MANUAL, check0, null, null));
        programInfos.add(new ProgramInfo(R.string.hill, hillDesc, false, 0, R.drawable.diagram_hill_level, R.drawable.diagram_hill_incline, ProgramsEnum.HILL, check1, null, null));
        programInfos.add(new ProgramInfo(R.string.fatburn, fatburnDesc, false, 0, R.drawable.diagram_fatburn_level, R.drawable.diagram_fatburn_incline, ProgramsEnum.FATBURN, check2, null, null));
        programInfos.add(new ProgramInfo(R.string.cardio, cardioDesc, false, 0, R.drawable.diagram_cardio_level, R.drawable.diagram_cardio_incline, ProgramsEnum.CARDIO, check3, null, null));
        programInfos.add(new ProgramInfo(R.string.strength, strengthDesc, false, 0, R.drawable.diagram_strength_level, R.drawable.diagram_strength_incline, ProgramsEnum.STRENGTH, check4, null, null));
        programInfos.add(new ProgramInfo(R.string.hiit, hiitDesc, false, 0, R.drawable.diagram_hiit_level, R.drawable.diagram_hiit_incline, ProgramsEnum.HIIT, check5, null, null));
        programInfos.add(new ProgramInfo(R.string.heart_rate, heartRateDesc, true, R.drawable.photo_programs_hr, 0, 0, ProgramsEnum.HEART_RATE, check8, null, null));
        programInfos.add(new ProgramInfo(R.string.custom, customDesc, false, 0, 0, 0, ProgramsEnum.CUSTOM, check9, userProfileEntity.getLevelDiagram(), userProfileEntity.getInclineDiagram()));
        return programInfos;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        banner = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}