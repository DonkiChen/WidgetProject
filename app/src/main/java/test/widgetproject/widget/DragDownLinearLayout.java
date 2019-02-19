package test.widgetproject.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import test.widgetproject.main.R;

public class DragDownLinearLayout extends LinearLayout implements NestedScrollingParent2 {

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private float mDragY = 0;
    private float mDragDismissDistance;
    private float mDragDismissRatio = 0.5f;
    private OnDragListener mOnDragListener;
    private boolean mDragging = false;
    private ObjectAnimator mTranslationAnimator;

    public DragDownLinearLayout(Context context) {
        this(context, null);
    }

    public DragDownLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDownLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DragDownLinearLayout);
        mDragDismissDistance = typedArray.getDimension(R.styleable.DragDownLinearLayout_ddllDismissDistance, 0);
        mDragDismissRatio = typedArray.getFloat(R.styleable.DragDownLinearLayout_ddllDismissRatio, 0.5f);
        typedArray.recycle();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && type == ViewCompat.TYPE_TOUCH;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean consumeY = mDragging && dy > 0 || mDragY > 0 && dy > 0;
        if (consumeY) {
            consumed[1] = dy;
            drag(-dy);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDragDismissDistance == 0) {
            mDragDismissDistance = mDragDismissRatio * h;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        drag(-dyUnconsumed);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mTranslationAnimator != null) {
            mTranslationAnimator.cancel();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedScrollingParentHelper.onStopNestedScroll(target, type);
        dragFinished();
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    private void drag(float deltaY) {
        if (deltaY == 0) {
            return;
        }
        mDragY += deltaY;
        if (mDragY > 0) {
            mDragging = true;
            setTranslationY(mDragY);
            if (mOnDragListener != null) {
                mOnDragListener.onDrag(mDragY);
            }
        } else {
            mDragY = 0;
            mDragging = false;
        }
    }

    private void dragFinished() {
        if (mDragY == 0 || mDragY == getHeight() || !mDragging) {
            return;
        }
        final boolean shouldDismiss = mDragY >= mDragDismissDistance;
        final float finalTranslationY = shouldDismiss ? getHeight() : 0;

        mTranslationAnimator = ObjectAnimator.ofFloat(this, "translationY",
                getTranslationY(), finalTranslationY);

        mTranslationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDragY = getTranslationY();
            }
        });
        mTranslationAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mTranslationAnimator.addListener(
                new AnimatorListenerAdapter() {
                    private boolean isCanceled = false;

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isCanceled && shouldDismiss && mOnDragListener != null) {
                            mOnDragListener.onDragOutOfDistance();
                        }
                        isCanceled = false;
                        mDragging = false;
                        mTranslationAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        isCanceled = true;
                    }
                }
        );
        mTranslationAnimator.start();
    }

    public void setOnDragListener(OnDragListener onDragListener) {
        mOnDragListener = onDragListener;
    }

    public interface OnDragListener {
        void onDrag(float dragY);

        void onDragOutOfDistance();
    }
}
