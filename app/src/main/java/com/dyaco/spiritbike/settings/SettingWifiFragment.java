package com.dyaco.spiritbike.settings;

//import android.Manifest;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.text.InputType;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
//import com.dyaco.spiritbike.DashboardActivity;
//import com.dyaco.spiritbike.R;
//import com.dyaco.spiritbike.support.BaseFragment;
//import com.hacknife.wifimanager.IWifi;
//import com.hacknife.wifimanager.IWifiManager;
//import com.hacknife.wifimanager.OnWifiChangeListener;
//import com.hacknife.wifimanager.OnWifiConnectListener;
//import com.hacknife.wifimanager.OnWifiStateChangeListener;
//import com.hacknife.wifimanager.State;
//import com.hacknife.wifimanager.WifiManager;
//import com.tbruyelle.rxpermissions3.RxPermissions;
//
//import java.util.List;
//
//import io.reactivex.rxjava3.disposables.Disposable;

/**IWifi
 * name()	ssid
 * isEncrypt()	是否加密
 * level()	信號強度
 * isConnected()
 * isSaved()	密碼是否保存
 *
 * IWifiManager
 * removeWifi(IWifi wifi)
 */
public class SettingWifiFragment{
//public class SettingWifiFragment extends BaseFragment implements OnWifiChangeListener, OnWifiConnectListener, OnWifiStateChangeListener {
//    private RecyclerView wifiList;
//  //  WifiReceiver receiverWifi;
//  //  private WifiManager wifiManager;
//    private WifiAdapter wifiAdapter;
//    private ProgressBar progress;
//    private ImageView ivReload;
//    private IWifiManager manager;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_setting_wifi, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
//        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);
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
//        progress = view.findViewById(R.id.progress);
//
//        wifiList.setLayoutManager(new LinearLayoutManager(mActivity));
//        wifiList.setHasFixedSize(true);
//
//        wifiList.setAdapter(wifiAdapter);
//
////        manager = WifiManager.create(mActivity);
////        manager.setOnWifiChangeListener(this);
////        manager.setOnWifiConnectListener(this);
////        manager.setOnWifiStateChangeListener(this);
//        //manager.openWifi();
//       // manager.scanWifi();
//
//
//        wifiAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(IWifi bean) {
//
//                Log.i("WIFI", "onItemClick: " + bean);
//
//                if (bean.isConnected()) //已連線
//                    return;
//
//
//                if (bean.isSaved() ) { //已保存密碼
//                    manager.connectSavedWifi(bean);
//                } else if (bean.isEncrypt()) { //加密
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//                    builder.setTitle("Password");
//                    final EditText input = new EditText(mActivity);
//                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//                    builder.setView(input);
//
//                    builder.setPositiveButton("OK", (dialog, which) -> {
//
//                        String password = input.getText().toString();
//
//                        boolean isConnect = manager.connectEncryptWifi(bean, password);
//
//                        Toast.makeText(mActivity, "Connect:" + isConnect, Toast.LENGTH_LONG).show();
//                    });
//                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//                    builder.show();
//                } else { //開放
//                    manager.connectOpenWifi(bean);
//                }
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
//                  //  manager.scanWifi();
//
//                    break;
//                case 1:
//                    manager.closeWifi();
//                    ivReload.setClickable(false);
//                    wifiList.setVisibility(View.INVISIBLE);
//                  //  manager.disConnectWifi();
//                    wifiAdapter.setData2View(null);
//                    break;
//
//            }
//        });
//
//        getPermission();
//    }
//
//
//    private void getPermission() {
//        Disposable d = new RxPermissions(this)
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
//                        Log.i("WIFI", "onAllPermissionGranted: ");
//                        manager = WifiManager.create(mActivity);
//                        manager.setOnWifiChangeListener(this);
//                        manager.setOnWifiConnectListener(this);
//                        manager.setOnWifiStateChangeListener(this);
//                        manager.openWifi();
//                        //  manager.scanWifi();
//                        //  wifiManager.startScan();
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
//        manager.destroy();
//    }

}