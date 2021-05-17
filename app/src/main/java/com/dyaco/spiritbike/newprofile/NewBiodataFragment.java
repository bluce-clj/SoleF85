package com.dyaco.spiritbike.newprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.R;

import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MAX_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MAX_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MIN_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MIN_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MAX_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MAX_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MIN_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MIN_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class NewBiodataFragment extends Fragment implements RulerView.OnValueChangeListener {

    Group imperial_group;
    Group mm_group;
    // TextView height_unit_ft;
    TextView tv_right_show_num_ft;
    TextView tv_right_show_num_in;

    TextView tvWeightUnit;

    private TextView tvLeftShowNumber;
    private RulerView rulerViewLeft;

    private TextView tv_right_show_num_cm;
    private RulerView rulerViewRight;
    private String mUserName;
    private String mBirthDay;
    private int mGender;
    private SegmentedButtonGroup segmentedButtonGroup;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_biodata, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initRG(view);

    }

    private final View.OnClickListener btNewBiodataOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            int weight_metric;
            int height_metric;
            int weight_imperial;
            int height_imperial;

            if (segmentedButtonGroup.getPosition() == 0) {
                weight_imperial = Integer.parseInt(tvLeftShowNumber.getText().toString());
                height_imperial = Integer.parseInt(tv_right_show_num_cm.getText().toString());
                weight_metric = CommonUtils.lb2kg(weight_imperial);
                height_metric = CommonUtils.in2cm(height_imperial);
            } else {
                weight_metric = Integer.parseInt(tvLeftShowNumber.getText().toString());
                height_metric = Integer.parseInt(tv_right_show_num_cm.getText().toString());
                weight_imperial = CommonUtils.kg2lb(weight_metric);
                height_imperial = CommonUtils.cm2in(height_metric);
            }

            int unit = segmentedButtonGroup.getPosition() == 0 ? 1 : 0 ;

            bundle.putString("userName", mUserName);
            bundle.putString("birthDay", mBirthDay);
            bundle.putInt("gender", mGender);
            bundle.putInt("unit", unit);
            bundle.putInt("weight_metric", weight_metric);
            bundle.putInt("height_metric", height_metric);
            bundle.putInt("weight_imperial", weight_imperial);
            bundle.putInt("height_imperial", height_imperial);

            try {
                switch (view.getId()) {

                    case R.id.btBack_NewBiodata:
                        Navigation.findNavController(view).navigate(R.id.action_newBiodataFragment_to_newGenderFragment, bundle);
                        //  getActivity().onBackPressed();
                        break;
                    case R.id.btNext_NewBiodata:

                        bundle.putString("action", "action_newBiodataFragment_to_avatarIconFragment");
                        Navigation.findNavController(view).navigate(R.id.action_newBiodataFragment_to_avatarIconFragment, bundle);
                        break;
                    case R.id.btClose_NewBiodata:
                        Navigation.findNavController(view).navigate(R.id.action_newBiodataFragment_to_dialogDataLostFragment);

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initView(View view) {

        imperial_group = view.findViewById(R.id.imperial_group);
        mm_group = view.findViewById(R.id.mm_group);
        tv_right_show_num_ft = view.findViewById(R.id.tv_right_show_num_ft);
        //    height_unit_ft = view.findViewById(R.id.height_unit_ft);
        tv_right_show_num_in = view.findViewById(R.id.tv_right_show_num_in);
        tvWeightUnit = view.findViewById(R.id.weight_unit);

        Button btBack_NewBiodata = view.findViewById(R.id.btBack_NewBiodata);
        Button btNext_NewBiodata = view.findViewById(R.id.btNext_NewBiodata);
        Button btClose_NewBiodata = view.findViewById(R.id.btClose_NewBiodata);

        btBack_NewBiodata.setOnClickListener(btNewBiodataOnClick);
        btNext_NewBiodata.setOnClickListener(btNewBiodataOnClick);
        btClose_NewBiodata.setOnClickListener(btNewBiodataOnClick);

        tvLeftShowNumber = view.findViewById(R.id.tv_left_show_num);
        ImageButton btLeftPlus = view.findViewById(R.id.bt_left_plus);
        ImageButton btLeftMinus = view.findViewById(R.id.bt_left_minus);
        rulerViewLeft = view.findViewById(R.id.rulerViewLeft);
        rulerViewLeft.setOnValueChangeListener(this);

        tv_right_show_num_cm = view.findViewById(R.id.tv_right_show_num_cm);
        ImageButton btRightPlus = view.findViewById(R.id.bt_right_plus);
        ImageButton btRightMinus = view.findViewById(R.id.bt_right_minus);
        rulerViewRight = view.findViewById(R.id.rulerViewRight);
        rulerViewRight.setOnValueChangeListener(this);

        btLeftMinus.setOnClickListener(view1 -> rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) - 1));
        btLeftPlus.setOnClickListener(view12 -> rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) + 1));

        btRightMinus.setOnClickListener(view1 -> rulerViewRight.setSelectedValue(Float.parseFloat(tv_right_show_num_cm.getText().toString()) - 1));
        btRightPlus.setOnClickListener(view12 -> rulerViewRight.setSelectedValue(Float.parseFloat(tv_right_show_num_cm.getText().toString()) + 1));

        new CommonUtils().addAutoClick(btLeftPlus);
        new CommonUtils().addAutoClick(btLeftMinus);
        new CommonUtils().addAutoClick(btRightPlus);
        new CommonUtils().addAutoClick(btRightMinus);

    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.rulerViewLeft) {
            tvLeftShowNumber.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        } else {

            if (segmentedButtonGroup.getPosition() != 1) {
                tv_right_show_num_ft.setText(String.valueOf(CommonUtils.in2ft((int) value)));
                tv_right_show_num_in.setText(String.valueOf(CommonUtils.in2sin((int) value)));
            }
            tv_right_show_num_cm.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        }
    }

    private void initRG(View view) {

        segmentedButtonGroup = view.findViewById(R.id.sc_unit);
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            switch (position) {
                case 1:

                    //公制體重
                    int i = CommonUtils.lb2kg(Integer.parseInt(tvLeftShowNumber.getText().toString()));
                    tvWeightUnit.setText("kg");
                    rulerViewLeft.setMaxValue(METRIC_MAX_WEIGHT);
                    rulerViewLeft.setMinValue(METRIC_MIN_WEIGHT);
                    rulerViewLeft.setSelectedValue(i);

                    //公制身高
                    int i2 = CommonUtils.in2cm(Integer.parseInt(tv_right_show_num_cm.getText().toString()));
                    rulerViewRight.setMaxValue(METRIC_MAX_HEIGHT);
                    rulerViewRight.setMinValue(METRIC_MIN_HEIGHT);
                    rulerViewRight.setSelectedValue(i2);

                    imperial_group.setVisibility(View.INVISIBLE);
                    mm_group.setVisibility(View.VISIBLE);

                    break;
                case 0:

                    //英制體重
                    int i3 = CommonUtils.kg2lb(Integer.parseInt(tvLeftShowNumber.getText().toString()));
                    tvWeightUnit.setText("lb");
                    rulerViewLeft.setMaxValue(IMPERIAL_MAX_WEIGHT);
                    rulerViewLeft.setMinValue(IMPERIAL_MIN_WEIGHT);
                    rulerViewLeft.setSelectedValue(i3);

                    //英制身高
                    int i4 = CommonUtils.cm2in(Integer.parseInt(tv_right_show_num_cm.getText().toString()));
                    rulerViewRight.setMaxValue(IMPERIAL_MAX_HEIGHT);
                    rulerViewRight.setMinValue(IMPERIAL_MIN_HEIGHT);
                    rulerViewRight.setSelectedValue(i4);
                    imperial_group.setVisibility(View.VISIBLE);
                    mm_group.setVisibility(View.INVISIBLE);

            }
        });

        int position = getInstance().getDeviceSettingBean().getUnit_code() == 0 ? 1 : 0;
        segmentedButtonGroup.setPosition(position, false);

        if (position == 1) {
            rulerViewLeft.setSelectedValue(mWeightMetric);
        //    tvLeftShowNumber.setText("60");
            rulerViewRight.setSelectedValue(mHeightMetric);
          //  tv_right_show_num_cm.setText("170");
        } else {
            rulerViewLeft.setSelectedValue(mWeightImperial);
          //  tvLeftShowNumber.setText("200");
            rulerViewRight.setSelectedValue(mHeightImperial);
//            tv_right_show_num_ft.setText("5");
//            tv_right_show_num_in.setText("0");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}