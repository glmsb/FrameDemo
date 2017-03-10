package com.demo.wyd.framedemo.customView;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;

import com.demo.wyd.framedemo.bean.Welfare;

import java.util.List;

/**
 * Description:
 * Created by wyd on 2017/3/9.
 */

public class MyCallBack extends ItemTouchHelper.SimpleCallback {
    private static final int TRANS_Y_GAP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, Resources.getSystem().getDisplayMetrics());
    private static final float SCALE_GAP = 0.05f;

    private List<Welfare> welfareList;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;

    public MyCallBack(RecyclerView recyclerView, RecyclerView.Adapter adapter, List<Welfare> data) {
        this(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.welfareList = data;
        this.rv = recyclerView;
        this.adapter = adapter;
    }

    public MyCallBack(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Welfare welfare = welfareList.remove(viewHolder.getAdapterPosition());
        welfareList.add(welfareList.size()-1, welfare);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
       /* int childCount = recyclerView.getChildCount();
        //先根据滑动的dxdy 算出现在动画的比例系数fraction
        double swipdValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipdValue / getThreshold(viewHolder);
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        }
        for (int i = 1; i < childCount; i++) {
            View view = recyclerView.getChildAt(i);
            view.setScaleX((float) (1 - SCALE_GAP * i + fraction * SCALE_GAP));
            view.setTranslationY((float) (TRANS_Y_GAP * i - fraction * TRANS_Y_GAP));
        }*/


        //先根据滑动的dxdy 算出现在动画的比例系数fraction
        double swipValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipValue / getThreshold(viewHolder);
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        }
        //对每个ChildView进行缩放 位移
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            //第几层,举例子，count =7， 最后一个TopView（6）是第0层，
            int level = childCount - i - 1;
            if (level > 0) {
                child.setScaleX((float) (1 - SCALE_GAP * level + fraction * SCALE_GAP));

                if (level < 4 - 1) {
                    child.setScaleY((float) (1 - SCALE_GAP * level + fraction * SCALE_GAP));
                    child.setTranslationY((float) (TRANS_Y_GAP * level - fraction * TRANS_Y_GAP));
                }
            }
        }
    }

    //水平方向是否可以被回收掉的阈值
    public float getThreshold(RecyclerView.ViewHolder viewHolder) {
        return rv.getWidth() * getSwipeThreshold(viewHolder);
    }

}
