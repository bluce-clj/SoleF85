package com.dyaco.spiritbike.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.room.UserProfileEntity;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class SettingChildLockFragment extends BaseFragment {

    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    public SegmentedButtonGroup segmentedButtonGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_child_lock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);

        initView(view);

        segmentedButtonGroup.setPosition(getInstance().getDeviceSettingBean().getChild_lock() == 1 ? 0 : 1, false);
    }

    boolean isFirst = true;
    private void initView(View view) {

        segmentedButtonGroup = view.findViewById(R.id.sc_unit);
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setChild_lock(1);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
                    if (!isFirst) {
                        try {
                            ((DashboardActivity) requireActivity()).showDialogAlert();
                            requireActivity().onBackPressed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        isFirst = false;
                    }

                    break;
                case 1:
                    DeviceSettingBean deviceSettingBean2 = getInstance().getDeviceSettingBean();
                    deviceSettingBean2.setChild_lock(0);
                    getInstance().setDeviceSettingBean(deviceSettingBean2);
                    isFirst = false;
            }
        });
    }
}