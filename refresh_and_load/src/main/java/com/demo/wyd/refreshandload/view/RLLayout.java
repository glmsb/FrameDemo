package com.demo.wyd.refreshandload.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.demo.wyd.refreshandload.listener.OnPullRefreshListener;
import com.demo.wyd.refreshandload.listener.OnPushLoadMoreListener;
import com.demo.wyd.refreshandload.listener.OperationListener;

/**
 * Description::刷新和加载的父布局,非侵入式，用法和SwipeRefreshLayout类似。
 * Created by wyd on 2017/4/13.
 */

public class RLLayout extends ViewGroup {
    private static final String LOG_TAG = "RLLayout";
    private static final int HEADER_FOOTER_HEIGHT = 50;// 头部和底部的高度 (dp)

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};

    // RLLayout内的目标View，比如RecyclerView,ListView,ScrollView,GridView
    private View mTarget;
    private OnPullRefreshListener mOnPullRefreshListener;// 下拉刷新listener
    private OnPushLoadMoreListener mOnPushLoadMoreListener;// 上拉加载更多
    private OperationListener mOperationListener;

    private boolean mRefreshing = false;
    private boolean mLoadMore = false;
    private int mTouchSlop;
    private float mTotalDragDistance = -1; //默认是64dp
    private int mMediumAnimationDuration;
    private int mCurrentTargetOffsetTop;

    private boolean mOriginalOffsetCalculated = false;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;

    private boolean mScale = false;
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;

    private HeadViewContainer mHeadViewContainer;
    private RelativeLayout mFooterViewContainer;
    private int mHeaderViewIndex = -1;
    private int mFooterViewIndex = -1;
    protected int mFrom;
    private float mStartingScale;
    protected int mOriginalOffsetTop;
    private float mSpinnerFinalOffset;// 最后停顿时的偏移量px，与DEFAULT_CIRCLE_TARGET正比
    private boolean mNotify; //通知是否需要刷新和做出相应的动画
    private int mHeaderViewWidth, mFooterViewWidth;
    private int mHeaderViewHeight, mFooterViewHeight;
    private boolean mUsingCustomStart = false;
    private boolean targetScrollWithLayout = true;
    private int pushDistance = 0;

    private CircleProgressView defaultProgressView = null;
    private boolean usingDefaultHeader = false;
    private boolean isProgressEnable = false;

    public RLLayout(Context context) {
        this(context, null);
    }

    public RLLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*
         * getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
         */
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        //noinspection deprecation
        mHeaderViewWidth = display.getWidth();
        //noinspection deprecation
        mFooterViewWidth = display.getWidth();
        mHeaderViewHeight = (int) (HEADER_FOOTER_HEIGHT * metrics.density);
        mFooterViewHeight = (int) (HEADER_FOOTER_HEIGHT * metrics.density);
//        defaultProgressView = new CircleProgressView(context);
        createHeaderViewContainer();
        createFooterViewContainer();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
    }

    /**
     * 改变孩子节点绘制的顺序
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        // 将新添加的View,放到最后绘制
        if (mHeaderViewIndex < 0 && mFooterViewIndex < 0) {
            return i;
        }
        if (i == childCount - 2) {
            return mHeaderViewIndex;
        }
        if (i == childCount - 1) {
            return mFooterViewIndex;
        }
        int bigIndex = mFooterViewIndex > mHeaderViewIndex ? mFooterViewIndex : mHeaderViewIndex;
        int smallIndex = mFooterViewIndex < mHeaderViewIndex ? mFooterViewIndex : mHeaderViewIndex;
        if (i >= smallIndex && i < bigIndex - 1) {
            return i + 1;
        }
        if (i >= bigIndex || (i == bigIndex - 1)) {
            return i + 2;
        }
        return i;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mHeadViewContainer.measure(MeasureSpec.makeMeasureSpec(mHeaderViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(3 * mHeaderViewHeight, MeasureSpec.EXACTLY));
        mFooterViewContainer.measure(MeasureSpec.makeMeasureSpec(mFooterViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mFooterViewHeight, MeasureSpec.EXACTLY));
        if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            mCurrentTargetOffsetTop = mOriginalOffsetTop = -mHeadViewContainer.getMeasuredHeight();
            updateListenerCallBack();
        }
        mHeaderViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mHeadViewContainer) {
                mHeaderViewIndex = index;
                break;
            }
        }
        mFooterViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mFooterViewContainer) {
                mFooterViewIndex = index;
                break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        int distance = mCurrentTargetOffsetTop + mHeadViewContainer.getMeasuredHeight();
        if (!targetScrollWithLayout) {
            // 判断标志位，如果目标View不跟随手指的滑动而滑动，将下拉偏移量设置为0
            distance = 0;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() + distance - pushDistance;// 根据偏移量distance更新
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        Log.d(LOG_TAG, "debug:onLayout childHeight = " + childHeight);
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);// 更新目标View的位置
        int headViewWidth = mHeadViewContainer.getMeasuredWidth();
        int headViewHeight = mHeadViewContainer.getMeasuredHeight();
        mHeadViewContainer.layout((width / 2 - headViewWidth / 2),
                mCurrentTargetOffsetTop, (width / 2 + headViewWidth / 2),
                mCurrentTargetOffsetTop + headViewHeight);// 更新头布局的位置
        int footViewWidth = mFooterViewContainer.getMeasuredWidth();
        int footViewHeight = mFooterViewContainer.getMeasuredHeight();
        mFooterViewContainer.layout((width / 2 - footViewWidth / 2), height
                - pushDistance, (width / 2 + footViewWidth / 2), height
                + footViewHeight - pushDistance);
    }

    /**
     * 主要判断是否应该拦截子View的事件<br>
     * 如果拦截，则交给自己的OnTouchEvent处理<br>
     * 否者，交给子View处理<br>
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || mRefreshing || mLoadMore || (!isChildScrollToTop() && !isChildScrollToBottom())) {
            // 如果子View可以滑动，不拦截事件，交给子View处理-下拉刷新
            // 或者子View没有滑动到底部不拦截事件-上拉加载更多
            return false;
        }

        // 下拉刷新判断
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mHeadViewContainer.getTop(), true);// 恢复HeaderView的初始位置
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;// 记录按下的位置

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                float yDiff;
                if (isChildScrollToBottom()) {
                    yDiff = mInitialMotionY - y;// 计算上拉距离
                    if (yDiff > mTouchSlop && !mIsBeingDragged) {// 判断是否下拉的距离足够
                        mIsBeingDragged = true;// 正在上拉
                    }
                } else {
                    yDiff = y - mInitialMotionY;// 计算下拉距离
                    if (yDiff > mTouchSlop && !mIsBeingDragged) {// 判断是否下拉的距离足够
                        mIsBeingDragged = true;// 正在下拉
                    }
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;// 如果正在拖动，则拦截子View的事件
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || (!isChildScrollToTop() && !isChildScrollToBottom())) {
            // 如果子View可以滑动，不拦截事件，交给子View处理
            return false;
        }

        if (isChildScrollToBottom()) {// 上拉加载更多
            return handlerPushTouchEvent(ev, action);
        } else {// 下拉刷新
            return handlerPullTouchEvent(ev, action);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
//        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        final int index = ev.findPointerIndex(activePointerId);
        if (index < 0) {
            return -1;
        }
        return ev.getY(index);
//        return MotionEventCompat.getY(ev, index);
    }

    /**
     * 更新回调
     */
    private void updateListenerCallBack() {
        int distance = mCurrentTargetOffsetTop + mHeadViewContainer.getHeight();
        if (mOnPullRefreshListener != null) {
            mOnPullRefreshListener.onPullDistance(distance);
        }
        if (usingDefaultHeader && isProgressEnable) {
            defaultProgressView.setPullDistance(distance);
        }
    }


    /**
     * 创建头布局的容器
     */
    private void createHeaderViewContainer() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                (int) (mHeaderViewHeight * 0.8),
                (int) (mHeaderViewHeight * 0.8));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mHeadViewContainer = new HeadViewContainer(getContext());
        mHeadViewContainer.setVisibility(View.GONE);
//        defaultProgressView.setVisibility(View.VISIBLE);
//        defaultProgressView.setOnDraw(false);
//        mHeadViewContainer.addView(defaultProgressView, layoutParams);
        addView(mHeadViewContainer);
    }

    /**
     * 添加底部布局
     */
    private void createFooterViewContainer() {
        mFooterViewContainer = new RelativeLayout(getContext());
        mFooterViewContainer.setVisibility(View.GONE);
        addView(mFooterViewContainer);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                //startScaleDownAnimation(mRefreshListener);
                animateOffsetToStartPosition(mCurrentTargetOffsetTop, mRefreshListener);
            }
        }
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mHeadViewContainer.setVisibility(View.VISIBLE);
        Animation mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mHeadViewContainer.setAnimationListener(listener);
        }
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mScaleAnimation);
    }

    private void setAnimationProgress(float progress) {
        if (!usingDefaultHeader) {
            progress = 1;
        }
        ViewCompat.setScaleX(mHeadViewContainer, progress);
        ViewCompat.setScaleY(mHeadViewContainer, progress);
    }

    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        Animation mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mHeadViewContainer.setAnimationListener(listener);
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mScaleDownAnimation);
    }

    /**
     * 确保mTarget不为空<br>
     * mTarget一般是可滑动的ScrollView,ListView,RecyclerView等
     */
    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeadViewContainer) && !child.equals(mFooterViewContainer)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    /**
     * 处理下拉刷新的Touch事件
     */
    private boolean handlerPullTouchEvent(MotionEvent ev, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
//                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

//                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float y = ev.getY(pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                if (mIsBeingDragged) {
                    float originalDragPercent = overScrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float extraOS = Math.abs(overScrollTop) - mTotalDragDistance;
                    float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop : mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;

                    int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
                    if (mHeadViewContainer.getVisibility() != View.VISIBLE) {
                        mHeadViewContainer.setVisibility(View.VISIBLE);
                    }
                    if (!mScale) {
                        ViewCompat.setScaleX(mHeadViewContainer, 1f);
                        ViewCompat.setScaleY(mHeadViewContainer, 1f);
                    }
                    if (usingDefaultHeader) {
                        float alpha = overScrollTop / mTotalDragDistance;
                        if (alpha >= 1.0f) {
                            alpha = 1.0f;
                        }
                        ViewCompat.setScaleX(defaultProgressView, alpha);
                        ViewCompat.setScaleY(defaultProgressView, alpha);
                        ViewCompat.setAlpha(defaultProgressView, alpha);
                    }
                    if (overScrollTop < mTotalDragDistance) {
                        if (mScale) {
                            setAnimationProgress(overScrollTop / mTotalDragDistance);
                        }
                        if (mOnPullRefreshListener != null) {
                            mOnPullRefreshListener.onPullEnable(false);
                        }
                    } else {
                        if (mOnPullRefreshListener != null) {
                            mOnPullRefreshListener.onPullEnable(true);
                        }
                    }
                    setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
//                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    }
                    return false;
                }
//                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float y = ev.getY(pointerIndex);
                final float overScrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overScrollTop > mTotalDragDistance) {
                    setRefreshing(true, true /* notify */);
                } else {
                    mRefreshing = false;
                    Animation.AnimationListener listener = null;
                    if (!mScale) {
                        listener = new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (!mScale) {
                                    startScaleDownAnimation(null);
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                        };
                    }
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    /**
     * 处理上拉加载更多的Touch事件
     */
    private boolean handlerPushTouchEvent(MotionEvent ev, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                Log.d(LOG_TAG, "debug:onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE: {
//                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
//                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float y = ev.getY(pointerIndex);
                final float overScrollBottom = (mInitialMotionY - y) * DRAG_RATE;
                if (mIsBeingDragged) {
                    pushDistance = (int) overScrollBottom;
                    updateFooterViewPosition();
                    if (mOnPushLoadMoreListener != null) {
                        mOnPushLoadMoreListener.onPushEnable(pushDistance >= mFooterViewHeight);
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
//                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                mActivePointerId = ev.getPointerId(index);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    }
                    return false;
                }
//                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float y = ev.getY(pointerIndex);
                final float overScrollBottom = (mInitialMotionY - y) * DRAG_RATE;// 松手是下拉的距离
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                if (overScrollBottom < mFooterViewHeight || mOnPushLoadMoreListener == null) {// 直接取消
                    pushDistance = 0;
                } else {// 下拉到mFooterViewHeight
                    pushDistance = mFooterViewHeight;
                }
                animatorFooterToBottom((int) overScrollBottom, pushDistance);
                return false;
            }
        }
        return true;
    }

    /**
     * 松手之后，使用动画将Footer从距离start变化到end
     */
    private void animatorFooterToBottom(int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // update
                pushDistance = (Integer) valueAnimator.getAnimatedValue();
                updateFooterViewPosition();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (end > 0 && mOnPushLoadMoreListener != null && mOperationListener != null) {
                    // start loading more
                    mLoadMore = true;
                    mOnPushLoadMoreListener.onLoadMore();

                    //TODO: 2017/4/14
                    mOperationListener.doLoad();
                } else {
                    resetTargetLayout();
                    mLoadMore = false;
                }
            }
        });
        valueAnimator.setInterpolator(mDecelerateInterpolator);
        valueAnimator.start();
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mHeadViewContainer.setAnimationListener(listener);
        }
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mScale) {
            startScaleDownReturnToStartAnimation(from, listener);
        } else {
            mFrom = from;
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                mHeadViewContainer.setAnimationListener(listener);
            }
            mHeadViewContainer.clearAnimation();
            mHeadViewContainer.startAnimation(mAnimateToStartPosition);
        }
        resetTargetLayoutDelay(ANIMATE_TO_START_DURATION);
    }

    /**
     * 下拉时，超过距离之后，弹回来的动画监听器
     */
    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isProgressEnable = false;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isProgressEnable = true;
            if (mRefreshing) {
                if (mNotify) {
                    if (usingDefaultHeader) {
                        defaultProgressView.setAlpha(1.0f);
                        defaultProgressView.setOnDraw(true);
                        new Thread(defaultProgressView).start();
                    }
                    if (mOnPullRefreshListener != null) {
                        mOnPullRefreshListener.onRefresh();

                        // TODO: 2017/4/14
                        if (mOperationListener != null) {
                            mOperationListener.doRefresh();
                        }
                    }
                }
            } else {
                mHeadViewContainer.setVisibility(View.GONE);
                if (mScale) {
                    setAnimationProgress(0);
                } else {
                    setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true);
                }
            }
            mCurrentTargetOffsetTop = mHeadViewContainer.getTop();
            updateListenerCallBack();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mHeadViewContainer.getTop();
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
        }
    };

    private Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mHeadViewContainer.getTop();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }

    private void startScaleDownReturnToStartAnimation(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mStartingScale = ViewCompat.getScaleX(mHeadViewContainer);
        Animation mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mHeadViewContainer.setAnimationListener(listener);
        }
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mScaleDownToStartAnimation);
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mHeadViewContainer.bringToFront();
        mHeadViewContainer.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mHeadViewContainer.getTop();
        if (requiresUpdate) {
            invalidate();
        }
        updateListenerCallBack();
    }

    /**
     * 修改底部布局的位置-敏感pushDistance
     */
    private void updateFooterViewPosition() {
        mFooterViewContainer.setVisibility(View.VISIBLE);
        mFooterViewContainer.bringToFront();
        //针对4.4及之前版本的兼容
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mFooterViewContainer.getParent().requestLayout();
        }
        mFooterViewContainer.offsetTopAndBottom(-pushDistance);
        updatePushDistanceListener();
    }

    private void updatePushDistanceListener() {
        if (mOnPushLoadMoreListener != null) {
            mOnPushLoadMoreListener.onPushDistance(pushDistance);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
//        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * 重置Target位置
     *
     * @param delay 延迟时间
     */
    public void resetTargetLayoutDelay(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resetTargetLayout();
            }
        }, delay);
    }

    /**
     * 重置Target的位置
     */
    public void resetTargetLayout() {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = child.getWidth() - getPaddingLeft() - getPaddingRight();
        final int childHeight = child.getHeight() - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        int headViewWidth = mHeadViewContainer.getMeasuredWidth();
        int headViewHeight = mHeadViewContainer.getMeasuredHeight();
        mHeadViewContainer.layout((width / 2 - headViewWidth / 2),
                -headViewHeight, (width / 2 + headViewWidth / 2), 0);// 更新头布局的位置
        int footViewWidth = mFooterViewContainer.getMeasuredWidth();
        int footViewHeight = mFooterViewContainer.getMeasuredHeight();
        mFooterViewContainer.layout((width / 2 - footViewWidth / 2), height,
                (width / 2 + footViewWidth / 2), height + footViewHeight);
    }

    /**
     * 判断目标View是否滑动到顶部-还能否继续滑动
     */
    public boolean isChildScrollToTop() {
        return !ViewCompat.canScrollVertically(mTarget, -1);
    }

    /**
     * 是否滑动到底部
     */
    public boolean isChildScrollToBottom() {
        // TODO: 2017/4/15  // return ViewCompat.canScrollVertically(mTarget, -1);
        if (isChildScrollToTop()) {
            return false;
        }
        if (mTarget instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mTarget;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount();
            if (layoutManager instanceof LinearLayoutManager && count > 0) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastItems = new int[2];
                staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastItems);
                int lastItem = Math.max(lastItems[0], lastItems[1]);
                if (lastItem == count - 1) {
                    return true;
                }
            }
            return false;
        } else if (mTarget instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) mTarget;
            int count = absListView.getAdapter().getCount();
            int firstPos = absListView.getFirstVisiblePosition();
            if (firstPos == 0 && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop()) {
                return false;
            }
            int lastPos = absListView.getLastVisiblePosition();
            if (lastPos > 0 && count > 0 && lastPos == count - 1) {
                View item = absListView.getChildAt(lastPos - firstPos);
                //重要修改！！！必须考虑SuperSwipeRefreshLayout本身的footer
                if (item.getBottom() <= getBottom()) {
                    return true;
                }
            }
            return false;
        } else if (mTarget instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mTarget;
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (view != null) {
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff == 0) {
                    return true;
                }
            }
        } else if (mTarget instanceof NestedScrollView) {
            NestedScrollView nestedScrollView = (NestedScrollView) mTarget;
            View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
            if (view != null) {
                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView.getScrollY()));
                if (diff == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设置头布局
     */
    public void setHeaderView(View child) {
        if (child == null || mHeadViewContainer == null) {
            return;
        }
        usingDefaultHeader = false;
        mHeadViewContainer.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mHeaderViewWidth, mHeaderViewHeight);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mHeadViewContainer.addView(child, layoutParams);

        if (child instanceof OnPullRefreshListener) {
            mOnPullRefreshListener = (OnPullRefreshListener) child;
        }
    }

    public void setFooterView(View child) {
        if (child == null || mFooterViewContainer == null) {
            return;
        }
        mFooterViewContainer.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mFooterViewWidth, mFooterViewHeight);
        mFooterViewContainer.addView(child, layoutParams);

        if (child instanceof OnPushLoadMoreListener) {
            mOnPushLoadMoreListener = (OnPushLoadMoreListener) child;
        }
    }

    /**
     * 设置子View是否跟谁手指的滑动而滑动
     */
    public void setTargetScrollWithLayout(boolean targetScrollWithLayout) {
        this.targetScrollWithLayout = targetScrollWithLayout;
    }

    /**
     * 判断子View是否跟随手指的滑动而滑动，默认跟随
     */
    public boolean isTargetScrollWithLayout() {
        return targetScrollWithLayout;
    }

    /**
     * Set the distance to trigger a sync in dips
     *
     * @param distance 距离
     */
    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }


    /**
     * 设置下拉刷新的接口
     */
    private void setOnPullRefreshListener(OnPullRefreshListener listener) {
        mOnPullRefreshListener = listener;
    }

    /**
     * 设置上拉加载更多的接口
     */
    private void setOnPushLoadMoreListener(OnPushLoadMoreListener onPushLoadMoreListener) {
        this.mOnPushLoadMoreListener = onPushLoadMoreListener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && !mRefreshing) {
            // scale and show
            mRefreshing = true;
            int endTarget;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            } else {
                endTarget = (int) mSpinnerFinalOffset;
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
            if (usingDefaultHeader) {
                defaultProgressView.setOnDraw(false);
            }
        }
    }


    /**
     * 设置停止加载
     */
    public void setLoadMore(boolean loadMore) {
        if (!loadMore && mLoadMore) {// 停止加载
            animatorFooterToBottom(mFooterViewHeight, 0);
        }
    }

    /**
     * 已经加载全部数据是需要设置
     */
    public void setLoadOver() {
        //已经全部加载完毕
        if (mOnPushLoadMoreListener != null) {
            ((DefaultFooter) mOnPushLoadMoreListener).setLoadOver(true);
            setLoadMore(false);
        }
    }

    /**
     * 判断当前操作是下拉刷新还是上拉加载
     *
     * @return 这里简单的用true 代表刷新，false 代表加载
     */
    public boolean isRefresh() {
        if (mRefreshing) {
            //用于重置上拉加载
            if (mOnPushLoadMoreListener != null) {
                ((DefaultFooter) mOnPushLoadMoreListener).setLoadOver(false);
            }
            setRefreshing(false);
            return true;
        }
        if (mLoadMore) {
            setLoadMore(false);
            return false;
        }
        return true;
    }

    public void setHeaderViewBackgroundColor(int color) {
        mHeadViewContainer.setBackgroundColor(color);
    }

    /**
     * 用于在上下拉的过程中发起网络请求等操作，
     * 在操作结束之后需要手动调用{@link RLLayout#isRefresh()}判断属于什么上拉还是下拉，最终结束整个操作，还原UI
     *
     * @param listener 需要操作的接口
     */
    public void sentRequest(OperationListener listener) {
        mOperationListener = listener;
    }




    /*可以在这里改变默认头部的UI */

    /**
     * 设置默认下拉刷新进度条的颜色
     */
    public void setDefaultCircleProgressColor(int color) {
        if (usingDefaultHeader) {
            defaultProgressView.setProgressColor(color);
        }
    }

    /**
     * 设置圆圈的背景色
     */
    public void setDefaultCircleBackgroundColor(int color) {
        if (usingDefaultHeader) {
            defaultProgressView.setCircleBackgroundColor(color);
        }
    }

    public void setDefaultCircleShadowColor(int color) {
        if (usingDefaultHeader) {
            defaultProgressView.setShadowColor(color);
        }
    }
}
