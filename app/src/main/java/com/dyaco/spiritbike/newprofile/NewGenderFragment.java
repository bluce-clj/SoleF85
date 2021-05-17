package com.dyaco.spiritbike.newprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;


public class NewGenderFragment extends BaseFragment {

    private TextView m_tvMale_NewGender;
    private TextView m_tvFemale_NewGender;
    private RadioGroup rgGender_NewGender;
    private RadioButton m_rbMale_NewGender;
    private RadioButton m_rbFemale_NewGender;
    private String mUserName;
    private String mBirthDay;
    private int mGender;
    private int mWeightMetric;
    private int mHeightMetric;
    private int mWeightImperial;
    private int mHeightImperial;
    private int mUnit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mUserName = getArguments().getString("userName","");
            mBirthDay = getArguments().getString("birthDay","");
            mGender = getArguments().getInt("gender",1);
            mWeightMetric = getArguments().getInt("weight_metric",60);
            mHeightMetric = getArguments().getInt("height_metric",170);
            mWeightImperial = getArguments().getInt("weight_imperial",200);
            mHeightImperial = getArguments().getInt("height_imperial",70);
            mUnit = getArguments().getInt("unit",0);
        }

        Log.d("@@@@@@", "mGender: " + mGender);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_gender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rgGender_NewGender = view.findViewById(R.id.rgGender_NewGender);
        rgGender_NewGender.check(R.id.rbMale_NewGender);

        m_tvMale_NewGender = view.findViewById(R.id.tvMale_NewGender);
        m_tvFemale_NewGender = view.findViewById(R.id.tvFemale_NewGender);

        m_rbMale_NewGender = view.findViewById(R.id.rbMale_NewGender);
        m_rbFemale_NewGender = view.findViewById(R.id.rbFemale_NewGender);

        m_rbMale_NewGender.setOnClickListener(rbGenderOnClick);
        m_rbFemale_NewGender.setOnClickListener(rbGenderOnClick);


        Button btBack_NewGender = view.findViewById(R.id.btBack_NewGender);
        Button btNext_NewGender = view.findViewById(R.id.btNext_NewGender);
        Button btClose_NewGender = view.findViewById(R.id.btClose_NewGender);

        btBack_NewGender.setOnClickListener(btNewGenderOnClick);
        btNext_NewGender.setOnClickListener(btNewGenderOnClick);
        btClose_NewGender.setOnClickListener(btNewGenderOnClick);

        if (mGender == 1) {
            m_rbMale_NewGender.setChecked(true);
            m_rbMale_NewGender.callOnClick();
        } else {
            m_rbFemale_NewGender.setChecked(true);
            m_rbFemale_NewGender.callOnClick();
        }

    }

    private final View.OnClickListener btNewGenderOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            int gender;
            int i = rgGender_NewGender.getCheckedRadioButtonId();

            if (i == R.id.rbMale_NewGender) {
                gender = 1;
            } else {
                gender = 0;
            }

            bundle.putString("userName", mUserName);
            bundle.putString("birthDay", mBirthDay);
            bundle.putInt("gender", gender);
            bundle.putInt("unit", mUnit);
            bundle.putInt("weight_metric", mWeightMetric);
            bundle.putInt("height_metric", mHeightMetric);
            bundle.putInt("weight_imperial", mWeightImperial);
            bundle.putInt("height_imperial", mHeightImperial);
            switch (view.getId()) {
                case R.id.btBack_NewGender:
                    Navigation.findNavController(view).navigate(R.id.action_newGenderFragment_to_newBirthdayFragment, bundle);
                    break;
                case R.id.btNext_NewGender:
                    Navigation.findNavController(view).navigate(R.id.action_newGenderFragment_to_newBiodataFragment, bundle);
                    break;
                case R.id.btClose_NewGender:
                    Navigation.findNavController(view).navigate(R.id.action_newGenderFragment_to_dialogDataLostFragment);
                    break;
            }
        }
    };


    private final View.OnClickListener rbGenderOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            m_tvMale_NewGender.setTextColor(m_rbMale_NewGender.isChecked() ?
                    ContextCompat.getColor(mActivity, R.color.colorE4002B) : ContextCompat.getColor(mActivity, R.color.color597084));
            m_tvFemale_NewGender.setTextColor(m_rbFemale_NewGender.isChecked() ?
                    ContextCompat.getColor(mActivity, R.color.colorE4002B) : ContextCompat.getColor(mActivity, R.color.color597084));
        }
    };
}