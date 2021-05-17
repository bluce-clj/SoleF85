package com.dyaco.spiritbike.engineer;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.ON_INCLINE_CALI;
import static com.dyaco.spiritbike.MyApplication.ON_INCLINE_CALI_FAIL;
import static com.dyaco.spiritbike.MyApplication.ON_INCLINE_READ;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class FourFragment extends Fragment {
    private TextView level_ad;
    private TextView incline_ad;
    private TextView tv_max_ad_num;
    private TextView tv_mid_ad_num;
    private TextView tv_target_level_num;
    private TextView tv_current_ad_num;
    private Button btn_start;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_four, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_max_ad_num = view.findViewById(R.id.tv_max_ad_num);
        tv_mid_ad_num = view.findViewById(R.id.tv_mid_ad_num);

        tv_target_level_num = view.findViewById(R.id.tv_target_level_num);
        tv_current_ad_num = view.findViewById(R.id.tv_current_ad_num);

        level_ad = view.findViewById(R.id.level_ad);
        incline_ad = view.findViewById(R.id.incline_ad);

        btn_start = view.findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EngineerActivity) requireActivity()).loadingDialog.startDialog();
                getInstance().commandSetLwrMode(Device.LWR_MODE.INCLINE_CALI);

                new RxTimer().timer(10000, number -> {
                    try {
                        if (!isOK) {
                            ((EngineerActivity) requireActivity()).loadingDialog.stopDialog();
                            Toasty.error(getInstance(),"ERROR OCCURED!",Toasty.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


                //onLwrMode call commandSetInclineCalibration > onInclineCali call commandReadIncline > onInclineRead
                //commandSetLwrMode(Device.LWR_MODE.INCLINE_CALI); //改INCLINE_CALI校正模式
                //commandSetInclineCalibration(Device.INCLINE_CALI_STEP.START); //自動校正
                //commandReadIncline(); //校正完成讀取AD
            }
        });

        initEvent();

    }

    boolean isOK = false;
    private void initEvent() {
        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            if (msg.getType() == ON_INCLINE_CALI) {
                requireActivity().runOnUiThread(() -> {
                    isOK = true;
                    tv_current_ad_num.setText(String.valueOf(msg.getObj()));
                });
            }

            if (msg.getType() == ON_INCLINE_READ) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("ZZZZZZZZZ", "ON_INCLINE_READ:" + ((RxSettingBean) msg.getObj()).getInclineMinAd());
                    Log.d("ZZZZZZZZZ", "ON_INCLINE_READ:" + ((RxSettingBean) msg.getObj()).getInclineMaxAd());
                    tv_max_ad_num.setText(String.valueOf(((RxSettingBean) msg.getObj()).getInclineMaxAd()));
                    tv_mid_ad_num.setText(String.valueOf(((RxSettingBean) msg.getObj()).getInclineMinAd()));
                    tv_target_level_num.setText(((RxSettingBean) msg.getObj()).getStatus());

                    setAD();
                    ((EngineerActivity) requireActivity()).loadingDialog.stopDialog();
                });
            }

            if (msg.getType() == ON_INCLINE_CALI_FAIL) {
                requireActivity().runOnUiThread(() -> {
                    tv_max_ad_num.setText("");
                    tv_mid_ad_num.setText("");
                    Toasty.error(getInstance(),"FAIL",Toasty.LENGTH_LONG).show();

                    ((EngineerActivity) requireActivity()).loadingDialog.stopDialog();
                });
            }
        });
        compositeDisposable.add(d);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAD();

        Log.d("FFFFFFF", "onResume:4 ");
    }

    private void setAD() {
        String ld = "" ;
        String id = "";

        if (getInstance().getDeviceSettingBean().getDsLevelAd() != null) {
            ld = getInstance().getDeviceSettingBean().getDsLevelAd().replaceAll("#", ", ");
        }

        if (getInstance().getDeviceSettingBean().getDsInclineAd() != null) {
            id = getInstance().getDeviceSettingBean().getDsInclineAd().replaceAll("#", ", ");
        }

        level_ad.setText(String.format("LEVEL AD: %s", ld));
        incline_ad.setText(String.format("INCLINE AD: %s", id));

        Log.d("KKKKK", "setAD: " + id);
    }

    @Override
    public void onPause() {
        super.onPause();
       // compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

}
