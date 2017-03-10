package com.demo.wyd.framedemo.customView;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description:
 * Created by wyd on 2017/3/9.
 */

public class OverLayCardLayoutManager extends RecyclerView.LayoutManager {
    private static final int MAX_SHOW_COUNT = 4;
    private static final int TRANS_Y_GAP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, Resources.getSystem().getDisplayMetrics());
    private static final float SCALE_GAP = 0.05f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * RecyclerView 初始化 、 数据源改变时 都会被调用。
     * 1 在RecyclerView初始化时，会被调用两次。
     * 2 在调用adapter.notifyDataSetChanged()时，会被调用。
     * 3 在调用setAdapter替换Adapter时,会被调用。
     * 4 在RecyclerView执行动画时，它也会被调用。
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();

       /* for (int i = 0; i < MAX_SHOW_COUNT && i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            layoutDecoratedWithMargins(view, 0, 0, getWidth(), getHeight());
            if (i > 0) {
                view.setTranslationY(TRANS_Y_GAP * i);
            }
        }*/

        for (int i = MAX_SHOW_COUNT - 1; i < itemCount && i >= 0; i--) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            layoutDecoratedWithMargins(view, 0, 0, getWidth(), getHeight());
            if (i > 0) {
                view.setTranslationY(TRANS_Y_GAP * i);
                view.setScaleX(1 - SCALE_GAP * i);
            }
        }

        /*if (itemCount >= MAX_SHOW_COUNT) {
            for (int position = itemCount - MAX_SHOW_COUNT; position < itemCount; position++) {
                View view = recycler.getViewForPosition(position);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                int widthSpace = getWidth() - getDecoratedMeasuredWidth(view);
                int heightSpace = getHeight() - getDecoratedMeasuredHeight(view);
                //我们在布局时，将childView居中处理，这里也可以改为只水平居中
                layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2, widthSpace / 2 + getDecoratedMeasuredWidth(view), heightSpace / 2 + getDecoratedMeasuredHeight(view));

                int level = itemCount - position - 1;
                if (level > 0) {
                    view.setScaleX(1 - SCALE_GAP);
                    //前N层，依次向下位移和Y方向的缩小
                    if (level < MAX_SHOW_COUNT - 1) {
                        view.setTranslationY(TRANS_Y_GAP * level);
                        view.setScaleY(1 - SCALE_GAP * level);
                    } else {//第N层在 向下位移和Y方向的缩小的成都与 N-1层保持一致
                        view.setTranslationY(TRANS_Y_GAP * (level - 1));
                        view.setScaleY(1 - SCALE_GAP * (level - 1));
                    }
                }
            }
        }*/
    }
}
