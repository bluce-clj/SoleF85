package com.dyaco.spiritbike.workout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.R;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.dyaco.spiritbike.MyApplication.DEFAULT_SEEK_VALUE_LEVEL;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;
import static com.dyaco.spiritbike.support.CommonUtils.incF2I;
import static com.dyaco.spiritbike.support.ProgramsEnum.HEART_RATE;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;
import static com.dyaco.spiritbike.MyApplication.DEFAULT_SEEK_VALUE;


public class WorkoutDiagramFragment extends BaseFragment implements WorkoutDashboardActivity.OnInclinePlusListener, WorkoutDashboardActivity.OnLevelUpdateListener {

    private static final int INCLINE_RUNNING_01 = R.drawable.diagram_incline_running_01;
    private static final int INCLINE_RUNNING_02 = R.drawable.diagram_incline_running_02;
    private static final int INCLINE_FINISH_01 = R.drawable.diagram_incline_finish_01;
    private static final int INCLINE_FINISH_02 = R.drawable.diagram_incline_finish_02;
    private static final int INCLINE_PENDING_01 = R.drawable.diagram_incline_pending_01;
    private static final int INCLINE_PENDING_02 = R.drawable.diagram_incline_pending_02;

    private static final int LEVEL_RUNNING_01 = R.drawable.diagram_level_running_01;
    private static final int LEVEL_RUNNING_02 = R.drawable.diagram_level_running_02;
    private static final int LEVEL_FINISH_01 = R.drawable.diagram_level_finish_01;
    private static final int LEVEL_FINISH_02 = R.drawable.diagram_level_finish_02;
    private static final int LEVEL_PENDING_01 = R.drawable.diagram_level_pending_01;
    private static final int LEVEL_PENDING_02 = R.drawable.diagram_level_pending_02;

    private final int SEGMENT_PENDING = 0;
    private final int SEGMENT_RUNNING = 1;
    private final int SEGMENT_FINISH = 2;

    private final int DIAGRAM_TYPE_LEVEL = 0;
    private final int DIAGRAM_TYPE_INCLINE = 1;
    private final int DIAGRAM_TYPE_ALL = 2;

    public static int diagramSwitch;

    private View view;

    private int timeTick;
    private AlphaAnimation alphaAnimation;
   // int topLevel;
    WorkoutDashboardActivity parent;
    public int currentSegment = 0;

    public WorkoutDiagramFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (MODE == XE395ENT) {
            return inflater.inflate(R.layout.fragment_workout_diagram, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_workout_diagram_bike, container, false);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        parent = ((WorkoutDashboardActivity) mActivity);
        parent.setOnInclinePlusListener(this);
        parent.setOnLevelUpdateListener(this);

        initView();

        if (MODE == XE395ENT) {
            initSeekBar();
        } else {
            initLevelSeekBar();
        }

        //預設顯示的圖
        setAllSeekBarUpdate(true);
    }

    private void initView() {

        //切換圖
        diagramSwitch = DIAGRAM_TYPE_ALL;

        //Diagram更新時間
        int timeOut = parent.workoutBean.getTimeSecond();
        if (timeOut != 0) {
            timeTick = timeOut / 20;
        } else {
            timeTick = 60;
            //    timeTick = 1;
        }

        //初始化動畫
        alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.RESTART);
    }

    /**
     * 初始化Level SeekBar
     * BIKE 在用的
     */
    synchronized private void initLevelSeekBar() {

        String valueLevel = parent.workoutBean.getLevelDiagramNum() == null || "".equals(parent.workoutBean.getLevelDiagramNum()) ? DEFAULT_SEEK_VALUE_LEVEL : parent.workoutBean.getLevelDiagramNum();
        int[] arrayLevel = Arrays.stream(valueLevel.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
        for (int i = 0; i < 20; i++) {
            String seekBar02 = "sb_inc_pen_02_" + (i + 1);
            int resID02 = getResources().getIdentifier(seekBar02, "id", mActivity.getPackageName());
            //第一筆
            int status = i == 0 ? SEGMENT_RUNNING : SEGMENT_PENDING;
            view.findViewById(resID02).setEnabled(false);
            parent.workoutBean.getDiagramLevelList().add(new DiagramBean(view.findViewById(resID02), null, status, arrayLevel[i], arrayLevel[i], arrayLevel[i], arrayLevel[i]));
            parent.workoutBean.getDiagramHRList().add(new DiagramBean(0));
        }

        switch (parent.PROGRAM_TYPE) {
            case HILL:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case HIIT:
            case CUSTOM:
                //根據MaxLevel 初始化全部LEVEL
                int maxLevel = parent.LEVEL_MAX; //開始Program時 設定的 MAX LEVEL
                Arrays.sort(arrayLevel);
                // level diagram中 數值最高的一筆，custom可能低於5，因此最小值設5
                int currentMaxLevel = MathUtils.clamp(arrayLevel[arrayLevel.length - 1], 5, 20);
             //   topLevel = currentMaxLevel;
                parent.setMaxLevel(parent.LEVEL_MAX);

                for (int i = 0; i < parent.workoutBean.getDiagramLevelList().size(); i++) {
                    DiagramBean diagramBean = parent.workoutBean.getDiagramLevelList().get(i);
                    int currentLevel = diagramBean.getProgressLevel();
                    int newLevel = CommonUtils.calcCurrentLevel(maxLevel, currentLevel, currentMaxLevel);
                    diagramBean.setProgressLevel(newLevel);

                    //以最高的LEVEL調整SeekBar比例，單筆顯示的頁面需要動態切換時調整
                 //   diagramBean.getSeekBar01().setMax(topLevel);

                    parent.workoutBean.getDiagramLevelList().set(i, diagramBean);
                }
        }
    }

    /**
     * 初始化全部SeekBar
     * 橢圓機用的
     */
    synchronized private void initSeekBar() {

        String valueLevel = parent.workoutBean.getLevelDiagramNum() == null || "".equals(parent.workoutBean.getLevelDiagramNum()) ? DEFAULT_SEEK_VALUE_LEVEL : parent.workoutBean.getLevelDiagramNum();
        String valueIncline = parent.workoutBean.getInclineDiagramNum() == null || "".equals(parent.workoutBean.getInclineDiagramNum()) ? DEFAULT_SEEK_VALUE : parent.workoutBean.getInclineDiagramNum();

        int[] arrayLevel = Arrays.stream(valueLevel.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        int[] arrayIncline = Arrays.stream(valueIncline.split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        Log.d("VVVVV", "initSeekBar: " + valueIncline);

        //ALL
        for (int i = 0; i < 20; i++) {
            //   String seekBar02 = "sb_inc_pen_02_" + (i + 1);
            String seekBarLevel = "sb_all_level_" + (i + 1);
            String seekBarIncline = "sb_all_incline_" + (i + 1);
            //   int resID02 = getResources().getIdentifier(seekBar02, "id", mActivity.getPackageName());
            int resID1 = getResources().getIdentifier(seekBarLevel, "id", mActivity.getPackageName());
            int resID2 = getResources().getIdentifier(seekBarIncline, "id", mActivity.getPackageName());

            //第一筆
            int status = i == 0 ? SEGMENT_RUNNING : SEGMENT_PENDING;

            view.findViewById(resID1).setEnabled(false);
            view.findViewById(resID2).setEnabled(false);

            parent.workoutBean.getDiagramLevelList().add(new DiagramBean(view.findViewById(resID1), null, status, arrayLevel[i], arrayLevel[i], arrayIncline[i], arrayIncline[i]));

            parent.workoutBean.getDiagramInclineList().add(new DiagramBean(view.findViewById(resID2), null, status, arrayLevel[i], arrayLevel[i], arrayIncline[i], arrayIncline[i]));

            parent.workoutBean.getDiagramHRList().add(new DiagramBean(0));
        }

        switch (parent.PROGRAM_TYPE) {
//            case MANUAL:
//                parent.setMaxIncline(parent.workoutBean.getOrgMaxIncline());
//                break;
            case HILL:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case HIIT:
            case CUSTOM:
                //開始Program時 設定的 MAX LEVEL
                int maxLevel = parent.LEVEL_MAX;

                //設定當前MaxLevel
                parent.setMaxLevel(maxLevel);

                // level diagram中 數值最高的一筆，custom可能低於5，因此最小值設5
                Arrays.sort(arrayLevel);
                int currentMaxLevel = MathUtils.clamp(arrayLevel[arrayLevel.length - 1], 5, 20);
              //  topLevel = currentMaxLevel;

                //根據MaxLevel 初始化全部LEVEL
                for (int i = 0; i < parent.workoutBean.getDiagramLevelList().size(); i++) {

                    DiagramBean diagramBean = parent.workoutBean.getDiagramLevelList().get(i);

                    //用公式設定新Level
                    int newLevel = CommonUtils.calcCurrentLevel(maxLevel, diagramBean.getProgressLevel(), currentMaxLevel);
                    diagramBean.setProgressLevel(newLevel);

                    //以最高的LEVEL調整SeekBar比例，單筆顯示的頁面需要動態切換時調整
                 //   diagramBean.getSeekBar01().setMax(topLevel);

                    parent.workoutBean.getDiagramLevelList().set(i, diagramBean);


                    /**Incline
                     * ##############
                     */
                    int maxIncline = parent.INCLINE_MAX;

                    // level diagram中 數值最高的一筆，custom可能低於5，因此最小值設5
                    Arrays.sort(arrayIncline);
                    int currentMaxIncline = MathUtils.clamp(arrayIncline[arrayIncline.length - 1], 5, 41);
                    DiagramBean diagramInclineBean = parent.workoutBean.getDiagramInclineList().get(i);
                    //用公式設定新Level
                //    int newIncline = CommonUtils.calcCurrentLevel(maxIncline, diagramInclineBean.getProgressIncline(), currentMaxIncline);
                //    diagramInclineBean.setProgressIncline(newIncline);

                    diagramInclineBean.setProgressIncline(diagramInclineBean.getProgressInclineOrigin());

                    //以最高的LEVEL調整SeekBar比例，單筆顯示的頁面需要動態切換時調整
                    //   diagramInclineBean.getSeekBar01().setMax(topLevel);
                    parent.workoutBean.getDiagramInclineList().set(i, diagramInclineBean);

                    /**
                     * ##############
                     */


                    Log.d("JJJJJJJ", "initSeekBar: " + diagramInclineBean.getProgressIncline() + "," + diagramInclineBean.getProgressInclineOrigin());
                }
        }
    }

    /**
     * 啟動閃爍動畫
     *
     * @param view SeekBar
     */
    synchronized private void setAlphaAnimation(View view) {

        if (view.getAnimation() == null) {
            view.startAnimation(alphaAnimation);
        }
    }

    /**
     * 更新SeekBar的 ProgressDrawable
     *
     * @param seekBar          SeekBar
     * @param progressDrawable Style
     */
    synchronized private void setProgressDrawable(SeekBar seekBar, int progressDrawable) {

        seekBar.setProgressDrawable(ContextCompat.getDrawable(mActivity, progressDrawable));
    }

    /**
     * 更新 Level Incline Diagram一起顯示的SeekBar
     */
    synchronized private void setAllSeekBarUpdate(boolean isFlow) {

        for (int i = 0; i < 20; i++) {

            DiagramBean diagramBeanIncline = null;

            DiagramBean diagramBeanLevel = parent.workoutBean.getDiagramLevelList().get(i);

            if (MODE == XE395ENT) {
                diagramBeanIncline = parent.workoutBean.getDiagramInclineList().get(i);
                diagramBeanIncline.getSeekBar01().setProgress(diagramBeanIncline.getProgressIncline());
            }

//            if (parent.PROGRAM_TYPE == MANUAL) {
//                //變更 MANUAL Level Diagram
//                diagramBeanLevel.getSeekBar01().setProgress(diagramBeanLevel.getProgressLevel());
//            } else {
//                //MANUAL 以外的 level Diagram 不變動，取原始值
//                diagramBeanLevel.getSeekBar01().setProgress(diagramBeanLevel.getProgressLevelOrigin());
//            }

            diagramBeanLevel.getSeekBar01().setProgress(diagramBeanLevel.getProgressLevel());

            //所有Program Incline的值被調整後，Incline Diagram圖也變動
          //  if (MODE == XE395ENT) {

              //  if (parent.PROGRAM_TYPE == MANUAL) {
                    //變更 MANUAL Incline Diagram
                //    diagramBeanIncline.getSeekBar01().setProgress(diagramBeanIncline.getProgressIncline());
              //  } else {
                    //MANUAL 以外的 Incline Diagram 不變動，取原始值
               //     diagramBeanIncline.getSeekBar01().setProgress(diagramBeanIncline.getProgressInclineOrigin());
              //  }

                //    diagramBeanIncline.getSeekBar01().setProgress(diagramBeanIncline.getProgressIncline());
        //    }

            int statusStyleLevel = 0;
            int statusStyleIncline = 0;
            switch (diagramBeanLevel.getStatus()) {
                case SEGMENT_PENDING:
                    statusStyleLevel = MODE == XE395ENT ? LEVEL_PENDING_01 : LEVEL_PENDING_02;
                    statusStyleIncline = INCLINE_PENDING_01;

                    if (MODE == XE395ENT) {
                        assert diagramBeanIncline != null;
                        if (diagramBeanIncline.getSeekBar01().getAnimation() != null)
                            diagramBeanIncline.getSeekBar01().clearAnimation();
                    }

                    if (diagramBeanLevel.getSeekBar01().getAnimation() != null)
                        diagramBeanLevel.getSeekBar01().clearAnimation();

                    break;
                case SEGMENT_RUNNING:
                    statusStyleLevel = MODE == XE395ENT ? LEVEL_RUNNING_01 : LEVEL_RUNNING_02;
                    statusStyleIncline = INCLINE_RUNNING_01;

                    setAlphaAnimation(diagramBeanLevel.getSeekBar01());
                    if (MODE == XE395ENT) {
                        assert diagramBeanIncline != null;
                        setAlphaAnimation(diagramBeanIncline.getSeekBar01());
                    }

                    //把WorkoutDashboard顯示的Level及Incline 更新為當前Segment的值

                    if (parent.PROGRAM_TYPE != HEART_RATE) { //heart rate 不自動更新
                        if (isFlow) {
                            parent.setCurrentLevel(diagramBeanLevel.getProgressLevel(), false);
                        }
                    }

                    if (MODE == XE395ENT) {
                        if (isFlow) {
                            assert diagramBeanIncline != null;
                            parent.setCurrentIncline(diagramBeanIncline.getProgressIncline(), false);
                        }
                    }

                    break;
                case SEGMENT_FINISH:
                    statusStyleLevel = MODE == XE395ENT ? LEVEL_FINISH_01 : LEVEL_FINISH_02;
                    statusStyleIncline = INCLINE_FINISH_01;

                    if (diagramBeanLevel.getSeekBar01().getAnimation() != null) {
                        diagramBeanLevel.getSeekBar01().clearAnimation();
                    }

                    if (MODE == XE395ENT) {
                        assert diagramBeanIncline != null;
                        if (diagramBeanIncline.getSeekBar01().getAnimation() != null)
                            diagramBeanIncline.getSeekBar01().clearAnimation();
                    }

                    break;
                default:
            }

            setProgressDrawable(diagramBeanLevel.getSeekBar01(), statusStyleLevel);

            if (MODE == XE395ENT) {
                assert diagramBeanIncline != null;
                setProgressDrawable(diagramBeanIncline.getSeekBar01(), statusStyleIncline);
            }
        }
    }

    /**
     * 從WorkoutDashboard 點選 Incline 加或減
     *
     * @param updateInclineNum 增減的數值
     */
    @Override
    public void onInclineUpdate(int updateInclineNum, boolean beep) {

        if (parent.inclineError) return;

        //切換成Incline頁面
        diagramSwitch = DIAGRAM_TYPE_LEVEL;

        int currentIncline = parent.getCurrentIncline();
        int newCurrentIncline = currentIncline + (updateInclineNum);

        Log.d("KKKKKKK", "currentIncline: " + currentIncline +","+ newCurrentIncline);
        float ccinc = newCurrentIncline == 0 ? 0.5f : newCurrentIncline;

        boolean isManual = false;
        switch (parent.PROGRAM_TYPE) {
            case MANUAL:
            case HEART_RATE:
                isManual = true;

                //ftms 傳同階回成功
                if (currentIncline == newCurrentIncline) {
                    parent.responseFtmsUpdateIncline(true);
                    return;
                }
                boolean checkValue = newCurrentIncline <= 40 && newCurrentIncline >= 0;
                if (checkValue) {
                    parent.setCurrentIncline(newCurrentIncline, beep);
                    updateIncline(updateInclineNum, isManual, 0, 0);
                } else {
                    parent.responseFtmsUpdateIncline(false);
                    return;
                }
              //  setAllSeekBarUpdate(false);
                break;
            case HILL:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case HIIT:
            case CUSTOM:

                //ftms 傳同階回成功
                if (currentIncline == newCurrentIncline) {
                    parent.responseFtmsUpdateIncline(true);
                    return;
                }

                //加減後的新 incline 大於40 小於0 跳出
                if (newCurrentIncline > 40 || newCurrentIncline < 0) {
                    parent.responseFtmsUpdateIncline(false);
                    return;
                }

                //按 +，MaxIncline 不可超過20
                if (parent.INCLINE_MAX >= 40 && updateInclineNum >= 0) {
                    parent.responseFtmsUpdateIncline(false);
                    return;
                }

             //   Log.d("KKKKKKK", "org: " + parent.workoutBean.getOrgMaxIncline() + ",current" + parent.INCLINE_MAX);
                //按 -，newMaxLevel 不可低於 orgMaxLevel
//                if (parent.INCLINE_MAX <= parent.workoutBean.getOrgMaxIncline() && updateInclineNum < 0) {
//                    parent.responseFtmsUpdateIncline(false);
//                    return;
//                }

                for (int i = 0; i < parent.workoutBean.getDiagramInclineList().size(); i++) {
                    if (parent.workoutBean.getDiagramInclineList().get(i).getStatus() == SEGMENT_RUNNING)
                        currentSegment = i;
                }

                //算出新的MaxIncline  , 原始Incline, 更新後的 Incline , 當前顯示的 max Incline
             //   int newMaxIncline = CommonUtils.getNewMaxLevel(parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin(), ccinc, parent.workoutBean.getOrgMaxIncline());
              //  int newMaxIncline = CommonUtils.getNewMaxIncline(parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin(), ccinc, parent.workoutBean.getOrgMaxIncline());

                float orgIncline = parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin() == 0 ? 0.5f :parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin();
                int newMaxIncline = CommonUtils.getNewMaxIncline(orgIncline, ccinc, parent.workoutBean.getOrgMaxIncline());
             //   Log.d("KKKKKKK", "onInclineUpdate: " + parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin() +","+ ccinc +","+parent.workoutBean.getOrgMaxIncline());
             //   Log.d("KKKKKKK", "onInclineUpdate: " + orgIncline +","+ ccinc +","+parent.workoutBean.getOrgMaxIncline());
              //  Log.d("KKKKKKK", "newMaxIncline: " + newMaxIncline);

                // 算出來的 newMaxLevel 不可 超過20 或低於 orgMaxLevel
                if (newMaxIncline > 40) {
                    parent.responseFtmsUpdateIncline(false);
                    return;
                }

              //  Log.d("KKKKKKK", "getOrgMaxIncline(): " + parent.workoutBean.getOrgMaxIncline());

                for (int i = 0; i < parent.workoutBean.getDiagramInclineList().size(); i++) {
                    DiagramBean diagramBean = parent.workoutBean.getDiagramInclineList().get(i);
                    if (diagramBean.getStatus() == SEGMENT_RUNNING || diagramBean.getStatus() == SEGMENT_PENDING) {

                        //   int newPLevel = CommonUtils.calcCurrentLevel(newMaxIncline, diagramBean.getProgressInclineOrigin(), parent.workoutBean.getOrgMaxIncline());

                       // int newPLevel = CommonUtils.calcCurrentLevel(diagramBean.getProgressInclineOrigin(), ccinc, parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin());
                        int newPLevel = CommonUtils.calcCurrentLevel(diagramBean.getProgressInclineOrigin() == 0 ? 0.5f : diagramBean.getProgressInclineOrigin(), ccinc, orgIncline);

                        Log.d("KKKKKKK", "calcCurrentLevel: " +i +":"+ newPLevel);
                        //檢查算出來的newCurrentLevel，若有一根小於1或大於20，就調整失敗
                        if (newPLevel < 0 || newPLevel > 40) {
                            parent.responseFtmsUpdateIncline(false);
                            return;
                        }
                    }
                }

                //更新WorkoutDashboard的MaxLevel及CurrentLevel
                parent.setCurrentIncline(newCurrentIncline, beep);
                //更新Diagram Level
                updateIncline(updateInclineNum, isManual, newMaxIncline, ccinc);

                parent.INCLINE_MAX = newMaxIncline;
                parent.setMaxIncline(parent.INCLINE_MAX);
        }

//        //ftms 傳同階回成功
//        Log.d("FTMSSS", "onInclineUpdate: " + currentIncline +","+ newCurrentIncline);
//        if (currentIncline == newCurrentIncline) {
//            parent.responseFtmsUpdateIncline(true);
//            return;
//        }
//
//        boolean checkValue = newCurrentIncline <= parent.INCLINE_MAX && newCurrentIncline >= 1;
//
//        if (checkValue) {
//            parent.setCurrentIncline(newCurrentIncline, true);
//            updateIncline(updateInclineNum, isManual);
//        } else {
//            parent.responseFtmsUpdateIncline(false);
//            return;
//        }
//


          setAllSeekBarUpdate(false);

    }

    /**
     * 從WorkoutDashboard 點選 Level 加或減
     *
     * @param updateLevelNum 是否
     */
    @Override
    public void onLevelUpdate(int updateLevelNum, boolean beep) {

        if (parent.levelError) return;
        //切換成Level頁面
        diagramSwitch = DIAGRAM_TYPE_ALL;

        int currentLevel = parent.getCurrentLevel();
        int newLevel = currentLevel + (updateLevelNum);

        switch (parent.PROGRAM_TYPE) {

            case HEART_RATE:
            case MANUAL:

                Log.d("AAAAAA", "currentLevel: " + currentLevel + "," + newLevel);
                //ftms 傳同階回成功
                if (currentLevel == newLevel) {
                    parent.responseFtmsUpdateLevel(true);
                    return;
                }

                boolean checkValue = newLevel <= 20 && newLevel >= 1;
                if (checkValue) {
                    parent.setCurrentLevel(newLevel, beep);
                    updateLevel(updateLevelNum, true, 0, 0);
                } else {
                    parent.responseFtmsUpdateLevel(false);
                    return;
                }
              //  setAllSeekBarUpdate(false);
                break;
            case HILL:
            case FATBURN:
            case CARDIO:
            case STRENGTH:
            case HIIT:
            case CUSTOM:

                Log.d("AAAAAA", "currentLevel: " + currentLevel + "," + newLevel);
                //ftms 傳同階回成功
                if (currentLevel == newLevel) {
                    parent.responseFtmsUpdateLevel(true);
                    return;
                }

                //加減後的新 Level 大於20 小於1 跳出
                if (newLevel > 20 || newLevel < 1) {
                    parent.responseFtmsUpdateLevel(false);
                    return;
                }
                Log.d("AAAAAA", "update後的 level newlevel: " + newLevel);

                //current level 增加時，MaxLevel 不可超過20
                if (parent.LEVEL_MAX >= 20 && updateLevelNum > 0) {
                    parent.responseFtmsUpdateLevel(false);
                    return;
                }
//                //current level 減少時，newMaxLevel 不可低於 orgMaxLevel
//                if (parent.LEVEL_MAX <= parent.workoutBean.getOrgMaxLevel() && updateLevelNum < 0) {
//                    parent.responseFtmsUpdateLevel(false);
//                    return;
//                }

                for (int i = 0; i < parent.workoutBean.getDiagramLevelList().size(); i++) {
                    if (parent.workoutBean.getDiagramLevelList().get(i).getStatus() == SEGMENT_RUNNING)
                        currentSegment = i;
                }

                Log.d("AAAAAA", "原始current level:" + parent.workoutBean.getDiagramLevelList().get(currentSegment).getProgressLevelOrigin() + ",新current level" + currentLevel);
                //算出新的MaxLevel  , 原始level, 更新後的 level , 當前顯示的 max level
                //  int newMaxLevel = CommonUtils.getNewMaxLevel(currentLevel, newLevel, parent.LEVEL_MAX);
                int newMaxLevel = CommonUtils.getNewMaxLevel(parent.workoutBean.getDiagramLevelList().get(currentSegment).getProgressLevelOrigin(), newLevel, parent.workoutBean.getOrgMaxLevel());
                Log.d("AAAAAA", "當前currentSegment:" + currentSegment);

                // 算出來的 newMaxLevel 不可 超過20 或低於 orgMaxLevel
                if (newMaxLevel > 20) {
                    // if (newMaxLevel > 20 || newMaxLevel < parent.LEVEL_MAX) {
                    parent.responseFtmsUpdateLevel(false);
                    return;
                }
                //   Log.d("AAAAAA", "newMaxLevel: " + newMaxLevel + ", 原始設定的Max level" + parent.workoutBean.getOrgMaxLevel() + ",當前的max level:" + parent.LEVEL_MAX);

                for (int i = 0; i < parent.workoutBean.getDiagramLevelList().size(); i++) {
                    DiagramBean diagramBean = parent.workoutBean.getDiagramLevelList().get(i);
                    if (diagramBean.getStatus() == SEGMENT_RUNNING || diagramBean.getStatus() == SEGMENT_PENDING) {

                        //  int newPLevel = CommonUtils.calcCurrentLevel(newMaxLevel, diagramBean.getProgressLevel(), parent.LEVEL_MAX);
                        //  int newPLevel = CommonUtils.calcCurrentLevel(newMaxLevel, diagramBean.getProgressLevelOrigin(), parent.workoutBean.getOrgMaxLevel());
                        int newPLevel = CommonUtils.calcCurrentLevel(diagramBean.getProgressLevelOrigin(), newLevel, parent.workoutBean.getDiagramLevelList().get(currentSegment).getProgressLevelOrigin());
                        //檢查算出來的newCurrentLevel，若有一根小於1或大於20，就調整失敗
                        Log.d("AAAAAA", "@" + i + ":算出的新Level: " + newPLevel);

//                        if (newPLevel < 1 || newPLevel > 20) {
//                            parent.responseFtmsUpdateLevel(false);
//                            //    Log.d("AAAAAA", "失敗: " + newMaxLevel);
//                            return;
//                        }

                        if (newPLevel > 20) {
                            parent.responseFtmsUpdateLevel(false);
                            //    Log.d("AAAAAA", "失敗: " + newMaxLevel);
                            return;
                        }
                    }
                }

                //更新WorkoutDashboard的MaxLevel及CurrentLevel
                parent.setCurrentLevel(newLevel, beep);
                //更新Diagram Level
                //  updateLevel(updateLevelNum, false, parent.LEVEL_MAX, newMaxLevel);
                updateLevel(updateLevelNum, false, parent.LEVEL_MAX, newLevel);

                parent.LEVEL_MAX = newMaxLevel;
                parent.setMaxLevel(parent.LEVEL_MAX);

                break;
        }

        setAllSeekBarUpdate(false);
    }

    /**
     * 更新Level值
     *
     * @param updateLevelNum  是否為
     * @param isManual        是否為MANUAL Program
     * @param currentMaxLevel 當前MaxLevel (manual用不到)
     * @param newLevel        新MaxLevel (manual用不到)
     */
    synchronized private void updateLevel(int updateLevelNum, boolean isManual, int currentMaxLevel, int newLevel) {

        if (parent.workoutBean.getDiagramLevelList() == null) return;

        if (isManual) {
            //MANUAL Level 當前及之後Segment的CurrentLevel 變一樣
            for (int i = 0; i < parent.workoutBean.getDiagramLevelList().size(); i++) {
                DiagramBean diagramBean = parent.workoutBean.getDiagramLevelList().get(i);
                if (diagramBean.getStatus() == SEGMENT_RUNNING || diagramBean.getStatus() == SEGMENT_PENDING) {
                    diagramBean.setProgressLevel(diagramBean.getProgressLevel() + (updateLevelNum));
                    parent.workoutBean.getDiagramLevelList().set(i, diagramBean);
                }
            }
        } else { //five program

            //更新當前及之後的LEVEL
            for (int i = 0; i < parent.workoutBean.getDiagramLevelList().size(); i++) {
                DiagramBean diagramBean = parent.workoutBean.getDiagramLevelList().get(i);
                if (diagramBean.getStatus() == SEGMENT_RUNNING || diagramBean.getStatus() == SEGMENT_PENDING) {
                    //  int newLevel = CommonUtils.calcCurrentLevel(newMaxLevel, diagramBean.getProgressLevel(), currentMaxLevel);
                    // int newLevel = CommonUtils.calcCurrentLevel(newMaxLevel, diagramBean.getProgressLevelOrigin(), parent.workoutBean.getOrgMaxLevel());
                    int newPLevel = CommonUtils.calcCurrentLevel(diagramBean.getProgressLevelOrigin(), newLevel, parent.workoutBean.getDiagramLevelList().get(currentSegment).getProgressLevelOrigin());

                    diagramBean.setProgressLevel(Math.max(newPLevel, 1));
                    Log.d("WWWWWW", "updateLevel: " + diagramBean.getProgressLevelOrigin() + "," + newLevel + "," + parent.workoutBean.getDiagramLevelList().get(currentSegment).getProgressLevelOrigin() + "," + newPLevel);
                    //  Log.d("WWWWWW", "onLevelUpdate: "+i+"," + newPLevel);
                    parent.workoutBean.getDiagramLevelList().set(i, diagramBean);

                    //  Log.d("HHHHHH", "@" + i + "@" + "newMaxLevel: " + newMaxLevel + ", CurrentLevel:" + diagramBean.getProgressLevel() + ",NewCurrentLevel:" + newLevel + ",currentMaxLevel:" + currentMaxLevel);
                }
            }
        }


    }

    /**
     * 更新Incline的值
     *
     * @param updateInclineNum 是
     * @param isManual         是否為MANUAL Program
     */
    synchronized private void updateIncline(int updateInclineNum, boolean isManual, int newMaxIncline, float ccinc) {

        if (isManual) {
            //MANUAL 當前及之後的值 變一樣
            for (int i = 0; i < 20; i++) {
                DiagramBean diagramBean = parent.workoutBean.getDiagramInclineList().get(i);
                if (diagramBean.getStatus() == SEGMENT_RUNNING || diagramBean.getStatus() == SEGMENT_PENDING) {
                    diagramBean.setProgressIncline(diagramBean.getProgressIncline() + (updateInclineNum));
                    parent.workoutBean.getDiagramInclineList().set(i, diagramBean);
                }
            }
        } else {
//            //只改變當前的值
//            for (int i = 0; i < 20; i++) {
//                DiagramBean diagramBean = parent.workoutBean.getDiagramInclineList().get(i);
//                if (diagramBean.getStatus() == SEGMENT_RUNNING) {
//                    int progress = diagramBean.getProgressIncline() + (updateInclineNum);
//                    diagramBean.setProgressIncline(progress);
//                    parent.workoutBean.getDiagramInclineList().set(i, diagramBean);
//                    break;
//                }
//            }

            //更新當前及之後的LEVEL
            for (int i = 0; i < parent.workoutBean.getDiagramInclineList().size(); i++) {
                DiagramBean diagramBean = parent.workoutBean.getDiagramInclineList().get(i);
                if (diagramBean.getStatus() == SEGMENT_RUNNING || diagramBean.getStatus() == SEGMENT_PENDING) {
                    //  int newIncline = CommonUtils.calcCurrentLevel(newMaxIncline, diagramBean.getProgressInclineOrigin(), parent.workoutBean.getOrgMaxIncline());
                  //  int newPIncline = CommonUtils.calcCurrentLevel(diagramBean.getProgressInclineOrigin(), ccinc, parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin());
                    int newPIncline = CommonUtils.calcCurrentLevel(diagramBean.getProgressInclineOrigin() == 0 ? 0.5f : diagramBean.getProgressInclineOrigin(), ccinc, parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin()  == 0 ? 0.5f : parent.workoutBean.getDiagramInclineList().get(currentSegment).getProgressInclineOrigin());

                    diagramBean.setProgressIncline(newPIncline);
                    parent.workoutBean.getDiagramInclineList().set(i, diagramBean);

                    //  Log.d("HHHHHH", "@" + i + "@" + "newMaxLevel: " + newMaxLevel + ", CurrentLevel:" + diagramBean.getProgressLevel() + ",NewCurrentLevel:" + newLevel + ",currentMaxLevel:" + currentMaxLevel);
                }
            }
        }
    }

    /**
     * Segment隨時間變化
     */
    synchronized public void segmentFlow(long milliSeconds) {
        mActivity.runOnUiThread(() -> {
            long sec = TimeUnit.MILLISECONDS.toSeconds(milliSeconds);

            //  if (parent.workoutBean.getTimeSecond() == sec) return; //跳過倒數的第一筆時間

            if (sec % timeTick == 0) {
                //  switchDiagram();
                for (int i = 0; i < 20; i++) {

                    //   if (parent.workoutBean.getDiagramInclineList().get(i).getStatus() == SEGMENT_RUNNING) {
                    if (parent.workoutBean.getDiagramLevelList().get(i).getStatus() == SEGMENT_RUNNING) {

                        //存hr
                        parent.workoutBean.getDiagramHRList().get(i).setHr(parent.mCurrentHR);

                        //把當前狀態是Running的Segment更新為Finish
                        if (MODE == XE395ENT)
                            parent.workoutBean.getDiagramInclineList().get(i).setStatus(SEGMENT_FINISH);

                        parent.workoutBean.getDiagramLevelList().get(i).setStatus(SEGMENT_FINISH);

                        //更新下一個Segment的狀態，除了最後一筆
                        if (i != 19) {
                            if (MODE == XE395ENT)
                                parent.workoutBean.getDiagramInclineList().get(i + 1).setStatus(SEGMENT_RUNNING);

                            parent.workoutBean.getDiagramLevelList().get(i + 1).setStatus(SEGMENT_RUNNING);

                            //當前Segment
                            currentSegment = i + 1;

                        } else {
                            //正數的跑到最後的Segment，重新開始
                            if (parent.workoutBean.getTimeSecond() == 0) repeatDiagram();
                        }

                        setAllSeekBarUpdate(true);
                        //只更新第一筆
                        return;
                    }
                }
            }
        });
    }

    public int reCount = 1;
    /**
     * Diagram重頭開始
     */
    synchronized private void repeatDiagram() {

       // int size = parent.workoutBean.getDiagramInclineList().size();
        int size = 20;

        //MANUAL - 全部跟最後一個Segment一樣
        if (parent.PROGRAM_TYPE == MANUAL) {
            int lastLevelProgress = parent.workoutBean.getDiagramLevelList().get(size - 1).getProgressLevel();
            int lastInclineProgress = 0;
            if (MODE == XE395ENT) {
                lastInclineProgress = parent.workoutBean.getDiagramInclineList().get(size - 1).getProgressIncline();
            }

            for (int i = 0; i < size; i++) {
                DiagramBean diagramBeanLevel = parent.workoutBean.getDiagramLevelList().get(i);

                if (MODE == XE395ENT) {
                    DiagramBean diagramBeanIncline = parent.workoutBean.getDiagramInclineList().get(i);
                    diagramBeanIncline.setStatus(i == 0 ? SEGMENT_RUNNING : SEGMENT_PENDING);
                    diagramBeanIncline.setProgressIncline(lastInclineProgress);
                    parent.workoutBean.getDiagramInclineList().set(i, diagramBeanIncline);
                }

                diagramBeanLevel.setStatus(i == 0 ? SEGMENT_RUNNING : SEGMENT_PENDING);
                diagramBeanLevel.setProgressLevel(lastLevelProgress);
                parent.workoutBean.getDiagramLevelList().set(i, diagramBeanLevel);
            }

        } else {

            for (int i = 0; i < size; i++) {
                DiagramBean diagramBeanLevel = parent.workoutBean.getDiagramLevelList().get(i);
                if (MODE == XE395ENT) {
                    DiagramBean diagramBeanIncline = parent.workoutBean.getDiagramInclineList().get(i);
                    diagramBeanIncline.setStatus(i == 0 ? SEGMENT_RUNNING : SEGMENT_PENDING);
                    parent.workoutBean.getDiagramInclineList().set(i, diagramBeanIncline);
                }
                diagramBeanLevel.setStatus(i == 0 ? SEGMENT_RUNNING : SEGMENT_PENDING);
                parent.workoutBean.getDiagramLevelList().set(i, diagramBeanLevel);
            }
        }

        reCount += 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //   if (countDownTimer != null) countDownTimer.cancel();

        view = null;
        alphaAnimation = null;
        parent = null;
    }


//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (hidden) {
//        } else {
//        }
//    }
}