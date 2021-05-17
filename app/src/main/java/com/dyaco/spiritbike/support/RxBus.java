package com.dyaco.spiritbike.support;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public final class RxBus {


    private final Subject<Object> bus;


    private RxBus() {
        // toSerialized method made bus thread safe
        bus = PublishSubject.create().toSerialized();
    }


    public static RxBus getInstance() {
        return Holder.BUS;
    }


    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }


    public void post(Object obj) {
        bus.onNext(obj);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return bus.ofType(tClass);
    }


    public Observable<Object> toObservable() {
        return bus;
    }


    public boolean hasObservers() {
        return bus.hasObservers();
    }

    public void unregisterAll() {
        //會將所有由mBus生成的Observable都置completed狀態,後續的所有消息都收不到了
        bus.onComplete();
    }
}