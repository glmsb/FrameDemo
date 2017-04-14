package com.demo.wyd.refreshandload.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Description:默认的下拉刷新样式
 * Created by wyd on 2017/4/13.
 */

public class CircleProgressView extends View implements Runnable {

    private static final int PEROID = 16;// 绘制周期
    private Paint progressPaint;
    private Paint bgPaint;
    private int width;// view的高度
    private int height;// view的宽度

    private boolean isOnDraw = false;
    private boolean isRunning = false;
    private int startAngle = 0;
    private int speed = 8;
    private RectF ovalRect = null;
    private RectF bgRect = null;
    private int progressColor = 0xffcccccc;
    private int circleBackgroundColor = 0xffffffff;
    private int shadowColor = 0xff999999;
    private float density = 1.0f;


    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        density = metrics.density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(getBgRect(), 0, 360, false, createBgPaint());
        int index = startAngle / 360;
        int swipeAngle;
        if (index % 2 == 0) {
            swipeAngle = (startAngle % 720) / 2;
        } else {
            swipeAngle = 360 - (startAngle % 720) / 2;
        }
        canvas.drawArc(getOvalRect(), startAngle, swipeAngle, false, createPaint());
    }

    private RectF getBgRect() {
        width = getWidth();
        height = getHeight();
        if (bgRect == null) {
            int offset = (int) (density * 2);
            bgRect = new RectF(offset, offset, width - offset, height - offset);
        }
        return bgRect;
    }

    private RectF getOvalRect() {
        width = getWidth();
        height = getHeight();
        if (ovalRect == null) {
            int offset = (int) (density * 8);
            ovalRect = new RectF(offset, offset, width - offset, height - offset);
        }
        return ovalRect;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setCircleBackgroundColor(int circleBackgroundColor) {
        this.circleBackgroundColor = circleBackgroundColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    /**
     * 根据画笔的颜色，创建画笔
     *
     * @return 画笔
     */
    private Paint createPaint() {
        if (this.progressPaint == null) {
            progressPaint = new Paint();
            progressPaint.setStrokeWidth((int) (density * 3));
            progressPaint.setStyle(Paint.Style.STROKE);
            progressPaint.setAntiAlias(true);
        }
        progressPaint.setColor(progressColor);
        return progressPaint;
    }

    /**
     * 绘制背景
     *
     * @return 画笔
     */
    private Paint createBgPaint() {
        if (this.bgPaint == null) {
            bgPaint = new Paint();
            bgPaint.setColor(circleBackgroundColor);
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setAntiAlias(true);
            this.setLayerType(LAYER_TYPE_SOFTWARE, bgPaint);
            bgPaint.setShadowLayer(4.0f, 0.0f, 2.0f, shadowColor);
        }
        return bgPaint;
    }

    public void setPullDistance(int distance) {
        this.startAngle = distance * 2;
        postInvalidate();
    }

    @Override
    public void run() {
        while (isOnDraw) {
            isRunning = true;
            long startTime = System.currentTimeMillis();
            startAngle += speed;
            postInvalidate();
            long time = System.currentTimeMillis() - startTime;
            if (time < PEROID) {
                try {
                    Thread.sleep(PEROID - time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setOnDraw(boolean isOnDraw) {
        this.isOnDraw = isOnDraw;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onDetachedFromWindow() {
        isOnDraw = false;
        super.onDetachedFromWindow();
    }
}
