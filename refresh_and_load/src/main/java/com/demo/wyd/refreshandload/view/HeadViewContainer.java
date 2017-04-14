package com.demo.wyd.refreshandload.view;

import android.content.Context;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

/**
 * Description:下拉刷新布局头部的容器
 * Created by wyd on 2017/4/13.
 */

public class HeadViewContainer extends RelativeLayout {

    private Animation.AnimationListener mListener;

    public HeadViewContainer(Context context) {
        super(context);
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

}
