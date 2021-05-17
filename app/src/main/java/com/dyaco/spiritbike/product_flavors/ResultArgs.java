package com.dyaco.spiritbike.product_flavors;

import android.os.Bundle;

import androidx.annotation.IdRes;

import java.util.HashMap;
import java.util.Map;

public class ResultArgs {


    private static final String RECIPIENT_ID = "resultArgsRecipientId";

    private static final String REQUEST_CODE = "ResultArgsRequestCode";

    private static final String BUNDLE = "ResultArgsBundle";


    private final Map<String, Object> mArgsMap = new HashMap<>();

    public ResultArgs(@IdRes int recipientId, int requestCode) {
        mArgsMap.put(RECIPIENT_ID, recipientId);
        mArgsMap.put(REQUEST_CODE, requestCode);
    }

    public ResultArgs(Bundle bundle) {
        if (null == bundle) {
            return;
        }
        setBusinessArgs(bundle);
        mArgsMap.put(RECIPIENT_ID, bundle.getInt(RECIPIENT_ID));
        mArgsMap.put(REQUEST_CODE, bundle.getInt(REQUEST_CODE));
    }

    public Bundle toBundle() {
        Bundle temp = new Bundle();
        if (null != getBusinessArgs()) {
            temp.putAll(getBusinessArgs());
        }
        temp.putInt(RECIPIENT_ID, getRecipientId());
        temp.putInt(REQUEST_CODE, getRequestCode());
        return temp;
    }

    public @IdRes
    int getRecipientId() {
        return (int) mArgsMap.get(RECIPIENT_ID);
    }

    public int getRequestCode() {
        return (int) mArgsMap.get(REQUEST_CODE);
    }

    public ResultArgs setBusinessArgs(Bundle businessArgs) {
        if (null == businessArgs) {
            return this;
        }
        mArgsMap.put(BUNDLE, businessArgs);
        return this;
    }

    public Bundle getBusinessArgs() {
        return (Bundle) mArgsMap.get(BUNDLE);
    }

}