package com.demo.wyd.refreshandload.listener;

/**
 * Description:下拉刷新的接口
 * Created by wyd on 2017/4/13.
 */

public interface OnPullRefreshListener {
    // 开始刷新
    void onRefresh();

    // 下拉过程中的下拉距离
    void onPullDistance(int distance);

    // 下拉过程中，下拉的距离是否足够触发刷新
    void onPullEnable(boolean enable);
}
