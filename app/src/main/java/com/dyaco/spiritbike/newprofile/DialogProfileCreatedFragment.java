package com.dyaco.spiritbike.newprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;


public class DialogProfileCreatedFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_profile_created, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btSeeProfile_DialogProfileCreated = view.findViewById(R.id.btSeeProfile_DialogProfileCreated);
        btSeeProfile_DialogProfileCreated.setOnClickListener(view1 -> {
            //Navigation.findNavController(view).navigate(R.id.action_dialogProfileCreatedFragment_to_dashboardFragment);
            Intent intent = new Intent(mActivity, DashboardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("OpenProfile", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtras(bundle);
            startActivity(intent);
            mActivity.finish();
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }
}