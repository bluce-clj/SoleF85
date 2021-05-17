package com.dyaco.spiritbike.programs;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corestar.views.TouchBarView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dyaco.spiritbike.MyApplication.DEFAULT_SEEK_VALUE;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;

public class AdjustCustomInclineActivity extends BaseAppCompatActivity {
    // List<SeekBar> seekBarsList = new ArrayList<>();
    List<TextView> sbTextsList = new ArrayList<>();
    List<String> valueList = new ArrayList<>();
    private UserProfileEntity userProfileEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_custom_incline);
        userProfileEntity = getInstance().getUserProfile();
        //  initView();
        initDelay();
    }

    private void initDelay() {
        Looper.myQueue().addIdleHandler(() -> {
            initView();
            initBar();
            return false;
        });
    }

    private void initBar() {

        TouchBarView touchBarView = findViewById(R.id.touch_bar_view);
        touchBarView.setLevelChangedListener((bar, level) -> {
            valueList.set(bar, String.valueOf(level - 1));
            float ll = (level - 1) / 2f; //顯示
            sbTextsList.get(bar).setText(String.valueOf(ll));
        });

        int[] array;
        String values = userProfileEntity.getCustomInclineNum();
        if (values == null) {
            values = DEFAULT_SEEK_VALUE;
        }

        array = Arrays.stream(values.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        for (int i = 0; i < touchBarView.getBarCount(); i++) {
            touchBarView.setBarLevel(i, array[i] + 1);
            String seeBarText = "sb" + (i + 1) + "_text";
            int resID2 = getResources().getIdentifier(seeBarText, "id", getPackageName());
            sbTextsList.add(findViewById(resID2));

            sbTextsList.get(i).setText(String.valueOf((array[i]) / 2f));
            valueList.add(String.valueOf(array[i]));
        }

        Log.d("JJJJJJJ", "valueList: " + valueList.toString());
    }

    private void initView() {
        Button close = findViewById(R.id.btClose);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        close.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        btYes_DialogDataLost.setOnClickListener(v -> {
                    StringBuilder num = new StringBuilder();
                    StringBuilder dNum = new StringBuilder();
                    for (int i = 0; i < 20; i++) {
                        //    num.append(sbTextsList.get(i).getText().toString()).append("#");
                        num.append(valueList.get(i)).append("#");
                        dNum.append((Integer.parseInt(valueList.get(i)) + 1)).append("#");
                    }

                    num = num.deleteCharAt(num.length() - 1);
                    dNum = dNum.deleteCharAt(dNum.length() - 1);

                    //   byte[] byteArray = new CommonUtils().getImageBytes(this, num.toString(), 600);
                    byte[] byteArray = new CommonUtils().getImageBytes(this, dNum.toString(), 600);
                    userProfileEntity.setCustomInclineNum(num.toString());//值
                    userProfileEntity.setInclineDiagram(byteArray);//圖

                    Log.d("JJJJJJJ", "initView: " + userProfileEntity.getCustomInclineNum());

                    DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity,
                            new DatabaseCallback<UserProfileEntity>() {
                                @Override
                                public void onUpdated() {
                                    super.onUpdated();
                                    MyApplication.SSEB = false;
                                    setResult(RESULT_OK, null);
                                    finish();
                                }

                                @Override
                                public void onError(String err) {
                                    super.onError(err);
                                    Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                                }
                            });
                }
        );
    }

//    private void initView() {
//
//        int[] array;
//        String values = userProfileEntity.getCustomInclineNum();
//        if (values == null) {
//            values = DEFAULT_SEEK_VALUE;
//        }
//
//        array = Arrays.stream(values.split("#", -1))
//                .mapToInt(Integer::parseInt)
//                .toArray();
//
//        for (int i = 0; i < 20; i++) {
//            String seekBar = "sb" + (i + 1);
//            String seeBarText = "sb" + (i + 1) + "_text";
//            int resID1 = getResources().getIdentifier(seekBar, "id", getPackageName());
//            int resID2 = getResources().getIdentifier(seeBarText, "id", getPackageName());
//            seekBarsList.add(findViewById(resID1));
//            sbTextsList.add(findViewById(resID2));
//
//            seekBarsList.get(i).setProgress(array[i]);
//            sbTextsList.get(i).setText(String.valueOf(array[i]));
//            int finalI = i;
//            seekBarsList.get(i).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    sbTextsList.get(finalI).setText(String.valueOf(progress));
//                }
//            });
//        }
//    }

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