package com.demo.wyd.refreshandload.listener;

/**
 * Description:上拉加载的接口
 * Created by wyd on 2017/4/13.
 */

public interface OnPushLoadMoreListener {
    // 开始加载
    void onLoadMore();

    // 上拉过程中的上拉距离
    void onPushDistance(int distance);

    //上拉过程中，上拉的距离是否足够触发刷新
    void onPushEnable(boolean enable);
}
