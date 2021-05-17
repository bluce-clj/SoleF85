package com.dyaco.spiritbike.product_flavors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public abstract class BaseFragment2 extends Fragment {
    protected ResultArgs mArgs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(requireLayoutId(), container, false);
    }


    protected abstract @LayoutRes
    int requireLayoutId();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mArgs = new ResultArgs(getArguments());
        onFindView(view);
        onBindListener();
    }

    protected abstract void onFindView(View rootView);

    protected abstract void onBindListener();

    protected void go(@IdRes int destination) {
        go(destination, null);
    }

    protected void go(@IdRes int destination, Bundle bundle) {
        getNavController().navigate(destination, bundle);
    }

    protected void back() {
        getNavController().popBackStack();
    }

    protected <T> void setResult(T data) {
        //比較相等性以外的資源類型（@IdRes）是危險的，通常是錯誤的。某些資源類型將最高位設置為高，這會使值變為負數
        if (null == mArgs || 0 >= mArgs.getRecipientId()) {
            return;
        }
        getNavController().getBackStackEntry(mArgs.getRecipientId()).getSavedStateHandle().getLiveData(String.valueOf(mArgs.getRequestCode())).postValue(new Pair(mArgs.getRequestCode(), data));
    }

    /**
     * @param destination 要迁移到的页面
     * @param requestCode 与StartActivityForResult 的RequestCode 相同
     * @param <T>         返回数据类型
     * @return {@link LiveData} Pair.first: requestCode; Pair.second: resultData
     */
    protected <T> LiveData<Pair<Integer, T>> startFragmentForResult(@IdRes int destination, int requestCode) {
        return startFragmentForResult(destination, requestCode, null);
    }

    protected <T> LiveData<Pair<Integer, T>> startFragmentForResult(int requestCode, @NonNull NavDirections destination) {
        return startFragmentForResult(destination.getActionId(), requestCode, destination.getArguments());
    }

    protected <T> LiveData<Pair<Integer, T>> startFragmentForResult(@IdRes int destination, int requestCode, @Nullable Bundle bundle) {
        ResultArgs args = new ResultArgs(getNavController().getCurrentDestination().getId(), requestCode).setBusinessArgs(bundle);
        LiveData<Pair<Integer, T>> liveData = getNavController().getCurrentBackStackEntry().getSavedStateHandle().getLiveData(String.valueOf(requestCode));
        getNavController().navigate(destination, args.toBundle());
        return liveData;
    }

    protected void popTo(@IdRes int destination) {
        getNavController().popBackStack(destination, true);
    }

    protected NavController getNavController() {
        return Navigation.findNavController(getView());
    }

    public Activity mActivity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}

