package com.demo.wyd.framedemo.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.demo.wyd.framedemo.carrier.CarrierWelfare;
import com.demo.wyd.framedemo.iView.IViewWelfare;
import com.demo.wyd.framedemo.request.APIStore;
import com.demo.wyd.framedemo.request.RequestEngine;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description:
 * Created by wyd on 2017/2/21.
 */

public class WelfarePresenter extends BasePresenter {
    private static final String TAG = "WelfarePresenter";
    private IViewWelfare iViewWelfare;

    public WelfarePresenter(Context mContext, IViewWelfare iViewWelfare) {
        super(mContext);
        this.iViewWelfare = iViewWelfare;
    }

    public void gainWelfare() {
        APIStore.WelfareService welfareService = RequestEngine.getInstance().create(APIStore.WelfareService.class);
        Call<CarrierWelfare> welfareCall = welfareService.getWelfareList("福利");
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
        });
    }
}
