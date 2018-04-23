package test.widgetproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mvp.base.util.DisplayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2018/4/23.
 *
 * @author ChenFanlin
 */

public class SlideBar extends View {

    private Paint mPaint;
    private int mTextSize = DisplayUtils.sp2px(14);
    private List<String> mItems = new ArrayList<>();

    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            for (String item : mItems) {
                widthSize = (int) Math.max(mPaint.measureText(item), widthSize);
            }
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mItems.size() * mPaint.getFontMetricsInt().leading;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    public void addItems(String... items) {
        Collections.addAll(mItems, items);
    }
}
