package com.dyaco.spiritbike.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndTemplates;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.dyaco.spiritbike.support.CommonUtils.getImageBytes2;
import static com.dyaco.spiritbike.support.CommonUtils.isInteger;
import static com.dyaco.spiritbike.support.ProgramsEnum.CARDIO;
import static com.dyaco.spiritbike.support.ProgramsEnum.CUSTOM;
import static com.dyaco.spiritbike.support.ProgramsEnum.FATBURN;
import static com.dyaco.spiritbike.support.ProgramsEnum.HEART_RATE;
import static com.dyaco.spiritbike.support.ProgramsEnum.HIIT;
import static com.dyaco.spiritbike.support.ProgramsEnum.HILL;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;
import static com.dyaco.spiritbike.support.ProgramsEnum.STRENGTH;
import static com.dyaco.spiritbike.MyApplication.getInstance;


/**
 * 儲存成Template
 * 1.from HistorySummaryDetailActivity
 * 2.from WorkoutResultSummaryFragment
 */
public class SaveProgramAsTemplateActivity extends BaseAppCompatActivity {
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();

    private TextView tv_auto_fill;
    private TextView tvTextError;
    private TextInputEditText etTextInputEditText;
    private HistoryEntity historyEntity;
    private final ArrayList<TemplateEntity> templateEntityList = new ArrayList<>();
    private String autoProgramName;
    private WorkoutBean workoutBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_program_as);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        historyEntity = (HistoryEntity) bundle.getSerializable("historyEntity");
        workoutBean = (WorkoutBean) bundle.getSerializable("WorkoutBean");

        ConstraintLayout clNewUserName = findViewById(R.id.clUserName);
        tvTextError = findViewById(R.id.tv_text_error);
        TextInputLayout etUserName_UserName = findViewById(R.id.etUserName_UserName);
        etTextInputEditText = findViewById(R.id.etTextInputEditText);
        Button btClose_UserName = findViewById(R.id.btClose);
        TextView tvTitle_UserName = findViewById(R.id.tvTitle_UserName);
        View vUnderline_UserName = findViewById(R.id.vUnderline_UserName);
        //  tvTitle_UserName.setText(R.string.user_name);
        vUnderline_UserName.setBackgroundColor(ContextCompat.getColor(this, R.color.colorB4BEC7));


        clNewUserName.setBackgroundResource(R.drawable.background_popup_down);

        btClose_UserName.setOnClickListener(btUserNameOnClick);

        etTextInputEditText.requestFocus();
        etTextInputEditText.setOnKeyListener(etUserNameOnKey);

        tv_auto_fill = findViewById(R.id.tv_auto_fill);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

        etUserName_UserName.setEndIconOnClickListener(v -> {
            etTextInputEditText.setText("");
            tvTextError.setText("");
        });

        etTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTextError.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        getTemplateName();
    }

    private final View.OnClickListener btUserNameOnClick = view -> {
        MyApplication.SSEB = false;
        finish();
    };

    private final View.OnKeyListener etUserNameOnKey = (view, keyCode, keyEvent) -> {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

            checkName();

            return true;
        }

//        if (keyCode == KeyEvent.KEYCODE_DEL) {
//
//            tvTextError.setText("");
//
//            return true;
//        }
        return false;
    };

    private boolean M(String str) {
        boolean c = true;
        if (MANUAL.getText().equals(str)) c = false;
        if (HILL.getText().equalsIgnoreCase(str)) c = false;
        if (FATBURN.getText().equalsIgnoreCase(str)) c = false;
        if (CARDIO.getText().equalsIgnoreCase(str)) c = false;
        if (STRENGTH.getText().equalsIgnoreCase(str)) c = false;
        if (HIIT.getText().equalsIgnoreCase(str)) c = false;
        if (HEART_RATE.getText().equalsIgnoreCase(str)) c = false;
        if (CUSTOM.getText().equalsIgnoreCase(str)) c = false;

        return c;
    }

    /**
     * 檢查名稱
     */
    private void checkName() {
        String str = Objects.requireNonNull(etTextInputEditText.getText()).toString();

        if (!M(str)) {
            tvTextError.setText("this name belongs to a built-in program");
            return;
        }

        DatabaseManager.getInstance(MyApplication.getInstance()).checkTemplateName(str, userProfileEntity.getUid(),
                new DatabaseCallback<Integer>() {
                    @Override
                    public void onCount(Integer i) {
                        super.onCount(i);


                        Log.i("@@@@@@@@@@", "onCount: " + i);

                        if (i <= 0) {
                            saveTemplate();
                        } else {
                            tvTextError.setText("this name belongs to a built-in program");
                        }
                    }
                });
    }

    //儲存 Template
    private void saveTemplate() {

        String templateName = Objects.requireNonNull(etTextInputEditText.getText()).toString();

        if ("".equals(templateName)) {
            templateName = autoProgramName;
        }

        int parentUid = userProfileEntity.getUid();
        int programId;
        String levelDiagram;
        String inclineDiagram;

        byte[] levelDiagramByte;
        byte[] inclineDiagramByte;

        if (historyEntity == null) { //from WorkoutResultSummaryFragment
            programId = workoutBean.getBaseProgramId();
            levelDiagram = workoutBean.getLevelDiagramNum();
            inclineDiagram = workoutBean.getInclineDiagramNum();
        } else { //from HistorySummaryDetailActivity 抓 HistoryEntity
            programId = historyEntity.getBaseProgramId();
            levelDiagram = historyEntity.getDiagramLevel();
            inclineDiagram = historyEntity.getDiagramIncline();
        }

        StringBuilder inclineNum = new StringBuilder();
        inclineNum.append(inclineDiagram);

        levelDiagramByte = getImageBytes2(this, levelDiagram, 20, 400);
        //  inclineDiagramByte = getImageBytes(this, inclineNum, 1200);

        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setTemplateName(templateName);
        templateEntity.setTemplateParentUid(parentUid);
        templateEntity.setUseProgramId(HILL.getCode()); //Template 都當成 一般 Program
        templateEntity.setBaseProgramId(programId);
        templateEntity.setDiagramLevel(levelDiagram);
        templateEntity.setDiagramIncline(inclineDiagram);
        templateEntity.setLevelDiagram(levelDiagramByte);
        //   templateEntity.setInclineDiagram(inclineDiagramByte);

        DatabaseManager.getInstance(MyApplication.getInstance()).insertTemplate(templateEntity, new DatabaseCallback<TemplateEntity>() {
            @Override
            public void onAdded(long rowId) {
                super.onAdded(rowId);

                MyApplication.SSEB = false;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
              //  bundle.putString("name", etTextInputEditText.getText().toString());
                bundle.putSerializable("TemplateEntity", templateEntity);
                intent.putExtras(bundle);
                intent.setClass(SaveProgramAsTemplateActivity.this, SaveProgramSuccessActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(String err) {
                super.onError(err);
                Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getTemplateName() {

        DatabaseManager.getInstance(MyApplication.getInstance()).getTemplateFromUserProfile(userProfileEntity.getUid(), new DatabaseCallback<UserProfileAndTemplates>() {
            @Override
            public void onDataLoadedList(List<UserProfileAndTemplates> userProfileAndTemplatesList) {
                super.onDataLoadedList(userProfileAndTemplatesList);
                for (UserProfileAndTemplates userProfileAndTemplates : userProfileAndTemplatesList) {
                    templateEntityList.addAll(userProfileAndTemplates.templateEntityList);
                }
            //    autoProgramName = historyEntity == null ? ProgramsEnum.getProgram(workoutBean.getProgramId()).getText() + " " : ProgramsEnum.getProgram(historyEntity.getBaseProgramId()).getText() + " ";
                autoProgramName = historyEntity == null ? ProgramsEnum.getProgram(workoutBean.getBaseProgramId()).getText() + " " : ProgramsEnum.getProgram(historyEntity.getBaseProgramId()).getText() + " ";
                List<Integer> nameList = new ArrayList<>();
                for (TemplateEntity templateEntity : templateEntityList) {
                    if (templateEntity.getTemplateName().contains(autoProgramName)) {
                        String s = templateEntity.getTemplateName().substring(templateEntity.getTemplateName().length() - 3);
                        if (isInteger(s)) {
                            nameList.add(Integer.valueOf(s));
                        }
                    }
                }

                nameList.sort(Collections.reverseOrder());
                String nameNum;
                if (nameList.size() == 0) {
                    nameNum = "001";
                } else {
                    nameNum = String.format(Locale.getDefault(), "%03d", nameList.get(0) + 1);
                }

                autoProgramName += nameNum;


                String text = "AUTOFILL:" + autoProgramName;

                // Log.i("BBBBBBBBB", "onDataLoadedList: " + text);
                tv_auto_fill.setText(text);
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