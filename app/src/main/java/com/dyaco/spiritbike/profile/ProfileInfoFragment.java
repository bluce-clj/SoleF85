package com.dyaco.spiritbike.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.R;


import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.getAvatarProfileResourceId;

import static com.dyaco.spiritbike.support.CommonUtils.in2ft;
import static com.dyaco.spiritbike.support.CommonUtils.in2sin;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;

public class ProfileInfoFragment extends BaseFragment {
    TextView tvInfoUserName_Profile;
    TextView tvInfoHeight_Profile;
    TextView tvInfoAge_Profile;
    TextView tvInfoGender_Profile;
    TextView tvInfoWeight_Profile;
    TextView tvHeight_ft;
    TextView tvHeight_in;
    Button btInfoAvatar_Profile;
    Button btInfoUserName_Profile;
    Button btInfoDeleteProfile_Profile;
    Button btInfoHeight_Profile;
    Button btInfoAge_Profile;
    Button btInfoGender_Profile;
    Button btInfoWeight_Profile;
    Button btInfoSoleAccount_Profile;
    ImageView ivAvatar;
    Group imperial_group;
    Group mm_group;
    TextView height_unit_in;

    private ImageView ivSoleAccount;
    private TextView tvSoleAccount;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView tvInfoWeightUnit_Profile;
    private TextView tvInfoHeightUnit_Profile;

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    public static ProfileInfoFragment newInstance(String param1, String param2) {
        ProfileInfoFragment fragment = new ProfileInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_info, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initEvent();
    }

    private void intData() {

        UserProfileEntity userProfileEntity = getInstance().getUserProfile();

        ((DashboardActivity)mActivity).initUserData(userProfileEntity);

        String userName = userProfileEntity.getUserName();
        String gender = userProfileEntity.getGender() == 0 ? "Female" : "Male";
        String age = CommonUtils.getAgeByBirth(userProfileEntity.getBirthday());

        if (checkStr(userProfileEntity.getSoleAccountNo())) {
            ivSoleAccount.setImageResource(R.drawable.status_done_g);
            tvSoleAccount.setText(userProfileEntity.getSoleAccount());
        } else {
            ivSoleAccount.setImageResource(R.drawable.icon_attention);
            tvSoleAccount.setText("NONE");
        }


        String weight;
        String height;
        int heightImperial;
        if (userProfileEntity.getUnit() == 0) {
            //公制
            tvInfoWeightUnit_Profile.setText("kg");
            tvInfoHeightUnit_Profile.setText("cm");
            weight = String.valueOf(userProfileEntity.getWeight_metric());
            height = String.valueOf(userProfileEntity.getHeight_metric());
            tvInfoHeight_Profile.setText(height);
           // tvInfoHeight_Profile.setVisibility(View.VISIBLE);
            imperial_group.setVisibility(View.INVISIBLE);
            mm_group.setVisibility(View.VISIBLE);
        } else {
            //英制
            tvInfoWeightUnit_Profile.setText("lb");
            tvInfoHeightUnit_Profile.setText("in");
            weight = String.valueOf(userProfileEntity.getWeight_imperial());
            heightImperial = userProfileEntity.getHeight_imperial();
         //   tvInfoHeight_Profile.setVisibility(View.INVISIBLE);
            imperial_group.setVisibility(View.VISIBLE);
            mm_group.setVisibility(View.INVISIBLE);

            tvHeight_ft.setText(String.valueOf(in2ft(heightImperial)));
            tvHeight_in.setText(String.valueOf(in2sin(heightImperial)));
        }

        int avatar = getAvatarProfileResourceId(userProfileEntity.getUserImage());

        if (checkStr(userProfileEntity.getSoleHeaderImgUrl()) && isConnected(mActivity)) {

            GlideApp.with(mActivity)
                    .load(userProfileEntity.getSoleHeaderImgUrl())
                    .placeholder(R.drawable.avatar_start_no)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .error(avatar)
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(avatar);
        }
        tvInfoUserName_Profile.setText(userName);
        tvInfoWeight_Profile.setText(weight);
        tvInfoGender_Profile.setText(gender);
        tvInfoAge_Profile.setText(age);
    }

    private void initView(View view) {
        ivSoleAccount = view.findViewById(R.id.iv_account_icon);
        tvSoleAccount = view.findViewById(R.id.tv_account);

        mm_group = view.findViewById(R.id.mm_group);
        imperial_group = view.findViewById(R.id.imperial_group);
        tvHeight_ft = view.findViewById(R.id.tvHeight_ft);
        tvHeight_in = view.findViewById(R.id.tvHeight_in);
        tvInfoWeightUnit_Profile = view.findViewById(R.id.tvInfoWeightUnit_Profile);
        tvInfoHeightUnit_Profile = view.findViewById(R.id.tvInfoHeightUnit_Profile);
        ivAvatar = view.findViewById(R.id.ivInfoAvatar_Profile);
        tvInfoUserName_Profile = view.findViewById(R.id.tvInfoUserName_Profile);
        tvInfoHeight_Profile = view.findViewById(R.id.tvInfoHeight_Profile);
        tvInfoAge_Profile = view.findViewById(R.id.tvInfoAge_Profile);
        tvInfoGender_Profile = view.findViewById(R.id.tvInfoGender_Profile);
        tvInfoWeight_Profile = view.findViewById(R.id.tvInfoWeight_Profile);

        btInfoAvatar_Profile = view.findViewById(R.id.btInfoAvatar_Profile);
        btInfoUserName_Profile = view.findViewById(R.id.btInfoUserName_Profile);
        btInfoDeleteProfile_Profile = view.findViewById(R.id.btInfoDeleteProfile_Profile);
        btInfoHeight_Profile = view.findViewById(R.id.btInfoHeight_Profile);
        btInfoAge_Profile = view.findViewById(R.id.btInfoAge_Profile);
        btInfoGender_Profile = view.findViewById(R.id.btInfoGender_Profile);
        btInfoWeight_Profile = view.findViewById(R.id.btInfoWeight_Profile);
        btInfoSoleAccount_Profile = view.findViewById(R.id.btInfoSoleAccount_Profile);
    }

    private void initEvent() {

        //SOLE ACCOUNT
        btInfoSoleAccount_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, LinkSoleAppActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
          //  ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });

        btInfoAvatar_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditIconActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
        //    ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });


        btInfoUserName_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditUserNameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
         //   ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });

        btInfoDeleteProfile_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, DeleteProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
        //    ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });

        btInfoHeight_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditHeightActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
         //   ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });

        btInfoAge_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditAgeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
         //   ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });

        btInfoGender_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditGenderActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
         //   ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });

        btInfoWeight_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, EditWeightActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("action", "action_dialogProfileCreatedFragment_to_dashboardFragment");
            intent.putExtras(bundle);
            startActivity(intent);
          //  ((DashboardActivity)requireActivity()).startSleepTimer();
            MyApplication.SSEB = false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        intData();
    }

}