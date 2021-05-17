package com.dyaco.spiritbike.support.banner.indicator;

import android.view.View;

import androidx.annotation.NonNull;

import com.dyaco.spiritbike.support.banner.config.IndicatorConfig;
import com.dyaco.spiritbike.support.banner.listener.OnPageChangeListener;


public interface Indicator extends OnPageChangeListener {
    @NonNull
    View getIndicatorView();

    IndicatorConfig getIndicatorConfig();

    void onPageChanged(int count, int currentPosition);

}
