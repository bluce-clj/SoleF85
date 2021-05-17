package com.dyaco.spiritbike.profile;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;


public class EditUserNameActivity extends BaseAppCompatActivity {
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private TextView tvTextError;
    private TextInputEditText etTextInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_name);

        tvTextError = findViewById(R.id.tv_text_error);
        ConstraintLayout clNewUserName = findViewById(R.id.clUserName);
        etTextInputEditText = findViewById(R.id.etTextInputEditText);
        Button btClose_UserName = findViewById(R.id.btClose);
        TextView tvTitle_UserName = findViewById(R.id.tvTitle_UserName);
        View vUnderline_UserName = findViewById(R.id.vUnderline_UserName);
        tvTitle_UserName.setText("EDIT USERNAME");
        vUnderline_UserName.setBackgroundColor(ContextCompat.getColor(this, R.color.colorB4BEC7));


        clNewUserName.setBackgroundResource(R.drawable.background_popup_down);

        btClose_UserName.setOnClickListener(btUserNameOnClick);

//        etUserName_UserName.requestFocus();
//        etUserName_UserName.setOnKeyListener(etUserNameOnKey);


        etTextInputEditText.requestFocus();
        etTextInputEditText.findFocus();
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


        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

        etTextInputEditText.setText(userProfileEntity.getUserName());
    }


    private boolean textCheck() {

        String text = Objects.requireNonNull(etTextInputEditText.getText()).toString();

        return !"".equals(text);
    }

    private final View.OnClickListener btUserNameOnClick = view -> {
        MyApplication.SSEB = false;
        finish();
    };

    private final View.OnKeyListener etUserNameOnKey = (view, keyCode, keyEvent) -> {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

            if (textCheck()) {
                updateData();
            } else {
                tvTextError.setText("Input USER NAME");
            }

            return true;
        }
        return false;
    };

    private void updateData() {
        userProfileEntity.setUserName(Objects.requireNonNull(etTextInputEditText.getText()).toString());
        DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
            @Override
            public void onUpdated() {
                super.onUpdated();

                if (checkStr(userProfileEntity.getSoleAccountNo()))
                    new BaseCallWebApi().callSyncUserDataApi(userProfileEntity);

                MyApplication.SSEB = false;
                finish();
            }

            @Override
            public void onError(String err) {
                super.onError(err);

                Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
            }
        });
    }

    private BtnExitFullScreen btnExitFullScreen = new BtnExitFullScreen(this);
    @Override
    protected void onPause() {
        super.onPause();
        btnExitFullScreen.showBtnFullScreenExit(DashboardActivity.class, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnExitFullScreen.removeFloatView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnExitFullScreen.removeFloatView();
        btnExitFullScreen = null;
    }

}