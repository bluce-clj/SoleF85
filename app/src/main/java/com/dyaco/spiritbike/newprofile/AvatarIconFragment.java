package com.dyaco.spiritbike.newprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.R;

import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class AvatarIconFragment extends BaseFragment {

    private RadioGroup m_rgUp_AvatarIcon;
    private RadioGroup m_rgDown_AvatarIcon;
    private boolean m_bChecked = false;

    private String mUserName;
    private String mBirthDay;
    private int mGender;
    private int mWeightMetric;
    private int mHeightMetric;
    private int mWeightImperial;
    private int mHeightImperial;
    private int mAvatarIcon = -1;
    private View mView;
    private int mUnit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_avatar_icon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        m_rgUp_AvatarIcon = view.findViewById(R.id.rgUp_AvatarIcon);
        m_rgDown_AvatarIcon = view.findViewById(R.id.rgDown_AvatarIcon);

        ConstraintLayout clAvatarIcon = view.findViewById(R.id.clAvatarIcon);
        Button btClose_AvatarIcon = view.findViewById(R.id.btClose_AvatarIcon);
        Button btBack_AvatarIcon = view.findViewById(R.id.btBack_AvatarIcon);
        Button btAllDone_AvatarIcon = view.findViewById(R.id.btAllDone_AvatarIcon);
        ImageView ivPageIndex_AvatarIcon = view.findViewById(R.id.ivPageIndex_AvatarIcon);
        TextView tvTitle_AvatarIcon = view.findViewById(R.id.tvTitle_AvatarIcon);

        clAvatarIcon.setBackgroundResource(R.drawable.background_popup_down);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String m_strAction = bundle.getString("action");

            mUserName = getArguments().getString("userName","");
            mBirthDay = getArguments().getString("birthDay","");
            mGender = getArguments().getInt("gender",1);
            mWeightMetric = getArguments().getInt("weight_metric",60);
            mHeightMetric = getArguments().getInt("height_metric",170);
            mWeightImperial = getArguments().getInt("weight_imperial",200);
            mHeightImperial = getArguments().getInt("height_imperial",70);
            mUnit = getArguments().getInt("unit",0);


            switch (m_strAction) {
                case "action_newBiodataFragment_to_avatarIconFragment":
                    ivPageIndex_AvatarIcon.setImageResource(R.drawable.element_pagination_profile_5);
                    break;

                case "action_soleUserNameFragment_to_avatarIconFragment":
                    ivPageIndex_AvatarIcon.setImageResource(R.drawable.element_pagination_hr_2);
                    break;

                case "action_newQrCodeFragment_to_editUserNameFragment":
                    btBack_AvatarIcon.setText(R.string.cancel);
                    btAllDone_AvatarIcon.setText(R.string.save);
                    ivPageIndex_AvatarIcon.setVisibility(View.INVISIBLE);
                    tvTitle_AvatarIcon.setText(R.string.edit_user_name);
                    break;
            }
        }

        m_rgUp_AvatarIcon.setOnCheckedChangeListener(rgAvatarIconOnCheckedChange);
        m_rgDown_AvatarIcon.setOnCheckedChangeListener(rgAvatarIconOnCheckedChange);

        btClose_AvatarIcon.setOnClickListener(btAvatarIconOnClick);
        btBack_AvatarIcon.setOnClickListener(btAvatarIconOnClick);
        btAllDone_AvatarIcon.setOnClickListener(btAvatarIconOnClick);

    }

    private final View.OnClickListener btAvatarIconOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.btBack_AvatarIcon) {
               // Navigation.findNavController(view).popBackStack();

                Bundle bundle = new Bundle();
                bundle.putString("userName", mUserName);
                bundle.putString("birthDay", mBirthDay);
                bundle.putInt("gender", mGender);
                bundle.putInt("unit", mUnit);
                bundle.putInt("weight_metric", mWeightMetric);
                bundle.putInt("height_metric", mHeightMetric);
                bundle.putInt("weight_imperial", mWeightImperial);
                bundle.putInt("height_imperial", mHeightImperial);

                Navigation.findNavController(view).navigate(R.id.action_avatarIconFragment_to_newBiodataFragment, bundle);

            } else if(view.getId() == R.id.btAllDone_AvatarIcon){


                if (m_rgUp_AvatarIcon.getCheckedRadioButtonId() != -1) {
                    mAvatarIcon = m_rgUp_AvatarIcon.getCheckedRadioButtonId();
                }

                if (m_rgDown_AvatarIcon.getCheckedRadioButtonId() != -1) {

                    mAvatarIcon = m_rgDown_AvatarIcon.getCheckedRadioButtonId();
                }

                if (mAvatarIcon != -1) {

                    mAvatarIcon = CommonUtils.getAvatarResourceId(mAvatarIcon);
                    saveData();
                } else {
                    Toast.makeText(getInstance(), "Please Choose an Avatar.", Toast.LENGTH_SHORT).show();
                }

             //   Navigation.findNavController(view).navigate(R.id.action_avatarIconFragment_to_dialogProfileCreatedFragment);

                               /*
                    switch (m_strAction) {
                        case "action_newBiodataFragment_to_avatarIconFragment":
                            ivPageIndex_AvatarIcon.setImageResource(R.drawable.element_pagination_55);
                            break;
                        case "action_soleUserNameFragment_to_avatarIconFragment":
                            ivPageIndex_AvatarIcon.setImageResource(R.drawable.element_pagination_22);
                            break;
                        case "action_newQrCodeFragment_to_editUserNameFragment":
                            ivPageIndex_AvatarIcon.setVisibility(View.INVISIBLE);
                            tvTitle_AvatarIcon.setText(R.string.edit_user_name);
                            break;
                    }

                     */

            } else { //btClose_AvatarIcon

                Navigation.findNavController(view).navigate(R.id.action_avatarIconFragment_to_dialogDataLostFragment);
            }
        }
    };

    private final RadioGroup.OnCheckedChangeListener rgAvatarIconOnCheckedChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (m_bChecked)
                return;

            if (radioGroup.getId() == R.id.rgUp_AvatarIcon) {
                m_bChecked = true;
                m_rgDown_AvatarIcon.clearCheck();
                m_bChecked = false;
            } else if (radioGroup.getId() == R.id.rgDown_AvatarIcon) {
                m_bChecked = true;
                m_rgUp_AvatarIcon.clearCheck();
                m_bChecked = false;
            }
        }
    };


    private void saveData() {

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setBirthday(mBirthDay);
        userProfileEntity.setUserName(mUserName);
        userProfileEntity.setGender(mGender);
        userProfileEntity.setUserImage(mAvatarIcon);
        userProfileEntity.setHeight_metric(mHeightMetric);
        userProfileEntity.setWeight_metric(mWeightMetric);
        userProfileEntity.setHeight_imperial(mHeightImperial);
        userProfileEntity.setWeight_imperial(mWeightImperial);
        userProfileEntity.setUserType(1);
        userProfileEntity.setUnit(mUnit);

        Log.d("SSSSLLLLL", "updateUserProfile: " + getInstance().getDeviceSettingBean().getDisplay_mode());
        //user sleep mode on  不sleep
        userProfileEntity.setSleepMode(getInstance().getDeviceSettingBean().getDisplay_mode());

        Log.d("SSSSLLLLL", "uuuu: " + userProfileEntity.getSleepMode());
        UNIT_E = mUnit;

        DatabaseManager.getInstance(MyApplication.getInstance()).insertUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {

            @Override
            public void onAdded(long rowId) {
                super.onAdded(rowId);

                userProfileEntity.setUid((int)rowId);
             //   MMKV.defaultMMKV().encode(USER_PROFILE_ENTITY, userProfileEntity);
                getInstance().setUserProfile(userProfileEntity);

                Navigation.findNavController(mView).navigate(R.id.action_avatarIconFragment_to_dialogProfileCreatedFragment);
            }

            @Override
            public void onError(String err) {
                super.onError(err);

                Log.i("DBDBDB", "onError: " + err);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DatabaseManager.getInstance(getInstance()).roomClear();
        mView = null;
    }
}