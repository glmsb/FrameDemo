package com.demo.wyd.framedemo.model;

import com.demo.wyd.framedemo.bean.Welfare;
import com.demo.wyd.framedemo.request.APIStore;
import com.demo.wyd.framedemo.request.BaseSubscriber;
import com.demo.wyd.framedemo.request.RequestEngine;
import com.demo.wyd.framedemo.request.RxSchedulers;

import java.util.List;

/**
 * Description:M层，数据层
 * Created by wyd on 2018-3-25.
 */

public class WelfareListModel {
    public void getWelfareList(String type, int pageNo, BaseSubscriber<List<Welfare>> callBack) {
        APIStore.WelfareService welfareService = RequestEngine.getInstance().create(APIStore.WelfareService.class);

        //Observable（Flowable）->被观察者
        //Observer（Subscriber）->观察者
        welfareService.getWelfare(type, pageNo)
                .compose(RxSchedulers.compose())
                .subscribe(callBack);
    }
}
