package com.dyaco.spiritbike.newprofile;

//import android.Manifest;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.text.InputType;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
//import com.dyaco.spiritbike.R;
//import com.dyaco.spiritbike.settings.WifiAdapter;
//import com.dyaco.spiritbike.support.BaseFragment;
//import com.hacknife.wifimanager.IWifi;
//import com.hacknife.wifimanager.IWifiManager;
//import com.hacknife.wifimanager.OnWifiChangeListener;
//import com.hacknife.wifimanager.OnWifiConnectListener;
//import com.hacknife.wifimanager.OnWifiStateChangeListener;
//import com.hacknife.wifimanager.State;
//import com.hacknife.wifimanager.WifiManager;
//import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.List;

//import io.reactivex.rxjava3.disposables.Disposable;

public class FirstLaunchWifiWidgetFragment {}

//public class FirstLaunchWifiWidgetFragment extends BaseFragment implements OnWifiChangeListener, OnWifiConnectListener, OnWifiStateChangeListener {
//    private RecyclerView wifiList;
//    private WifiAdapter wifiAdapter;
//    private ImageView ivReload;
//    private IWifiManager manager;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_first_launch_wifi_widget, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Button btGotoFirstLaunchSetDate = view.findViewById(R.id.btGotoFirstLaunchSetDate);
//        btGotoFirstLaunchSetDate.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_firstLaunchWifiWidgetFragment_to_firstLaunchSetDateFragment));
//
//        final RxPermissions rxPermissions = new RxPermissions(this);
//
//        ivReload = view.findViewById(R.id.reLoad);
//        ivReload.setOnClickListener(view1 -> {
//
//            if (manager != null) {
//                manager.destroy();
//                manager.setOnWifiChangeListener(null);
//                manager.setOnWifiConnectListener(null);
//                manager.setOnWifiStateChangeListener(null);
//            }
//
//            manager = WifiManager.create(mActivity);
//            manager.setOnWifiChangeListener(this);
//            manager.setOnWifiConnectListener(this);
//            manager.setOnWifiStateChangeListener(this);
//            manager.openWifi();
//            manager.scanWifi();
//        });
//
//        wifiList = view.findViewById(R.id.wifiList);
//        wifiAdapter = new WifiAdapter(mActivity);
//
//        wifiList.setLayoutManager(new LinearLayoutManager(mActivity));
//        wifiList.setHasFixedSize(true);
//
//        wifiList.setAdapter(wifiAdapter);
//
//        wifiAdapter.setOnItemClickListener(bean -> {
//
//            Log.i("WIFI", "onItemClick: " + bean);
//
//            if (bean.isConnected()) //已連線
//                return;
//
//
//            if (bean.isSaved() ) { //已保存密碼
//                manager.connectSavedWifi(bean);
//            } else if (bean.isEncrypt()) { //加密
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//                builder.setTitle("Password");
//                final EditText input = new EditText(mActivity);
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//                builder.setView(input);
//
//                builder.setPositiveButton("OK", (dialog, which) -> {
//
//                    String password = input.getText().toString();
//
//                    boolean isConnect = manager.connectEncryptWifi(bean, password);
//
//                    Toast.makeText(mActivity, "Connect:" + isConnect, Toast.LENGTH_LONG).show();
//                });
//                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//                builder.show();
//            } else { //開放
//                manager.connectOpenWifi(bean);
//            }
//        });
//
//        SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.sc_unit);
//        segmentedButtonGroup.setOnPositionChangedListener(position -> {
//            switch (position) {
//                case 0:
//                    manager.openWifi();
//                    ivReload.setClickable(true);
//                    wifiList.setVisibility(View.VISIBLE);
//                    //  manager.scanWifi();
//
//                    break;
//                case 1:
//                    manager.closeWifi();
//                    ivReload.setClickable(false);
//                    wifiList.setVisibility(View.INVISIBLE);
//                    //  manager.disConnectWifi();
//                    wifiAdapter.setData2View(null);
//                    break;
//
//            }
//        });
//
//        getPermission(rxPermissions);
//    }
//
//    private void onAllPermissionGranted() {
//        Log.i("WIFI", "onAllPermissionGranted: ");
//        manager = WifiManager.create(mActivity);
//        manager.setOnWifiChangeListener(this);
//        manager.setOnWifiConnectListener(this);
//        manager.setOnWifiStateChangeListener(this);
//        manager.openWifi();
//        //  manager.scanWifi();
//
//        //  wifiManager.startScan();
//    }
//
//    private void onSomePermissionNotGranted() {
//        Toast.makeText(mActivity, "onSomePermissionNotGranted", Toast.LENGTH_SHORT).show();
//    }
//
//    private void onNeverShowPromptAgainClick() {
//        Toast.makeText(mActivity, "onNeverShowPromptAgainClick", Toast.LENGTH_SHORT).show();
//    }
//
//    private void getPermission(RxPermissions rxPermissions) {
//        Disposable d = rxPermissions
//                .requestEachCombined(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_WIFI_STATE,
//                        Manifest.permission.CHANGE_NETWORK_STATE,
//                        Manifest.permission.ACCESS_NETWORK_STATE,
//                        Manifest.permission.CHANGE_WIFI_STATE
//                )
//                .subscribe(permission -> {
//                    if (permission.granted) {
//                        onAllPermissionGranted();
//                    } else if (permission.shouldShowRequestPermissionRationale) {
//                        onSomePermissionNotGranted();
//                    } else {
//                        onNeverShowPromptAgainClick();
//                    }
//                });
//    }
//
//    @Override
//    public void onWifiChanged(List<IWifi> iWifiList) {
//        Log.i("WIFI", "onWifiChanged: " + iWifiList.size() );
//
//        wifiAdapter.setData2View(iWifiList);
//    }
//
//    @Override
//    public void onConnectChanged(boolean status) {
//        Log.i("WIFI", "onConnectChanged: " + status);
//    }
//
//    @Override
//    public void onStateChanged(State state) {
//        if (state == State.ENABLED) {
//            manager.scanWifi();
//        }
//        Log.i("WIFI", "onStateChanged: " + state);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (manager != null) manager.destroy();
//    }
//}