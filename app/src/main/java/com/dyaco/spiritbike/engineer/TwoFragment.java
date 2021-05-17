package com.dyaco.spiritbike.engineer;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.KeyBean;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.product_flavors.ModeEnum;

import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.kProgressHudUtil;
import com.dyaco.spiritbike.uart.McuBean;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tencent.mmkv.MMKV;

import java.util.Arrays;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.COMMAND_KEY;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.ON_AUTO_TEST;
import static com.dyaco.spiritbike.MyApplication.ON_HEART_RATE_CHANGED;
import static com.dyaco.spiritbike.MyApplication.ON_INCLINE_ADJUST;
import static com.dyaco.spiritbike.MyApplication.ON_LEVEL_ADJUST;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isAutoTest;

public class TwoFragment extends Fragment {
    private CompositeDisposable compositeDisposable;
    private Button btn_key_test;
    private Button btn_motor_manual_test;
    private Button btn_motor_auto_test;
    private Button btn_incline_manual_test;
    private Button btn_incline_auto_test;
    private TextView tv_motor_manual_ad_num;
    private TextView tv_motor_auto_ad_num;
    private TextView tv_incline_manual_ad_num;
    private TextView tv_incline_auto_ad_num;
    private TextView tv_key;

    private Button btn_motor_manual_plus;
    private Button btn_motor_manual_minus;
    private Button btn_incline_manual_plus;
    private Button btn_incline_manual_minus;

    private TextView tv_incline_L;
    private TextView tv_level_L;


    private TextView tv_motor_auto_num;
    private TextView tv_incline_auto_num;
    private TextView tv_rpm_num;

    private RxTimer levelRxTimer;
    private RxTimer inclineRxTimer;
    private RxTimer rpmTestTimer;
    private Button btn_control_test;

    private boolean isLevelAutoTestRunning = false;
    private boolean isInclineAutoTestRunning = false;

    private int[] inclineAd;
    private int[] levelAd;

    private TextView tv_wp;
    private TextView tv_hp;
    private TextView tv_hrs;
    private TextView tv_pass;

    RxTimer mmpLongTimer;

    public TwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compositeDisposable = new CompositeDisposable();

        tv_key = view.findViewById(R.id.tv_key);
        tv_pass = view.findViewById(R.id.tv_pass);
        btn_key_test = view.findViewById(R.id.btn_key_test);
        btn_motor_manual_test = view.findViewById(R.id.btn_motor_manual_test);
        btn_motor_auto_test = view.findViewById(R.id.btn_motor_auto_test);
        btn_incline_auto_test = view.findViewById(R.id.btn_incline_auto_test);
        btn_incline_manual_test = view.findViewById(R.id.btn_incline_manual_test);
        tv_motor_manual_ad_num = view.findViewById(R.id.tv_motor_manual_ad_num);
        tv_incline_manual_ad_num = view.findViewById(R.id.tv_incline_manual_ad_num);
        tv_incline_auto_ad_num = view.findViewById(R.id.tv_incline_auto_ad_num);
        btn_motor_manual_plus = view.findViewById(R.id.btn_motor_manual_plus);
        btn_motor_manual_minus = view.findViewById(R.id.btn_motor_manual_minus);
        btn_incline_manual_plus = view.findViewById(R.id.btn_incline_manual_plus);
        btn_incline_manual_minus = view.findViewById(R.id.btn_incline_manual_minus);
        tv_motor_auto_num = view.findViewById(R.id.tv_motor_auto_num);


        if (MODE != ModeEnum.XE395ENT) {
            btn_incline_auto_test.setVisibility(View.INVISIBLE);
            btn_incline_manual_test.setVisibility(View.INVISIBLE);
        }

        tv_wp = view.findViewById(R.id.tv_wp);
        tv_hp = view.findViewById(R.id.tv_hp);
        tv_hrs = view.findViewById(R.id.tv_hrs);

        tv_rpm_num = view.findViewById(R.id.tv_rpm_num);

        btn_control_test = view.findViewById(R.id.btn_control_test);

        tv_incline_L = view.findViewById(R.id.tv_incline_L); //incline ad階數
        tv_incline_auto_num = view.findViewById(R.id.tv_incline_auto_num); //incline auto test 跑了幾次

        tv_level_L = view.findViewById(R.id.tv_level_L); //level ad階數
        tv_motor_auto_ad_num = view.findViewById(R.id.tv_motor_auto_ad_num);//level auto test 跑了幾次

        initEvent();

        btn_key_test.setOnClickListener(v -> {
            clearAutoIncline();
            clearAutoLevel();
            // getInstance().commandSetLwrMode(Device.LWR_MODE.);
        });

        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();

        String ld = deviceSettingBean.getDsLevelAd();
        if (!TextUtils.isEmpty(ld)) {
            levelAd = Arrays.stream(ld.split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            btn_motor_auto_test.setEnabled(true);
        } else {
            levelAd = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }


        String id = deviceSettingBean.getDsInclineAd();
        if (!TextUtils.isEmpty(id)) {
            inclineAd = Arrays.stream(id.split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            btn_incline_auto_test.setEnabled(true);
        } else {
            inclineAd = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        //rpm test
        btn_control_test.setOnClickListener(v -> {

            clearAutoIncline();
            clearAutoLevel();

            isAutoTest = true;

            ((EngineerActivity) requireActivity()).loadingDialog.startDialog();


            getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);
            new RxTimer().timer(3000, number -> {
             //   ((EngineerActivity) requireActivity()).dismissProgress();
                try {
                    ((EngineerActivity) requireActivity()).loadingDialog.stopDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (rpmTestTimer != null) rpmTestTimer.cancel();
                rpmTestTimer = new RxTimer();
                rpmTestTimer.interval(1000, number2 -> {
                    getInstance().commandSetControl(0, Device.ACTION_MODE.STOP, levelAd[0], Device.ACTION_MODE.STOP, inclineAd[0], 0);
                });
            });
        });

        //level auto test
        btn_motor_auto_test.setOnClickListener(v -> {
            if (rpmTestTimer != null) rpmTestTimer.cancel();
            isAutoTest = true;
            if (!isLevelAutoTestRunning) {
                btn_motor_auto_test.setText("STOP");
                enableButton(false);
                new RxTimer().timer(4000, number -> enableButton(true));
                getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);

                clearAutoIncline();

                isLevelAutoTestRunning = true;

                if (levelRxTimer != null) levelRxTimer.cancel();

                levelRxTimer = new RxTimer();
                levelRxTimer.interval2(2000, number -> autoLevelTest());
            } else {
                clearAutoLevel();
            }
        });

        //incline auto test
//        btn_incline_auto_test.setOnClickListener(v -> {
//            isAutoTest = true;
//            if (!isInclineAutoTestRunning) {
//
//                enableButton(false);
//                new RxTimer().timer(4000, number -> enableButton(true));
//
//                getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);
//
//                isInclineAutoTestRunning = true;
//
//                isLevelAutoTestRunning = false;
//                if (levelRxTimer != null) levelRxTimer.cancel();
//
//                inclineRxTimer = new RxTimer();
//                inclineRxTimer.interval2(3000, number -> autoInclineTest());
//            } else {
//                clearAutoIncline();
//            }
//        });

        btn_incline_auto_test.setOnClickListener(v -> {
            if (rpmTestTimer != null) rpmTestTimer.cancel();
            isAutoTest = true;
            if (!isInclineAutoTestRunning) {
                btn_incline_auto_test.setText("STOP");
                enableButton(false);
                new RxTimer().timer(4000, number -> enableButton(true));
                getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);

                isInclineAutoTestRunning = true;

                clearAutoLevel();
                if (levelRxTimer != null) levelRxTimer.cancel();

                inclineRxTimer = new RxTimer();
                inclineRxTimer.interval2(2000, number -> autoInclineTest());
            } else {
                clearAutoIncline();
            }
        });


        // commandSetLwrMode(Device.LWR_MODE.RESISTANCE_ADJ);//改RESISTANCE_ADJ校正模式
        // commandSetResistanceAdjust(Device.TREMOR.UP,100); //校正 UP
        // commandSetResistanceAdjust(Device.TREMOR.DOWN,100); //校正 DOWN
        // commandSetResistanceAdjust(Device.TREMOR.SEEAD,100); //看AD

        //Motor Manual test
        btn_motor_manual_test.setOnClickListener(v -> {
            if (rpmTestTimer != null) rpmTestTimer.cancel();
            clearAutoIncline();
            clearAutoLevel();
            getInstance().commandSetLwrMode(Device.LWR_MODE.RESISTANCE_ADJ);
        });

        btn_motor_manual_plus.setOnClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            updateLevelAd(Device.TREMOR.UP);
            Log.d("FFFFFF", "@@@@click: ");
        });

        btn_motor_manual_plus.setOnLongClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            mmpLongTimer = new RxTimer();
            mmpLongTimer.interval(250, n -> updateLevelAd(Device.TREMOR.UP));
            return false;
        });

        btn_motor_manual_minus.setOnClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            updateLevelAd(Device.TREMOR.DOWN);
        });

        btn_motor_manual_minus.setOnLongClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            mmpLongTimer = new RxTimer();
            mmpLongTimer.interval(250, n -> updateLevelAd(Device.TREMOR.DOWN));
            return false;
        });

        //   commandSetLwrMode(Device.LWR_MODE.INCLINE_ADJ); //改INCLINE_ADJ校正模式
        //  commandSetInclineAdjust(Device.TREMOR.UP, 100); //校正 UP
        //commandSetInclineAdjust(Device.TREMOR.Down, 100); //校正 UP
        //   commandSetInclineAdjust(Device.TREMOR.SEEAD, 100); //看AD

        //Incline Manual test
        btn_incline_manual_test.setOnClickListener(v -> {
            if (rpmTestTimer != null) rpmTestTimer.cancel();
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            clearAutoIncline();
            clearAutoLevel();
            getInstance().commandSetLwrMode(Device.LWR_MODE.INCLINE_ADJ);
        });

        btn_incline_manual_plus.setOnClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            updateInclineAd(Device.TREMOR.UP);
        });

        btn_incline_manual_plus.setOnLongClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            mmpLongTimer = new RxTimer();
            mmpLongTimer.interval(250, n -> updateInclineAd(Device.TREMOR.UP));
            return false;
        });

        btn_incline_manual_minus.setOnClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            updateInclineAd(Device.TREMOR.DOWN);
        });

        btn_incline_manual_minus.setOnLongClickListener(v -> {
            if (mmpLongTimer != null) mmpLongTimer.cancel();
            mmpLongTimer = new RxTimer();
            mmpLongTimer.interval(250, n -> updateInclineAd(Device.TREMOR.DOWN));
            return false;
        });
    }

    private void enableButton(boolean isEnable) {
        btn_incline_auto_test.setEnabled(isEnable);
        btn_motor_auto_test.setEnabled(isEnable);
    }

    int autoLevelTestCount = 19; //level第20段
    int autoLevelTestCNT = 0; //跑幾次
    int autoLevelTestStatus = 0;
    private boolean isLevelAutoTestPause = false;

    private void autoLevelTest() {
        //    getInstance().commandSetControl(0, Device.ACTION_MODE.NORMAL, levelAd[0], Device.ACTION_MODE.NORMAL, 0, 0);
        if (!isLevelAutoTestPause) {
            Log.d("AUTO_TEST_LEVEL", "跑去第" + (autoLevelTestCount + 1) + "段 ing..");

            Toasty.warning(getInstance(), "Motor Testing...", Toasty.LENGTH_SHORT).show();
            tv_level_L.setText(String.format(Locale.getDefault(), "L.%d", autoLevelTestCount + 1));
          //  tv_level_L.setText(String.format(Locale.getDefault(), "L.%d", autoLevelTestCount));
            getInstance().commandSetControl(0, Device.ACTION_MODE.NORMAL, levelAd[autoLevelTestCount], Device.ACTION_MODE.STOP, inclineAd[0], 0);

        } else {
            Toasty.warning(getInstance(), "PAUSE..", Toasty.LENGTH_SHORT).show();
        }
    }

  //  private int autoInclineTestCount = 18; //incline第19段
    private int autoInclineTestCount = 39; //incline第19段
    private int autoInclineTestCNT = 0; //跑幾次
    private int autoInclineTestStatus = 0;
    private int stopCount = 0;
    private boolean isInclineAutoTestPause = false;

    private void autoInclineTest() {

        if (!isInclineAutoTestPause) {
            Log.d("AUTO_TEST_INCLINE", "跑去第" + (autoInclineTestCount + 1) + "段 ing..");
            Toasty.warning(getInstance(), "Incline Testing...", Toasty.LENGTH_SHORT).show();
            tv_incline_L.setText(String.format(Locale.getDefault(), "L.%d", autoInclineTestCount + 1));
            getInstance().commandSetControl(0, Device.ACTION_MODE.STOP, levelAd[0], Device.ACTION_MODE.NORMAL, inclineAd[autoInclineTestCount], 0);
        } else {
            Toasty.warning(getInstance(), "PAUSE..", Toasty.LENGTH_SHORT).show();
        }
    }
//    private void autoInclineTest() {
//        tv_incline_L.setText(String.format(Locale.getDefault(),"L.%d", autoInclineTestCount + 1));
//        tv_incline_auto_num.setText(String.valueOf(autoInclineTestCNT));
//        getInstance().commandSetControl(0, Device.ACTION_MODE.NORMAL, 0, Device.ACTION_MODE.NORMAL, inclineAd[autoInclineTestCount], 0);
//
//        if (autoInclineTestStatus == 0) {
//            if (autoInclineTestCount != 19) {
//                autoInclineTestCount++;
//            } else {
//                autoInclineTestStatus = 1;
//            }
//        } else {
//            if (autoInclineTestCount != 0) {
//                autoInclineTestCount --;
//            } else {
//                autoInclineTestStatus = 0;
//                autoInclineTestCNT ++;
//            }
//        }
//    }

    private void clearAutoIncline() {
        stopCount = 0;
        btn_incline_auto_test.setText("TEST");
        autoInclineTestCount = 39; //incline第19段
        autoInclineTestCNT = 0;
        autoInclineTestStatus = 0;
        isInclineAutoTestRunning = false;
        isInclineAutoTestPause = false;
        if (inclineRxTimer != null){
            inclineRxTimer.cancel();
            inclineRxTimer = null;
        }
    }

    private void clearAutoLevel() {
        stopCount = 0;
        btn_motor_auto_test.setText("TEST");
        autoLevelTestCount = 19; //incline第19段
        autoLevelTestCNT = 0;
        autoLevelTestStatus = 0;
        isLevelAutoTestRunning = false;
        isLevelAutoTestPause = false;
        if (levelRxTimer != null){
            levelRxTimer.cancel();
            levelRxTimer = null;
        }
    }

    private void updateInclineAd(Device.TREMOR d) {

        getInstance().commandSetInclineAdjust(d, 30);
    //   getInstance().commandSetInclineAdjust(Device.TREMOR.SEEAD, 15);

//        btn_incline_manual_plus.setEnabled(false);
//        btn_incline_manual_minus.setEnabled(false);
//        getInstance().commandSetInclineAdjust(d, 100);
//        new RxTimer().timer(3000, number -> {
//            getInstance().commandSetInclineAdjust(Device.TREMOR.SEEAD, 100);
//            btn_incline_manual_plus.setEnabled(true);
//            btn_incline_manual_minus.setEnabled(true);
//        });
    }

    private void updateLevelAd(Device.TREMOR d) {

        getInstance().commandSetResistanceAdjust(d, 30);
    //   getInstance().commandSetResistanceAdjust(Device.TREMOR.SEEAD, 15);

//        ((EngineerActivity) requireActivity()).showDialogProgress();
//        btn_motor_manual_plus.setEnabled(false);
//        btn_motor_manual_minus.setEnabled(false);
//        getInstance().commandSetResistanceAdjust(d, 10);
//        new RxTimer().timer(3000, number -> {
//            getInstance().commandSetResistanceAdjust(Device.TREMOR.SEEAD, 10);
//            btn_motor_manual_plus.setEnabled(true);
//            btn_motor_manual_minus.setEnabled(true);
//            ((EngineerActivity) requireActivity()).dismissProgress();
//        });
    }

    boolean isKEY01;
    boolean isKEY05;
    boolean isKEY02;
    boolean isKEY06;
    boolean isKEY03;
    boolean isKEY11;
    boolean isKEY10;
    boolean isKEY09;
    boolean isKEY08;

    private void initEvent() {

        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            //Level AD Response
            if (msg.getType() == ON_LEVEL_ADJUST) {

                requireActivity().runOnUiThread(() ->
                        tv_motor_manual_ad_num.setText(String.valueOf(msg.getObj())));
            }

            //Incline AD Response
            if (msg.getType() == ON_INCLINE_ADJUST) {
                requireActivity().runOnUiThread(() ->
                        tv_incline_manual_ad_num.setText(String.valueOf(msg.getObj())));
            }

            if (msg.getType() == COMMAND_KEY) {
                requireActivity().runOnUiThread(() -> {
                    KeyBean keyBean = (KeyBean) msg.getObj();
                    String key = "";
                    getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                    if (keyBean.getKeyStatus() == 0) {
                        key = String.valueOf(keyBean.getKey());
                    } else {
                        key = Arrays.toString(keyBean.getKeys().toArray());
                    }

                    if (keyBean.getKey() == Device.KEY.KEY01) isKEY01 = true;
                    if (keyBean.getKey() == Device.KEY.KEY05) isKEY05 = true;
                    if (keyBean.getKey() == Device.KEY.KEY02) isKEY02 = true;
                    if (keyBean.getKey() == Device.KEY.KEY06) isKEY06 = true;
                    if (keyBean.getKey() == Device.KEY.KEY03) isKEY03 = true;
                    if (keyBean.getKey() == Device.KEY.KEY11) isKEY11 = true;
                    if (keyBean.getKey() == Device.KEY.KEY10) isKEY10 = true;
                    if (keyBean.getKey() == Device.KEY.KEY09) isKEY09 = true;
                    if (keyBean.getKey() == Device.KEY.KEY08) isKEY08 = true;

                    tv_pass.setVisibility(View.INVISIBLE);

                    if (isKEY01 && isKEY05 && isKEY02 && isKEY06 && isKEY03 && isKEY11 && isKEY10 && isKEY09 && isKEY08) {
                        tv_pass.setVisibility(View.VISIBLE);

                        isKEY01 = false;
                        isKEY05 = false;
                        isKEY02 = false;
                        isKEY06 = false;
                        isKEY03 = false;
                        isKEY11 = false;
                        isKEY10 = false;
                        isKEY09 = false;
                        isKEY08 = false;
                    }

                    tv_key.setText(key);
                });
            }

            if (msg.getType() == ON_HEART_RATE_CHANGED) {
                requireActivity().runOnUiThread(() -> tv_hrs.setText(String.valueOf( msg.getObj())));
            }

            if (msg.getType() == ON_AUTO_TEST) {
                requireActivity().runOnUiThread(() -> {

                    boolean levelError = ((McuBean) msg.getObj()).getErrors().contains(Device.MCU_ERROR.RES);
                    boolean inclineError = ((McuBean) msg.getObj()).getErrors().contains(Device.MCU_ERROR.INC);

                    String errorMsg1 = ((McuBean) msg.getObj()).getErrors().toString();
                    String errorMsg2 = "ERROR" + (levelError ? " E2" : "") + (inclineError ? " E3" : "");

                    if (levelError || inclineError) {
                        if (count % 4 == 0)
                            Toasty.error(getInstance(), errorMsg1 + ":" + errorMsg2, Toasty.LENGTH_SHORT).show();

                        count ++;
                    }

                    Log.d("ON_AUTO_TEST", "initEvent: " + ((McuBean) msg.getObj()).toString());
                    tv_hp.setText(String.valueOf(((McuBean) msg.getObj()).getHp()));
                    tv_wp.setText(String.valueOf(((McuBean) msg.getObj()).getWp()));
                    tv_rpm_num.setText(String.valueOf(((McuBean) msg.getObj()).getRpm()));


                    if (isInclineAutoTestRunning) {

                        tv_incline_auto_ad_num.setText(String.valueOf(((McuBean) msg.getObj()).getIncline()));

                        if (((McuBean) msg.getObj()).getInclineStatus() == Device.ACTION_STATUS.STOP) {
                            stopCount++;

                            if (stopCount > 4) {
                                if (autoInclineTestCount == 39) { //到第19段 結束
                                    autoInclineTestCount = 1; // 回到第2段
                                    Log.d("AUTO_TEST_INCLINE", "到第40段結束，再從第2段開始跑");
                                } else {
                                    autoInclineTestCount = 39; //回到19段
                                    autoInclineTestCNT++; //跑了幾次
                                    isInclineAutoTestPause = true;

                                    tv_incline_auto_num.setText(String.valueOf(autoInclineTestCNT));

                                    Toasty.warning(requireContext(), "休息1分鐘", Toast.LENGTH_LONG, true).show();
                                    Log.d("AUTO_TEST_INCLINE", "全部跑完一次，休息一分鐘");
                                    new RxTimer().timer(60 * 1000, number -> isInclineAutoTestPause = false);
                                }
                                stopCount = 0;
                            }
                        } else {
                            stopCount = 0;
                        }

                        //   Log.d("INCLINE_AUTO_TEST", "initEvent: " +(msg.getObj()).toString());
                    } else if (isLevelAutoTestRunning) {
                        Log.d("ON_AUTO_TEST_LEVEL", "initEvent: " + ((McuBean) msg.getObj()).toString());
                        tv_motor_auto_ad_num.setText(String.valueOf(((McuBean) msg.getObj()).getLevel()));

                        if (((McuBean) msg.getObj()).getLevelStatus() == Device.ACTION_STATUS.STOP) {
                            stopCount++;

                            if (stopCount > 4) {
                                if (autoLevelTestCount == 19) { //到第20段 結束
                                    autoLevelTestCount = 0; // 回到第1段
                                    Log.d("AUTO_TEST_LEVEL", "到第20段結束，再從第1段開始跑");
                                } else {
                                    autoLevelTestCount = 19; //回到20段
                                    autoLevelTestCNT++; //跑了幾次

                                    tv_motor_auto_num.setText(String.valueOf(autoLevelTestCNT));

                                    //   Toasty.info(requireContext(), "休息一分鐘", Toast.LENGTH_LONG, true).show();
                                    Log.d("AUTO_TEST_LEVEL", "全部跑完一次，重新開始");
                                    //   new RxTimer().timer(60 * 1000, number -> isLevelAutoTestPause = false);
                                }
                                stopCount = 0;
                            }
                        } else {
                            stopCount = 0;
                        }
                    }
                });
            }
        });
        compositeDisposable.add(d);
    }

    long count = 0;

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
        if (compositeDisposable != null){
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        clear();
    }

    private void clear() {

        if (rpmTestTimer != null){
            rpmTestTimer.cancel();
            rpmTestTimer = null;
        }

        isAutoTest = false;

        autoInclineTestStatus = 0;

        clearAutoIncline();
        clearAutoLevel();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FFFFFFF", "onResume:2 ");
    }
}
