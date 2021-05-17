package com.dyaco.spiritbike.settings;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class SettingSleepFragment extends BaseFragment {

    private UserProfileEntity userProfileEntity;

    private SegmentedButtonGroup segmentedButtonGroup;

    private TextView stext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_sleep, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);

        initView(view);

        userProfileEntity = getInstance().getUserProfile();
        //	0 --> off (會休眠)
        //	1 --> on (不休眠)
        segmentedButtonGroup.setPosition(userProfileEntity.getSleepMode() == 1 ? 0 : 1, false);
    }

    private void initView(View view) {

        stext = view.findViewById(R.id.stext);

        segmentedButtonGroup = view.findViewById(R.id.sc_unit);
        segmentedButtonGroup.setOnPositionChangedListener(position -> {

            //	0 --> off (會休眠)
            //	1 --> on (不休眠)
            switch (position) {
                case 0: //1 --> on (不休眠)
                    stext.setText(R.string.sleep_text_on);
//                    UserProfileEntity userProfileEntity = getInstance().getUserProfile();
//                    userProfileEntity.setSleepMode(0);
//                    getInstance().setUserProfile(userProfileEntity);

                    setTimeOut(2147483647);
                    updateData(1);
                    break;
                case 1: //0 --> off (會休眠)
                    stext.setText(R.string.sleep_text_off);
//                    UserProfileEntity userProfileEntity2 = getInstance().getUserProfile();
//                    userProfileEntity2.setSleepMode(1);
//                    getInstance().setUserProfile(userProfileEntity2);

                    setTimeOut(60 * 30 * 1000);
                    updateData(0);
            }

            //   updateData(position);
        });

    }

    private void updateData(int mode) {
        userProfileEntity.setSleepMode(mode);
        DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();

                        ((DashboardActivity) requireActivity()).startSleepTimer();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);

                        Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void setTimeOut(int time) {
        Settings.System.putInt(requireActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
          segmentedButtonGroup = null;
          stext = null ;
    }
}