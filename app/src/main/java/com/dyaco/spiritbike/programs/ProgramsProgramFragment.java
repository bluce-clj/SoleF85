package com.dyaco.spiritbike.programs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndFavorites;
import com.dyaco.spiritbike.R;

import java.util.List;

import static com.dyaco.spiritbike.support.ProgramsEnum.CARDIO;
import static com.dyaco.spiritbike.support.ProgramsEnum.CUSTOM;
import static com.dyaco.spiritbike.support.ProgramsEnum.FATBURN;
import static com.dyaco.spiritbike.support.ProgramsEnum.HEART_RATE;
import static com.dyaco.spiritbike.support.ProgramsEnum.HIIT;
import static com.dyaco.spiritbike.support.ProgramsEnum.HILL;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;
import static com.dyaco.spiritbike.support.ProgramsEnum.STRENGTH;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class ProgramsProgramFragment extends BaseFragment {
    private List<FavoritesEntity> favoritesEntityList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView ivTagManual_Programs;
    private ImageView ivTagHill_Programs;
    private ImageView ivTagFatburn_Programs;
    private ImageView ivTagCardio_Programs;
    private ImageView ivTagStrength_Programs;
    private ImageView ivTagHiit_Programs;
    private ImageView ivTagHeartRate_Programs;
    private ImageView ivTagCustom_Programs;

    private String mParam1;
    private int openProgram;

    public ProgramsProgramFragment() {

    }

    public static ProgramsProgramFragment newInstance(String param1, int openProgram) {
        ProgramsProgramFragment fragment = new ProgramsProgramFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt("openProgram", openProgram);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            openProgram = getArguments().getInt("openProgram", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_programs_program, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivTagManual_Programs = view.findViewById(R.id.ivTagManual_Programs);
        ivTagHill_Programs = view.findViewById(R.id.ivTagHill_Programs);
        ivTagFatburn_Programs = view.findViewById(R.id.ivTagFatburn_Programs);
        ivTagCardio_Programs = view.findViewById(R.id.ivTagCardio_Programs);
        ivTagStrength_Programs = view.findViewById(R.id.ivTagStrength_Programs);
        ivTagHiit_Programs = view.findViewById(R.id.ivTagHiit_Programs);
        ivTagHeartRate_Programs = view.findViewById(R.id.ivTagHeartRate_Programs);
        ivTagCustom_Programs = view.findViewById(R.id.ivTagCustom_Programs);

        Button btManual_Programs = view.findViewById(R.id.btManual_Programs);
        Button btHill_Programs = view.findViewById(R.id.btHill_Programs);
        Button btFatburn_Programs = view.findViewById(R.id.btFatburn_Programs);
        Button btCardio_Programs = view.findViewById(R.id.btCardio_Programs);
        Button btStrength_Programs = view.findViewById(R.id.btStrength_Programs);
        Button btHiit_Programs = view.findViewById(R.id.btHiit_Programs);
//        Button bt5kRun_Programs = view.findViewById(R.id.bt5kRun_Programs);
//        Button bt10kRun_Programs = view.findViewById(R.id.bt10kRun_Programs);
        Button btHeartRate_Programs = view.findViewById(R.id.btHeartRate_Programs);
        Button btCustom_Programs = view.findViewById(R.id.btCustom_Programs);

        btManual_Programs.setOnClickListener(btProgramsOnClick);
        btHill_Programs.setOnClickListener(btProgramsOnClick);
        btFatburn_Programs.setOnClickListener(btProgramsOnClick);
        btCardio_Programs.setOnClickListener(btProgramsOnClick);
        btStrength_Programs.setOnClickListener(btProgramsOnClick);
        btHiit_Programs.setOnClickListener(btProgramsOnClick);
//        bt5kRun_Programs.setOnClickListener(btProgramsOnClick);
//        bt10kRun_Programs.setOnClickListener(btProgramsOnClick);
        btHeartRate_Programs.setOnClickListener(btProgramsOnClick);
        btCustom_Programs.setOnClickListener(btProgramsOnClick);

    }


    private void initData() {
        UserProfileEntity userProfileEntity = getInstance().getUserProfile();

        DatabaseManager.getInstance(MyApplication.getInstance()).getFavoriteFromUserProfile(userProfileEntity.getUid(), new DatabaseCallback<UserProfileAndFavorites>() {
            @Override
            public void onDataLoadedList(List<UserProfileAndFavorites> userProfileAndFavoritesList) {
                super.onDataLoadedList(userProfileAndFavoritesList);

                for (UserProfileAndFavorites userProfileAndFavorites : userProfileAndFavoritesList) {

                    for (FavoritesEntity favoritesEntity : userProfileAndFavorites.favoritesEntityList) {


                        switch (favoritesEntity.getFavoriteType()) {
                            case 0:
                                if (ivTagManual_Programs != null)
                                    ivTagManual_Programs.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                if (ivTagHill_Programs != null) {
                                    ivTagHill_Programs.setVisibility(View.VISIBLE);
                                } else {
                                    Log.d("@@@@@@@", "ivTagHill_Programs: "+ ivTagHill_Programs);
                                }
                                break;
                            case 2:
                                if (ivTagFatburn_Programs != null)
                                    ivTagFatburn_Programs.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                if (ivTagCardio_Programs != null)
                                    ivTagCardio_Programs.setVisibility(View.VISIBLE);
                                break;
                            case 4:
                                if (ivTagStrength_Programs != null)
                                    ivTagStrength_Programs.setVisibility(View.VISIBLE);
                                break;
                            case 5:
                                if (ivTagHiit_Programs != null)
                                 ivTagHiit_Programs.setVisibility(View.VISIBLE);
                                break;
                            case 8:
                                if (ivTagHeartRate_Programs != null)
                                ivTagHeartRate_Programs.setVisibility(View.VISIBLE);
                                break;
                            case 9:
                                if (ivTagCustom_Programs != null)
                                ivTagCustom_Programs.setVisibility(View.VISIBLE);
                                break;
                        }

//                        favoritesEntityList.add(favoritesEntity);
//                        Log.i("BBBBB", "favoritesEntity: " + favoritesEntity.getFavoriteName());
                    }
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener btProgramsOnClick = view -> {
        //  ((DashboardActivity)requireActivity()).startSleepTimer();
        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.btManual_Programs:
                bundle.putInt("item", MANUAL.getCode());
                break;

            case R.id.btHill_Programs:
                bundle.putInt("item", HILL.getCode());
                break;
            case R.id.btFatburn_Programs:
                bundle.putInt("item", FATBURN.getCode());
                break;
            case R.id.btCardio_Programs:
                bundle.putInt("item", CARDIO.getCode());
                break;
            case R.id.btStrength_Programs:
                bundle.putInt("item", STRENGTH.getCode());
                break;
            case R.id.btHiit_Programs:
                bundle.putInt("item", HIIT.getCode());
                break;
            case R.id.btHeartRate_Programs:
                bundle.putInt("item", HEART_RATE.getCode());
                break;
            case R.id.btCustom_Programs:
                bundle.putInt("item", CUSTOM.getCode());
                break;
        }
        //  Navigation.findNavController(view).navigate(R.id.action_programsFragment_to_programsDetailsFragment, bundle);
        try {
            ((DashboardActivity) mActivity).navController.navigate(R.id.action_programsFragment_to_programsDetailsFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ivTagManual_Programs = null;
        ivTagHill_Programs = null;
        ivTagFatburn_Programs = null;
        ivTagCardio_Programs = null;
        ivTagStrength_Programs = null;
        ivTagHiit_Programs = null;
        ivTagHeartRate_Programs = null;
        ivTagCustom_Programs = null;
    }
}