package com.demo.wyd.refreshandload;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

/**
 * Description:刷新和加载的父布局
 * Created by wyd on 2017/4/12.
 */

public class RLLayout_todo extends LinearLayout implements NestedScrollingParent,NestedScrollingChild {

    private static final int HEADER_FOOTER_HEIGHT = 50;// 头部和底部的高度 (dp)

    private Context mContext;
    private LinearLayout headerLayout, footerLayout;
    private int headerHeight, footerHeight;

    private NestedScrollingParentHelper helper;
    private NestedScrollingChildHelper childHelper;
    private boolean isFling;
    private boolean IsRefresh = true;
    private boolean IsLoad = true;
    private int totalY = 0;

    public RLLayout_todo(Context context) {
        super(context);
        mContext = context;
    }

    public RLLayout_todo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initLayout();
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        //设置布局方式
        setOrientation(VERTICAL);

        //初始化头部和底部的高度 (dp)
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int header_footer_height = (int) (metrics.density * HEADER_FOOTER_HEIGHT);
        headerHeight = footerHeight = header_footer_height;

        helper = new NestedScrollingParentHelper(this);
        childHelper = new NestedScrollingChildHelper(this);

        if (headerLayout == null) {
            headerLayout = new LinearLayout(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.v_refresh_headerer, this, false);
            addHeaderView(view);
        }
        addView(headerLayout);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (footerLayout == null) {
            footerLayout = new LinearLayout(mContext);
            View view = LayoutInflater.from(mContext).inflate(R.layout.v_refresh_headerer, this, false);
            ((TextView) view.findViewById(R.id.tv_header)).setText("加载更多！");
            addFooterView(view);
        }
        addView(footerLayout);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 添加刷新的头部View
     *
     * @param headerView 刷新头布局
     */
    public void addHeaderView(View headerView) {
        //加入刷新头布局
        headerLayout.removeAllViews();
        headerLayout.addView(headerView);

        // 更改布局的布局参数
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, headerHeight);
        layoutParams.topMargin = -headerHeight;
        headerLayout.setLayoutParams(layoutParams);
    }

    /**
     * 添加加载的底部View
     *
     * @param footerView 底部布局
     */
    public void addFooterView(View footerView) {
        //加入加载的底部布局
        footerLayout.removeAllViews();
        footerLayout.addView(footerView);

        // 更改布局的布局参数
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, footerHeight);
        layoutParams.bottomMargin = -footerHeight;
        footerLayout.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.e(TAG, "onStartNestedScroll: " + child.toString() + target.toString() + nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        Log.e(TAG, "onNestedScrollAccepted: " + child.toString() + target.toString() + axes);
        helper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public int getNestedScrollAxes() {
        Log.e(TAG, "getNestedScrollAxes: ");
        return helper.getNestedScrollAxes();
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.e(TAG, "onNestedPreScroll: " + target.toString() + dx + dy + consumed[0] + consumed[1]);

        isFling = true;
        if (IsRefresh && dy > 0) {
            totalY += dy;
            if (totalY / 2 <= 0) {
                scrollTo(0, totalY / 2);
                consumed[1] = dy;
            } else {
                scrollTo(0, 0);
                consumed[1] = 0;
            }
            return;
        }
        if (IsLoad && dy < 0) {
            totalY += dy;
            if (totalY / 2 >= 0) {
                scrollTo(0, totalY / 2);
                consumed[1] = dy;
            } else {
                scrollTo(0, 0);
                consumed[1] = 0;
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e(TAG, "onNestedScroll: " + target.toString() + dxConsumed + dyConsumed + dxUnconsumed + dyUnconsumed);

        if (dyUnconsumed != 0) {
            totalY += dyUnconsumed;
            scrollTo(0, totalY / 2);
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
        Log.e(TAG, "onStopNestedScroll: " + child.toString());
        helper.onStopNestedScroll(child);
        isFling = false;
        if (-totalY / 2 >= headerHeight) {
            smoothScrollTo(-totalY / 2, headerHeight);
        } else if (totalY / 2 >= footerHeight) {
            smoothScrollTo(-totalY / 2, -footerHeight);
        } else {
            smoothScrollTo(0, 0);
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.e(TAG, "onNestedPreFling: " + target.toString() + velocityX + velocityY);
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "onNestedFling: " + target.toString() + velocityX + velocityY + consumed);
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    private void smoothScrollTo(int fromY, int toY) {
        ValueAnimator animator = ValueAnimator.ofInt(fromY, toY);
        if (fromY == toY) {
            animator.setDuration(0);
        } else {
            animator.setDuration(300);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int to = -(int) animation.getAnimatedValue();
                scrollTo(0, to);
                totalY = to * 2;
            }
        });
        animator.start();
    }



    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        childHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return childHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }


    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
