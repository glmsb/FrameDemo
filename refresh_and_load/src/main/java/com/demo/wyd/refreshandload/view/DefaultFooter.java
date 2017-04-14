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
        text.setText("努力加载中...");
        imageView.setImageResource(R.drawable.ic_launcher);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(3000);
        animator.start();
    }

    @Override
    public void onPushDistance(int distance) {

    }

    @Override
    public void onPushEnable(boolean enable) {
        text.setText(enable ? "释放以加载" : "上来加载");
        imageView.setImageResource(R.mipmap.iconfont_downgrey);
        imageView.setRotation(enable ? 0 : 180);
    }
}
