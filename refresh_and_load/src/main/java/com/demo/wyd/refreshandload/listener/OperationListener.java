package com.demo.wyd.refreshandload.listener;

/**
 * Description:用户用于在四大组件中发起请求等操作的接口
 * Created by wyd on 2017/4/14.
 */

public interface OperationListener {
    //下拉刷新
    void doRefresh();

    //上拉加载
    void doLoad();
}
