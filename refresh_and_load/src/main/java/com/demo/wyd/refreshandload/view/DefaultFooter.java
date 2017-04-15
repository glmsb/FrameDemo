package com.demo.wyd.refreshandload.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.wyd.refreshandload.R;
import com.demo.wyd.refreshandload.listener.OnPushLoadMoreListener;

/**
 * Description:
 * Created by wyd on 2017/4/14.
 */

public class DefaultFooter extends FrameLayout implements OnPushLoadMoreListener {
    private TextView text;
    private ImageView imageView;
    private boolean isLoadOver;

    public DefaultFooter(@NonNull Context context) {
        this(context, null);
    }

    public DefaultFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.v_refresh_headerer, this);
        text = ((TextView) findViewById(R.id.tv_header));
        imageView = (ImageView) findViewById(R.id.imv_header);
    }

    @Override
    public void onLoadMore() {
        if (isLoadOver)
            return;
        text.setText("努力加载中...");
        imageView.setImageResource(R.mipmap.ic_refreshing_loading);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void onPushDistance(int distance) {

    }

    @Override
    public void onPushEnable(boolean enable) {
        if (isLoadOver)
            return;
        imageView.setVisibility(VISIBLE);
        text.setText(enable ? "释放以加载" : "上拉加载");
        imageView.setImageResource(R.mipmap.ic_refresh_load);
        imageView.setRotation(enable ? 0 : 180);
    }

    public void setLoadOver(boolean isLoadOver) {
        this.isLoadOver = isLoadOver;
        text.setText("已加载全部");
        imageView.setVisibility(GONE);
    }
}
