package com.demo.wyd.framedemo.request;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:设置同一的拦截，对每个订阅做相同的标准
 * Created by wyd on 2018-3-26.
 */

public class RxSchedulers {
    public static <T> FlowableTransformer<T, T> compose() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(Subscription subscription) throws Exception {
                                if (!NetworkUtils.isAvailableByPing()) {
                                    ToastUtils.showShort("网络异常");
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
        /*return new FlowableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                if (!NetworkUtils.isAvailableByPing()) {
                                    ToastUtils.showShort("网络异常");
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };*/
    }

}
