package com.dyaco.spiritbike.support;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class RxTimer {

    private Disposable mDisposable;

    /**
     * milliseconds毫秒後執行指定動作
     *
     * @param milliSeconds milliSeconds
     * @param rxAction     rxAction
     */
    public void timer(long milliSeconds, final RxAction rxAction) {
        Observable.timer(milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (rxAction != null) {
                            rxAction.action(number);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //取消訂閱
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消訂閱
                        cancel();
                    }
                });
    }

    /**
     * 每隔milliseconds毫秒後執行指定動作
     * initialDelay 第一次執行的延遲時間
     * .take(5) 執行5次
     * @param milliSeconds milliSeconds
     * @param rxAction rxAction
     */
    public void interval(long milliSeconds, final RxAction rxAction) {
        Observable.interval(1000, milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long milliSeconds) {
                        if (rxAction != null) {
                            rxAction.action(milliSeconds * 1000);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void interval2(long milliSeconds, final RxAction rxAction) {
        Observable.interval(3000, milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long milliSeconds) {
                        if (rxAction != null) {
                            rxAction.action(milliSeconds * 1000);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void interval3(long milliSeconds, final RxAction rxAction) {
        Observable.interval(0, milliSeconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long milliSeconds) {
                        if (rxAction != null) {
                            rxAction.action(milliSeconds);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 取消訂閱
     */
    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    public interface RxAction {
        /**
         * 讓調用者指定指定動作
         *
         * @param number number
         */
        void action(long number);
    }
}