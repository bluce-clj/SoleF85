package com.dyaco.spiritbike.newprofile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class UserNameFragment extends BaseFragment {

    private String m_strAction;

    private TextView tvTextError;
    private TextInputEditText etTextInputEditText;
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

        Log.d("@@@@@@", "onCreate11111: " + mBirthDay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTextError = view.findViewById(R.id.tv_text_error);

        ConstraintLayout clNewUserName = view.findViewById(R.id.clUserName);
        // EditText etUserName_UserName = view.findViewById(R.id.etUserName_UserName);
        TextInputLayout etUserName_UserName = view.findViewById(R.id.etUserName_UserName);
        etTextInputEditText = view.findViewById(R.id.etTextInputEditText);
        etTextInputEditText.setText(mUserName);
        Button btClose_UserName = view.findViewById(R.id.btClose);
        TextView tvTitle_UserName = view.findViewById(R.id.tvTitle_UserName);
        ImageView ivPageIndex_UserName = view.findViewById(R.id.ivPageIndex_UserName);
        View vUnderline_UserName = view.findViewById(R.id.vUnderline_UserName);

        tvTitle_UserName.setText("USERNAME");
        vUnderline_UserName.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
        // vUnderline_UserName.setBackgroundColor( ContextCompat.getColor(getContext(), R.color.color9D2227) );

        Bundle bundle = getArguments();
        if (bundle != null) {
            m_strAction = bundle.getString("action");
            switch (m_strAction) {
                case "action_newQrCodeFragment_to_userNameFragment":
                case "action_newBirthdayFragment_to_userNameFragment":
                    ivPageIndex_UserName.setImageResource(R.drawable.element_pagination_profile_1);
                    break;
                case "action_newQrCodeFragment_to_soleUserNameFragment":
                    ivPageIndex_UserName.setImageResource(R.drawable.element_pagination_profile_sole_1);
                    break;
                case "action_newQrCodeFragment_to_editUserNameFragment":
                    ivPageIndex_UserName.setVisibility(View.INVISIBLE);
                    tvTitle_UserName.setText(R.string.edit_user_name);
                    break;
            }
        }

        clNewUserName.setBackgroundResource(R.drawable.background_popup_down);

        btClose_UserName.setOnClickListener(btUserNameOnClick);

        etTextInputEditText.requestFocus();
        etTextInputEditText.setOnKeyListener(etUserNameOnKey);

        etTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvTextError.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        InputMethodManager imm = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    private final View.OnClickListener btUserNameOnClick = view -> {
        Bundle bundle = new Bundle();

        if (view.getId() == R.id.btClose) {
            bundle.putString("action", "action_userNameFragment_to_dialogDataLostFragment");
            Navigation.findNavController(view).navigate(R.id.action_userNameFragment_to_dialogDataLostFragment, bundle);
        }
    };

    private final View.OnKeyListener etUserNameOnKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            Bundle bundle = new Bundle();
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                switch (m_strAction) {
                    case "action_newQrCodeFragment_to_newUserNameFragment":
                    case "action_newBirthdayFragment_to_userNameFragment":


                        if (textCheck()) {

                            bundle.putString("userName", Objects.requireNonNull(etTextInputEditText.getText()).toString());
                            bundle.putString("birthDay", mBirthDay);
                            bundle.putInt("gender", mGender);
                            bundle.putInt("unit", mUnit);
                            bundle.putInt("weight_metric", mWeightMetric);
                            bundle.putInt("height_metric", mHeightMetric);
                            bundle.putInt("weight_imperial", mWeightImperial);
                            bundle.putInt("height_imperial", mHeightImperial);

                            Navigation.findNavController(view).navigate(R.id.action_userNameFragment_to_newBirthdayFragment, bundle);
                        } else {
                            tvTextError.setText("INPUT NAME");
                        }

                        break;

                    case "action_newQrCodeFragment_to_soleUserNameFragment":
                        bundle.putString("action", "action_soleUserNameFragment_to_soleIconFragment");
                        Navigation.findNavController(view).navigate(R.id.action_userNameFragment_to_avatarIconFragment, bundle);
                        break;

                    case "action_newQrCodeFragment_to_editUserNameFragment":
                        //Navigation.findNavController(view).navigate(R.id.action_userNameFragment_to_infoFragment);
                        break;
                }
                return true;
            }
            return false;
        }
    };


    private boolean textCheck() {

        String text = Objects.requireNonNull(etTextInputEditText.getText()).toString();
//        if (!"".equals(text)) {
//            checkUserName(text);
//        }

        return !"".equals(text);
    }

//    private void checkUserName(String str) {
//        DatabaseManager.getInstance(getActivity()).checkUserName(str, new DatabaseCallback<Integer>() {
//            @Override
//            public void onCount(Integer i) {
//                super.onCount(i);
//
//                if (i <= 0) {
//
//                } else {
//                    tvTextError.setText("this name belongs to a built-in program");
//                }
//            }
//        });
//    }
}