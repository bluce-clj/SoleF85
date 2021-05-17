package com.dyaco.spiritbike.support.room;

import java.util.List;

public abstract class DatabaseCallback<T> {

    public void onDataLoadedList(List<T> lists) {

     }

    public void onDataLoadedBean(T userProfileEntity) {

    }

    public void onAdded(long rowId) {

    }

    public void onQueryAll() {

    }

    public void onDeleted() {

    }

    public void onUpdated() {

    }

    public void onError(String err) {

    }

    public void onCount(Integer i) {

    }

    public void onNoData() {

    }

}
