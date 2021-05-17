package com.dyaco.spiritbike.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;

import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class SettingUnitFragment extends BaseFragment {

    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();

    private TextView tvDistance;
    private TextView tvSpeed;
    private TextView tvHeight;
    private TextView tvWeight;
    private SegmentedButtonGroup segmentedButtonGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_unit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);
        initView(view);

        segmentedButtonGroup.setPosition(userProfileEntity.getUnit() == 1 ? 0 : 1, false);
    }

    private void initView(View view) {
        tvDistance = view.findViewById(R.id.unit_1_2);
        tvSpeed = view.findViewById(R.id.unit_2_2);
        tvHeight = view.findViewById(R.id.unit_3_2);
        tvWeight = view.findViewById(R.id.unit_4_2);

        segmentedButtonGroup = view.findViewById(R.id.sc_unit);
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    tvDistance.setText("mi");
                    tvSpeed.setText("mph");
                    tvHeight.setText("ft, in");
                    tvWeight.setText("lb");
                    updateData(1);
                    break;
                case 1:
                    tvDistance.setText("km");
                    tvSpeed.setText("km/h");
                    tvHeight.setText("cm");
                    tvWeight.setText("kg");
                    updateData(0);
            }
        });

        // segmentedButtonGroup.getPosition();
    }

    private void updateData(int position) {

        userProfileEntity.setUnit(position);
        UNIT_E = position;

        DatabaseManager.getInstance(MyApplication.getInstance()).
                updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);

                        Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                    }
                });
    }
}