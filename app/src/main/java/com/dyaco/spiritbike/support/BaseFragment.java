package com.dyaco.spiritbike.support;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dyaco.spiritbike.support.banner.util.LogUtils;

import java.util.List;

public abstract class BaseFragment extends Fragment {

    public Activity mActivity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
            LogUtils.d("當前acitivty:" + mActivity.getClass().getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }


    public Fragment getVisibleFragment()
    {
        List<Fragment> fragments = getFragmentManager().getFragments();
        for(Fragment fragment:fragments)
        {
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    public String getCurrentFragmentName() {
        if (getVisibleFragment() != null) {
            return getVisibleFragment().getClass().getSimpleName() + "->";
        } else {
            return null;
        }

    }


}
