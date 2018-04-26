package test.widgetproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mvp.base.util.DisplayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.widgetproject.main.R;
import test.widgetproject.util.MathUtils;

/**
 * Created on 2018/4/23.
 *
 * @author ChenFanlin
 */

public class SlideBar extends View {
    private static final String TAG = SlideBar.class.getSimpleName();

    private Paint mPaint;
    private List<String> mItems = new ArrayList<>();
    private float mItemHeight = 0;
    private Paint.FontMetrics mFontMetrics;
    private float mMaxItemWidth = 0;
    //用于存储item的baseline的x,y
    private HashMap<String, Float[]> mItemLocationMap = new HashMap<>();
    private String mCurrentItem;
    private OnBarTouchListener mOnBarTouchListener;
    private int mNormalTextColor = Color.BLACK;
    private int mNormalTextSize = DisplayUtils.sp2px(14);
    private int mSelectedTextColor = Color.RED;
    private int mSelectedTextSize = mNormalTextSize;

    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlideBar);
            mNormalTextColor = typedArray.getColor(R.styleable.SlideBar_barNormalTextColor, mNormalTextColor);
            mNormalTextSize = typedArray.getDimensionPixelSize(R.styleable.SlideBar_barNormalTextSize, mNormalTextSize);
            mSelectedTextColor = typedArray.getColor(R.styleable.SlideBar_barSelectedTextColor, mSelectedTextColor);
            mSelectedTextSize = typedArray.getDimensionPixelSize(R.styleable.SlideBar_barSelectedTextSize, mNormalTextSize);
            typedArray.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(Math.max(mNormalTextSize, mSelectedTextSize));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String item = findItemByTouch(event.getY());
        if (item != null && !TextUtils.equals(item, mCurrentItem)) {
            setSelected(item, true);
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mOnBarTouchListener != null) {
                mOnBarTouchListener.onTouchUp();
            }
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mItemHeight = (h - getPaddingTop() - getPaddingBottom()) * 1.0f / mItems.size();
        if (mFontMetrics == null) {
            mFontMetrics = mPaint.getFontMetrics();
        } else {
            mPaint.getFontMetrics(mFontMetrics);
        }
        mItemLocationMap.clear();
        //先算出baseline位置,提高绘制性能
        //y居中
        float textY = (mItemHeight - mFontMetrics.descent - mFontMetrics.ascent) / 2 + getPaddingTop();
        float textX;
        int count = mItems.size();
        for (int i = 0; i < count; i++) {
            String item = mItems.get(i);
            Float[] location = new Float[2];
            mItemLocationMap.put(item, location);
            float needWidth = mPaint.measureText(item);
            //长短不同的字符串的x居中
            textX = getPaddingLeft() + (mMaxItemWidth - needWidth) / 2;
            Log.d(TAG, "onSizeChanged: mMaxItemWidth = " + mMaxItemWidth + " needWidth =" + needWidth);
            location[0] = textX;
            location[1] = textY;
            textY += mItemHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Map.Entry<String, Float[]> entry : mItemLocationMap.entrySet()) {
            mPaint.setColor(TextUtils.equals(entry.getKey(), mCurrentItem) ? mSelectedTextColor : mNormalTextColor);
            mPaint.setTextSize(TextUtils.equals(entry.getKey(), mCurrentItem) ? mSelectedTextSize : mNormalTextSize);
            Float[] value = entry.getValue();
            canvas.drawText(entry.getKey(), value[0], value[1], mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int maxWidth = -1;
        if (widthMode == MeasureSpec.AT_MOST) {
            for (String item : mItems) {
                maxWidth = (int) Math.ceil(Math.max(mPaint.measureText(item), maxWidth));
            }
            mMaxItemWidth = maxWidth;
            widthSize = maxWidth + getPaddingLeft() + getPaddingRight();
            Log.d(TAG, "onMeasure: widthSize = " + widthSize + " maxWidth = " + maxWidth);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) Math.ceil(mItems.size() * mPaint.getFontSpacing())
                    + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private String findItemByTouch(float y) {
        float baseline = (mItemHeight - mFontMetrics.descent - mFontMetrics.ascent) / 2;
        for (Map.Entry<String, Float[]> entry : mItemLocationMap.entrySet()) {
            Float[] value = entry.getValue();
            // value[1]存的是baseline
            // 转换成item的top
            if (MathUtils.between(y, value[1] - baseline, value[1] - baseline + mItemHeight)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addItems(String... items) {
        Collections.addAll(mItems, items);
        requestLayout();
        setDefaultSelect();
    }

    public void addItems(List<String> items) {
        mItems.addAll(items);
        requestLayout();
        setDefaultSelect();
    }

    private void setDefaultSelect() {
        if (mItems.size() > 0) {
            setSelected(mItems.get(0), false);
        }
    }

    public void clear() {
        mItems.clear();
        requestLayout();
    }

    public List<String> getItems() {
        return mItems;
    }

    public void setSelected(String item, boolean invokeListener) {
        if (mItemLocationMap.isEmpty()) {
            mCurrentItem = item;
            return;
        }
        if (TextUtils.equals(mCurrentItem, item)) {
            return;
        }

        if (!mItemLocationMap.containsKey(item)) {
            return;
        }

        if (mItemLocationMap.containsKey(mCurrentItem)) {
            int oldTextY = (int) Math.floor(mItemLocationMap.get(mCurrentItem)[1]);
            postInvalidate(0, oldTextY, getMeasuredWidth(), (int) (oldTextY + mItemHeight));
        }
        int newTextY = (int) Math.floor(mItemLocationMap.get(item)[1]);
        postInvalidate(0, newTextY, getMeasuredWidth(), (int) (newTextY + mItemHeight));
        mCurrentItem = item;
        if (mOnBarTouchListener != null && invokeListener) {
            mOnBarTouchListener.onTouchDown(mItems.indexOf(mCurrentItem), mCurrentItem);
        }
        Log.d(TAG, "setSelected: " + item);
    }

    public void setOnBarTouchListener(OnBarTouchListener onBarTouchListener) {
        mOnBarTouchListener = onBarTouchListener;
    }

    public interface OnBarTouchListener {
        void onTouchDown(int index, String item);

        void onTouchUp();
    }
}
