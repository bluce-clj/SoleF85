package com.dyaco.spiritbike;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyaco.spiritbike.engineer.DeviceSettingBean;

import static com.dyaco.spiritbike.MyApplication.getInstance;


public class LogoFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivLogo = view.findViewById(R.id.ivLogo);

        splash(view, ivLogo);

      //  new RxTimer().timer(2000, number -> splash(view, ivLogo));
    }

    private void splash(View view, ImageView ivLogo) {

        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();

     //   Log.d("OOOOOO", "splash: " + deviceSettingBean.isDsFirstLaunch() +","+ deviceSettingBean.isDsBeep() +","+ deviceSettingBean.isDsSleep());


        if (deviceSettingBean.isFirst_launch()) {
        //    Navigation.findNavController(view).navigate(R.id.action_logoFragment_to_firstLaunchWifiWidgetFragment);
            Navigation.findNavController(view).navigate(R.id.action_logoFragment_to_firstLaunchSetDateFragment);
        } else {
//            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
//                    .addSharedElement(ivLogo, "sharedView")
//                    .build();
//            Navigation.findNavController(view).navigate(R.id.action_logoFragment_to_startScreenFragment, null, null, extras);

            Navigation.findNavController(view).navigate(R.id.action_logoFragment_to_startScreenFragment);
        }
    }
}