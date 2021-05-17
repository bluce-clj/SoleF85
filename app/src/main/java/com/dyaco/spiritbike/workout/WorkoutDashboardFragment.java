package com.dyaco.spiritbike.workout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.showToastAlert;
import static com.dyaco.spiritbike.support.ProgramsEnum.HEART_RATE;
import static com.dyaco.spiritbike.workout.WorkoutDashboardActivity.calculation;
import static com.dyaco.spiritbike.workout.WorkoutDashboardFragment.StatusEnum.*;

public class WorkoutDashboardFragment extends BaseFragment {

    private final int HOLDING_HR = 0;
    private int noHrTime;

    enum StatusEnum {IDLE_MODE, REACHING_MODE, MAINTAINING_MODE}

    private static final String TAG = "HeartRateTest@@";

    // private TextView tvCenter;
    private TextView tv_time_sec;
    private TextView tv_time_hour;
    private TextView time_center;

    public TextView tvNormalText;
    public View clNormalView;
    public View clHrView;
    public TextView tvCurrentHR;
    public TextView tvTargetHR;
    public TextView tvHrHint;
    public TextView tvHrNumber;
    private WorkoutDashboardActivity parent;

    private TextView tv_workout_title;
    private HeartRateStatusBean hrStatusBean;
    private RxTimer rxTimer = new RxTimer();

    public WorkoutDashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent = ((WorkoutDashboardActivity) requireActivity());
        initView(view);

        if (HEART_RATE == parent.PROGRAM_TYPE) initHRData();

        isHrShow = true;
    }

    private void initHRData() {

        //  parent.callUpdateLevel(1);

        //HeartRate
        hrStatusBean = new HeartRateStatusBean();
        hrStatusBean.setTargetHR(Integer.parseInt(parent.workoutBean.getTargetHeartRate()));

        //THR_MAX = (220 - AGE);
        int targetHrMax = 220 - Integer.parseInt(CommonUtils.getAgeByBirth(parent.userProfileEntity.getBirthday()));
        hrStatusBean.setTargetHrMax(targetHrMax);

        tv_workout_title.setText(HEART_RATE.getText());
        clNormalView.setVisibility(View.INVISIBLE);
        tvNormalText.setVisibility(View.INVISIBLE);

        clHrView.setVisibility(View.VISIBLE);
        String targetHeartRate = parent.workoutBean.getTargetHeartRate();

        tvTargetHR.setText(targetHeartRate);
        //  tvCurrentHR.setText(currentHR);
    }

    public void setDashBoardTimer(long milliSeconds) {

        String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(milliSeconds));
        String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        tv_time_hour.setText(mm);
        tv_time_sec.setText(ss);

//        if("99".equals(mm) && "59".equals(ss)) {
//            stopT = true;
//        }
    }

    public void setTimeCenter() {
        time_center.setText(":");
    }

    private void initView(View view) {
        tv_workout_title = view.findViewById(R.id.tv_workout_title);
        //  tvCenter = view.findViewById(R.id.tv_center);
        tv_time_sec = view.findViewById(R.id.tv_time_sec);
        tv_time_hour = view.findViewById(R.id.tv_time_hour);
        time_center = view.findViewById(R.id.time_center);
        clNormalView = view.findViewById(R.id.cl_normal);
        tvNormalText = view.findViewById(R.id.tv_normal_text);

        clHrView = view.findViewById(R.id.cl_hr_view);
        tvCurrentHR = view.findViewById(R.id.tv_current_hr);
        tvTargetHR = view.findViewById(R.id.tv_target_hr);
        tvHrHint = view.findViewById(R.id.tv_hr_hint);
        tvHrNumber = view.findViewById(R.id.tv_hr_number);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rxTimer != null){
            rxTimer.cancel();
            rxTimer = null;
        }
        if (showTextRxTimer != null){
            showTextRxTimer.cancel();
            showTextRxTimer = null;
        }
        if (noRpmTimer != null) {
            noRpmTimer.cancel();
            noRpmTimer = null;
        }
        hrStatusBean = null;
        parent = null;
    }

    /**
     * @param sec sec
     */
    public void updateHeartRateStatus(long sec, int currentHR) {

        //更新當前HR
        hrStatusBean.setCurrentHR(currentHR);
        requireActivity().runOnUiThread(() -> tvCurrentHR.setText(String.valueOf(hrStatusBean.getCurrentHR())));

        //IDLE MODE Check
        checkIDLE_MODE(sec);

        //  if (currentHR < 40) return;

        Log.d("心跳@@@@@", "updateHeartRateStatus: ");
        //   if (hrStatusBean.getMode() != IDLE_MODE) checkIDLE_MODE(sec);

        //MAINTAINING_MODE
        if (hrStatusBean.getMode() == MAINTAINING_MODE) {

            if (!hrStatusBean.isMaintaining()) {
                Log.d(TAG, "第一次進入 MAINTAINING 狀態");
                hrStatusBean.setMaintaining(true);
            }

            //MAINTAINING 狀態下， 每15秒執行
            if (sec % 15 == 0) {
                inMaintaining();
            }

        } else {

            //HR0: PROGRAM 開始後的第一次心跳值
            if (hrStatusBean.getHR0() <= 0) hrStatusBean.setHR0(currentHR);

            /*
              REACHING MODE 檢查 : 取得 watt
              condition：
              1.當前狀態不是 REACHING_MODE
              2.watt尚未取得
             */
            if (!hrStatusBean.isWattDone() &&
                    hrStatusBean.getMode() != REACHING_MODE &&
                    hrStatusBean.getMode() != MAINTAINING_MODE)
                checkREACHING_MODE();

            /*
              取ROR1
              condition：
              1.當前狀態為 REACHING_MODE
              2.已取得watt
              3.ROR1尚未取得
             */
            if (hrStatusBean.isWattDone() && !hrStatusBean.isROR1done() && hrStatusBean.getMode() == REACHING_MODE) {
                //進入一次後就不會再進了，
                getROR1(sec);
            }

//            /*取ROR2
//             condition：
//             1.當前狀態為 REACHING_MODE
//             2.ROR1已取得
//             3.ROR2尚未取得
//             */
//            if (hrStatusBean.isROR1done() && (hrStatusBean.getMode() == REACHING_MODE) && !hrStatusBean.isROR2done()) {
//                getROR2(sec);
//            }

            //在 REACHING 狀態，任何時候 HR >= THR 進入 MAINTAINING 狀態
            if (hrStatusBean.getMode() == REACHING_MODE) {
                if (hrStatusBean.getCurrentHR() >= hrStatusBean.getTargetHR()) {
                    rxTimer.cancel();

                    Log.d(TAG, "在 REACHING 狀態，任何時候 HR >= THR 進入 MAINTAINING 狀態");
                    hrStatusBean.setMode(MAINTAINING_MODE);
                    setHintText(R.string.hr_hint_maintaining, "");
                }
            }
        }
    }

    /**
     * IDLE MODE Check
     *
     * @param timerTotalTimeSec time
     */
    private void checkIDLE_MODE(long timerTotalTimeSec) {

        //PROGRAM 開始後，心跳值若超過 THR *1.2 或 THR_MAX*1.2 ，系統將強制回到 IDLE_MODE 。
        if (hrStatusBean.getCurrentHR() > (hrStatusBean.getTargetHR() * 1.2) || hrStatusBean.getCurrentHR() > (hrStatusBean.getTargetHrMax() * 1.2)) {
            //心跳太高，退出Workout
            if (rxTimer != null) rxTimer.cancel();
            if (noRpmTimer != null) noRpmTimer.cancel();
            if (showTextRxTimer != null) showTextRxTimer.cancel();
            // hrStatusBean.setMode(IDLE_MODE);
            parent.showHrDialogAlert(1, "");
            return;
        }

        //PROGRAM 開始後若30秒內檢測不到心跳輸入，系統將強制回到IDLE_MODE
        if (hrStatusBean.getCurrentHR() < 40) {
            //    mActivity.runOnUiThread(() -> {

//                if (isHrShow) {
//                    if (timerTotalTimeSec % 2 == 0) {
//                        showToastAlert(getString(R.string.no_hr_toast_text), mActivity);
//                    }
//                } else {
//                    parent.showHrDialogAlert(0, "");
//                }

            if (!isHrShow || ((WorkoutDashboardActivity) requireActivity()).isWorkoutInBackground) {
                ((WorkoutDashboardActivity) requireActivity()).showHrDialogAlert(0, "");
            } else {
                if (timerTotalTimeSec % 2 == 0) {
                    showToastAlert(getString(R.string.no_hr_toast_text), requireActivity());
                }
            }

            noHrTime++;
            Log.d(TAG, "noHrTime: " + noHrTime);
            if (noHrTime > 30) {
                if (rxTimer != null) rxTimer.cancel();
                if (noRpmTimer != null) noRpmTimer.cancel();
                if (showTextRxTimer != null) showTextRxTimer.cancel();
                //  Log.d("心跳@@@@", "parent: " + parent );
                try {
                    ((WorkoutDashboardActivity) requireActivity()).onWorkOutStop(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //     });
            return;
        } else {
            noHrTime = 0;
            parent.dismissDialog();
        }

        //PROGRAM 開始後若超過5分鐘沒有RPM輸入，系統將強制回到IDLE_MODE
        if (parent.mRPM == 0) {
            if (noRpmTimer == null) {
                Log.d(TAG, "沒RPM，計時五分鐘結束");
                noRpmTimer = new RxTimer();
//                noRpmTimer.timer(1000 * 60 * 5, //1000 * 60 * 5
//                        sec -> requireActivity().runOnUiThread(() -> parent.onWorkOutStop(false)));

                noRpmTimer.timer(1000 * 60 * 5, //1000 * 60 * 5
                        sec -> {
                            try {
                                ((WorkoutDashboardActivity) requireActivity()).onWorkOutStop(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });


                // hrStatusBean.setMode(IDLE_MODE);
                //   mActivity.runOnUiThread(() -> parent.onWorkOutStop(false));
            }
        } else {
            if (noRpmTimer != null) {
                Log.d(TAG, "有RPM");
                noRpmTimer.cancel();
                noRpmTimer = null;
            }
        }
    }

    private RxTimer noRpmTimer;

    boolean isCheckReachingModeDone;

    /**
     * 檢查是否進入 REACHING_MODE
     */
    private void checkREACHING_MODE() {

        Log.d(TAG, "檢查是否進入 REACHING_MODE: " + hrStatusBean.getCurrentHR() + "<=" + (hrStatusBean.getTargetHR() - 5) + " >>" + (hrStatusBean.getCurrentHR() <= (hrStatusBean.getTargetHR() - 5)));

        //若 HR<=THR-5，5秒後系統會依據下列公式 設置第一次 LEVEL 並開始 REACHING 狀態
        if (hrStatusBean.getCurrentHR() <= (hrStatusBean.getTargetHR() - 5)) {

            if (!isCheckReachingModeDone) {
                Log.d(TAG, "若 HR<=THR-5，5秒後系統會依據下列公式 設置第一次 LEVEL 並開始 REACHING 狀態");
                isCheckReachingModeDone = true;
                rxTimer.timer(5000, this::calcReaching);
            }
        }
    }

    /**
     * REACHING_MODE 計算 WATT
     */
    private void calcReaching(long sec) {

        if (!parent.isWorkoutTimerRunning) {
            isCheckReachingModeDone = false;
            Log.d(TAG, "等待五秒時暫停: ");
            return;
        }

        Log.d(TAG, "進入REACHING_MODE 開始計算 WATT");
        hrStatusBean.setMode(REACHING_MODE);

        // mActivity.runOnUiThread(() -> tvHrHint.setText(R.string.hr_hint_2));
        //根據該RPM的WATT值找出適當的LEVEL RPM<15不做判斷，RPM>120 時，以RPM120代入此公式求出WATT值，並以此條件來設置LEVEL 。
        int watt = 0;
        int rpm = Math.min(parent.mRPM, 120);
        if (rpm >= 15) {
            //  Log.d(TAG, "calcReaching: " + );
            watt = (int) CommonUtils.getWATT(parent.userProfileEntity.getWeight_metric(), hrStatusBean.getTargetHR(), rpm);
        }

        Log.d(TAG, "計算 WATT: " + "mRPM:" + parent.mRPM + ",rpm:" + rpm + ",體重:" + parent.userProfileEntity.getWeight_metric() + ",THR:" + hrStatusBean.getTargetHR() + ",WATT:" + watt);
        if (watt < 0) {
            watt = 3;
        }

        //  Log.d(TAG, "calcReaching: " + watt +","+ rpm);
        //  Log.d(TAG, "calcReaching: " + calculation.getWattLevel(watt, rpm));
        parent.callUpdateLevel(calculation.getWattLevel(watt, rpm) - 1, false);

        Log.d(TAG, "取得Watt: " + watt);
        //取得Watt
        hrStatusBean.setWatt(watt);
        hrStatusBean.setWattDone(true);
    }

    /**
     * 取得ROR1
     * 在REACHING狀態，第60秒HR值HR60若不等於0，則做ROR1的計算若HR等於0則強制等待(HOLD在第60秒)HR不等於0時做ROR1的計算 。
     *
     * @param sec sec
     */
    private void getROR1(long sec) {

        Log.d(TAG, "等待60秒後，計算ROR1: " + sec);
        //不到60秒，離開
        if (sec < 60) return;

        //設定HR60
        hrStatusBean.setHR60(hrStatusBean.getCurrentHR());

        //HR60 <= 0，執行Holding，等待 HR60 > 0
        if (hrStatusBean.getHR60() <= HOLDING_HR) {
            Log.d(TAG, "getROR1 HR60 <= " + HOLDING_HR + ",HOLDING");

            hrStatusBean.setROR1Holding(true);
            return;
        }

        Log.d(TAG, "過60秒取得ROR1");
        int ROR1 = CommonUtils.getROR1(hrStatusBean.getTargetHR(), hrStatusBean.getHR0(), hrStatusBean.getHR60());
        int GOAL = CommonUtils.getGOAL(ROR1, hrStatusBean.getHR60(), hrStatusBean.getTargetHR());
        hrStatusBean.setROR1(ROR1);
        hrStatusBean.setROR1done(true);
        hrStatusBean.setGOAL(GOAL);
        hrStatusBean.setROR1Holding(false);

        checkCOAL(true);
    }

    /**
     * ROR1取得後 過15秒 執行
     *
     * @param sec sec
     */
    private void getROR2(long sec) {

        rxTimer.cancel();
        //在 REACHING狀態，第75秒起含第75秒，若HR不等於0做ROR2的計算若HR等於0則強制等待HOLD在第75秒
        hrStatusBean.setHR75(hrStatusBean.getCurrentHR());
        if (hrStatusBean.getHR75() > HOLDING_HR) {
            Log.d(TAG, "getROR2  取得ROR2");
            int ROR2 = CommonUtils.getROR2(hrStatusBean.getHR75(), hrStatusBean.getHR60());
            int GOAL = CommonUtils.getGOAL(ROR2, hrStatusBean.getHR75(), hrStatusBean.getTargetHR());
            hrStatusBean.setROR2(ROR2);
            hrStatusBean.setROR2done(true);
            hrStatusBean.setROR2Holding(false);
            hrStatusBean.setGOAL(GOAL);
            checkCOAL(false);

        } else {

            hrStatusBean.setROR2Holding(true);
            //HR 75 HOLDING，等HR75 不等於 0 做 ROR2 的計算
            rxTimer.interval(1000, number -> {
                if (parent.isWorkoutTimerRunning) {
                    Log.d(TAG, "HR 75 HOLDING: Workout暫停");
                    return;
                }
                Log.d(TAG, "HR75 Holding 直到 HR75 > 0");
                if (hrStatusBean.getCurrentHR() > HOLDING_HR) {
                    rxTimer.cancel();
                    hrStatusBean.setROR2Holding(false);
                    getROR2(number);
                }
            });
        }
    }

    boolean isHigh = false;
    int GOAL;

    /**
     * COAL 判斷
     *
     * @param isROR1 true > 計算ROR1
     */
    private void checkCOAL(boolean isROR1) {

        int updateValue = 0;
        GOAL = hrStatusBean.getGOAL();

        Log.d(TAG, "COAL 判斷 是否可以進入 MAINTAINING_MODE(5 >= GOAL && GOAL >= -5):  GOAL >>> " + GOAL);

        if (5 >= GOAL && GOAL >= -5) {
            //進入Maintaining mode
            hrStatusBean.setMode(MAINTAINING_MODE);
            rxTimer.cancel();
            setHintText(R.string.hr_hint_maintaining, "");

            Log.d(TAG, "checkCOAL: 進入 MAINTAINING_MODE:" + "5 >= " + GOAL + "&& >= -5");
        } else if (GOAL > 15) {
            updateValue = -3;
            isHigh = true;
        } else if (15 >= GOAL && GOAL > 7) {
            updateValue = -2;
            isHigh = true;
        } else if (7 >= GOAL && GOAL > 5) {
            updateValue = -1;
            isHigh = true;
        } else if (GOAL < -40) {
            updateValue = 3;
            isHigh = false;
        } else if (-40 <= GOAL && GOAL < -20) {
            updateValue = 2;
            isHigh = false;
        } else if (-20 <= GOAL && GOAL < -5) {
            updateValue = 1;
            isHigh = false;
        }

        // Log.d(TAG, "checkCOAL 調整LEVEL = "  + parent.getCurrentLevel() +","+ (c));

//        if (parent.getCurrentLevel() + (updateValue) <= 0 ||
//                parent.getCurrentLevel() + (updateValue) > 17) updateValue = 0;

        if (parent.getCurrentLevel() + (updateValue) > 17) {
            if (parent.getCurrentLevel() == 16) updateValue = 1;
            if (parent.getCurrentLevel() == 15) updateValue = 2;
            if (parent.getCurrentLevel() == 17) updateValue = 0;
        }

        if (parent.getCurrentLevel() + (updateValue) <= 0) {
            if (parent.getCurrentLevel() == 2) updateValue = -1;
            if (parent.getCurrentLevel() == 3) updateValue = -2;
            if (parent.getCurrentLevel() == 1) updateValue = 0;
        }

        if (updateValue != 0) {
            setHintText(isHigh ? R.string.hr_hint_too_high : R.string.hr_hint_not_high,
                    String.valueOf(parent.getCurrentLevel() + (updateValue)));

            if (!isHrShow || parent.isWorkoutInBackground) {
                ((WorkoutDashboardActivity) requireActivity()).showHrDialogAlert(isHigh ? 3 : 2, String.valueOf(parent.getCurrentLevel() + (updateValue)));
            }
        }

        if (hrStatusBean.getMode() == REACHING_MODE) {
            //未進入 MAINTAINING_MODE
            parent.callUpdateLevel(updateValue, false);

            Log.d(TAG, "checkCOAL 調整LEVEL = " + (updateValue));

            //ROR1、ROR2 都取得，開始循環
            if (hrStatusBean.isROR1done() && hrStatusBean.isROR2done()) {
                Log.d(TAG, "checkCOAL: ROR1 , ROR2 都已取得");
                //處理完後，HR60 = HR75
                hrStatusBean.setHR60(hrStatusBean.getHR75());
                Log.d(TAG, "checkCOAL: 未進入MAINTAINING 狀態，每15秒重複此步驟 直到進入 MAINTAINING 狀態");
                //若未進入MAINTAINING 狀態，重複此步驟 直到進入 MAINTAINING 狀態
                //15秒後執行
                rxTimer.timer(15000, this::tryToMaintaining);
            } else {
                Log.d(TAG, "checkCOAL: ROR1已取得，ROR2未取得，過15秒執行 取ROR2");
                //ROR1取得後過15秒執行
                if (isROR1) rxTimer.timer(15000, this::getROR2);
            }
        }
    }

    /**
     * ROR1 ROR2 都取得，重複執行tryToMaintaining直到進入 MAINTAINING_MODE
     */
    private void tryToMaintaining(long sec) {

        rxTimer.cancel();

        Log.d(TAG, "tryToMaintaining: 每15秒重複checkCOAL執行 直到進入 MAINTAINING_MODE");
        if (hrStatusBean.getMode() == REACHING_MODE) {
            //下一個 15 秒的 HR( 當下的心跳值 ))=HR75
            hrStatusBean.setHR75(hrStatusBean.getCurrentHR());
            int ROR2 = CommonUtils.getROR2(hrStatusBean.getHR75(), hrStatusBean.getHR60());
            int GOAL = CommonUtils.getGOAL(ROR2, hrStatusBean.getHR75(), hrStatusBean.getTargetHR());
            hrStatusBean.setROR2(ROR2);
            hrStatusBean.setGOAL(GOAL);

            Log.d(TAG, "tryToMaintaining: ROR2:" + ROR2 + ",GOAL:" + GOAL + ",HR75:" + hrStatusBean.getHR75() + ",HR60:" + hrStatusBean.getHR60() + ",TargetHR:" + hrStatusBean.getTargetHR());
            checkCOAL(false);
        }
    }

    /**
     * MAINTAINING 狀態 下， 每隔 15 秒 做下列判斷及調整
     */
    private void inMaintaining() {

        Log.d(TAG, "inMaintaining: MAINTAINING 狀態 下， 每隔 15 秒 做下列判斷及調整LEVEL");
        Log.d(TAG, "inMaintaining: " + hrStatusBean.getCurrentHR() + ">=" + (hrStatusBean.getTargetHR() + 3) + "," + (hrStatusBean.getCurrentHR() >= hrStatusBean.getTargetHR() + 3));
        Log.d(TAG, "inMaintaining: " + hrStatusBean.getCurrentHR() + "<" + (hrStatusBean.getTargetHR() - 3) + "," + (hrStatusBean.getCurrentHR() < hrStatusBean.getTargetHR() - 3));

        if (hrStatusBean.getCurrentHR() >= hrStatusBean.getTargetHR() + 3) {

            if ((parent.getCurrentLevel() - 1) < 0) return;

            parent.callUpdateLevel(-1, false);

            Log.d(TAG, "降低:" + parent.getCurrentLevel());

            setHintText(R.string.hr_hint_too_high, String.valueOf(parent.getCurrentLevel()));

            if (!isHrShow || parent.isWorkoutInBackground)
                ((WorkoutDashboardActivity) requireActivity()).showHrDialogAlert(3, String.valueOf(parent.getCurrentLevel()));
        }

        if (hrStatusBean.getCurrentHR() < (hrStatusBean.getTargetHR() - 3)) {

            if ((parent.getCurrentLevel() + 1) > 17) return;

            parent.callUpdateLevel(1, false);

            Log.d(TAG, "升高:" + parent.getCurrentLevel());

            setHintText(R.string.hr_hint_not_high, String.valueOf(parent.getCurrentLevel()));

            if (!isHrShow || parent.isWorkoutInBackground)
                ((WorkoutDashboardActivity) requireActivity()).showHrDialogAlert(2, String.valueOf(parent.getCurrentLevel()));


        }
    }

    /**
     * 調整目標心跳
     *
     * @param isPlus 增加 / 減少
     */
    public void updateTargetHeartRate(boolean isPlus) {

        //  if (hrStatusBean.getMode() != MAINTAINING_MODE) return;
        int targetHR = Integer.parseInt(tvTargetHR.getText().toString());
        if (!isPlus && (targetHR <= 72)) return;
        if (isPlus && (targetHR >= 200)) return;

        if (!parent.isLongClickIng) { //不是long click
            getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
        } else {
            parent.isLongBeep = true;
        }

        tvTargetHR.setText(String.valueOf(isPlus ? ++targetHR : --targetHR));
        hrStatusBean.setTargetHR(targetHR);
    }

    private RxTimer showTextRxTimer = new RxTimer();

    private void setHintText(int hintT, String num) {

        if (!parent.isWorkoutTimerRunning) return;

        requireActivity().runOnUiThread(() -> {
            tvHrHint.setText(getString(hintT));
            tvHrNumber.setText(num);
        });

        showTextRxTimer.timer(6000,
                sec -> {
                    tvHrHint.setText("");
                    tvHrNumber.setText("");
                });


        //  tvHrHint.setText(R.string.hr_hint_high_finish);
    }

    boolean isHrShow;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHrShow = !hidden;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("JKLJKL", "onDetach: ");
    }
}