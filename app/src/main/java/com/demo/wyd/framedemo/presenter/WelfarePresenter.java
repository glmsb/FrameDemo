package com.demo.wyd.framedemo.presenter;

import android.content.Context;

import com.demo.wyd.framedemo.bean.Welfare;
import com.demo.wyd.framedemo.carrier.CarrierWelfare;
import com.demo.wyd.framedemo.iView.IViewWelfare;
import com.demo.wyd.framedemo.model.WelfareListModel;
import com.demo.wyd.framedemo.request.APIStore;
import com.demo.wyd.framedemo.request.BaseSubscriber;
import com.demo.wyd.framedemo.request.RequestEngine;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:P层，有两个特点。1、需要持有V层的引用；2、需要持有M层的应用
 * Created by wyd on 2017/2/21.
 */

public class WelfarePresenter extends BasePresenter {
    private IViewWelfare iViewWelfare;
    private WelfareListModel model;

    public WelfarePresenter(Context mContext, IViewWelfare iViewWelfare) {
        super(mContext);
        this.iViewWelfare = iViewWelfare;
        this.model = new WelfareListModel();
    }

    public void gainWelfare(String type, int pageNo) {
        model.getWelfareList(type, pageNo, new BaseSubscriber<List<Welfare>>(mContext, true) {
            @Override
            protected void onHandleSuccess(List<Welfare> result) {
                iViewWelfare.updateLayout(result);
            }
        });
    }
        /*APIStore.WelfareService welfareService = RequestEngine.getInstance().create(APIStore.WelfareService.class);
       *//* Call<CarrierWelfare> welfareCall = welfareService.getWelfareList("福利");
        welfareCall.enqueue(new Callback<CarrierWelfare>() {
            @Override
            public void onResponse(Call<CarrierWelfare> call, Response<CarrierWelfare> response) {
                Log.e(TAG, "onResponse: " + response.raw().toString());
                iViewWelfare.updateLayout(response.body().getResults());
            }

            @Override
            public void onFailure(Call<CarrierWelfare> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*//*


        welfareService.getWelfare(type, pageNo)
                .subscribeOn(Schedulers.io())
                .map(new Function<CarrierWelfare, List<Welfare>>() {
                    @Override
                    public List<Welfare> apply(@NonNull CarrierWelfare carrierWelfare) throws Exception {
                        return carrierWelfare.getResults();
                    }
                })
                .filter(new Predicate<List<Welfare>>() {
                    @Override
                    public boolean test(@NonNull List<Welfare> welfares) throws Exception {
                        return welfares != null && welfares.size() > 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Welfare>>() {
                    @Override
                    public void accept(@NonNull List<Welfare> welfares) throws Exception {
                        iViewWelfare.updateLayout(welfares);
                    }
                });
    }*/
}
