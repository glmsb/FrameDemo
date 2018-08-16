package com.demo.wyd.framedemo.request;

import android.app.ProgressDialog;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.demo.wyd.framedemo.model.BaseResponse;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;

/**
 * Description:观察者的封装
 * Created by wyd on 2018-3-26.
 */

public abstract class BaseSubscriber<T> implements FlowableSubscriber<BaseResponse<T>> {
    private ProgressDialog progressDialog;
    private Subscription subscription;

    protected BaseSubscriber() {
    }

    protected BaseSubscriber(Context mContext, boolean showProgress) {
        if (showProgress) {
             progressDialog = ProgressDialog.show(mContext, "请稍后", "正在努力加载...", true);
            progressDialog.setOnCancelListener(dialogInterface -> subscription.cancel());
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscription = s;
        subscription.request(5000);// TODO: 2018-3-26  这里不知道为什么要设置这一句
        LogUtils.e("onSubscribe");
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onNext(BaseResponse<T> t) {
        LogUtils.e("onNext");
        if (t.isError()) {
            ToastUtils.showLong("请检查服务器接口返回结果");
            LogUtils.d(t);
        } else {
            T results = t.getResults();
            if (results==null){
                ToastUtils.showLong("请检查服务器接口返回结果");
                return;
            }
            onHandleSuccess(results);
        }
    }

    protected abstract void onHandleSuccess(T result);

    @Override
    public void onError(Throwable t) {//请求超时，网络不好，服务器故障等
        LogUtils.e("onError");

        ToastUtils.showLong(t.getMessage());
        LogUtils.e(t);
    }

    @Override
    public void onComplete() {
        LogUtils.e("onComplete");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
