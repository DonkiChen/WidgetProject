package test.widgetproject.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.mvp.base.util.ResUtils;

import test.widgetproject.main.R;

/**
 * Created on 2018/4/13.
 *
 * @author ChenFanlin
 */

public class SwipeVerifyImage extends View {
    private Paint mPaint;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private RectF mSwipeBar = new RectF();
    private Path mPath = new Path();
    private int mSwipeBarHeight = 60;
    private int mBarImageSpace = 30;
    private int mBarStrokeWidth = 2;

    public SwipeVerifyImage(Context context) {
        this(context, null);
    }

    public SwipeVerifyImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeVerifyImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBarStrokeWidth);
        mDrawable = ResUtils.getDrawable(R.drawable.ic_launcher_background);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        Rect drawableBounds = mDrawable.getBounds();
        int halfStrokeWidth = mBarStrokeWidth / 2;
        mSwipeBar.set(halfStrokeWidth,
                drawableBounds.height() + mBarImageSpace + halfStrokeWidth,
                drawableBounds.width() - halfStrokeWidth,
                drawableBounds.height() + mBarImageSpace + mSwipeBarHeight - halfStrokeWidth);
        RectF swipeSlider = new RectF();
        swipeSlider.set(mSwipeBar.left, mSwipeBar.top, mSwipeBar.height(), mSwipeBar.bottom);
        //滑块  正方形 里面有个箭头
        mPath.addRect(swipeSlider, Path.Direction.CCW);
        mPath.moveTo(swipeSlider.left + swipeSlider.height() / 4, swipeSlider.top + swipeSlider.height() / 2);
        mPath.rLineTo(swipeSlider.width() / 2, 0);
        mPath.rMoveTo(-swipeSlider.width() / 6, -swipeSlider.width() / 6);
        mPath.rLineTo(swipeSlider.width() / 6, swipeSlider.height() / 6);
        mPath.rLineTo(-swipeSlider.width() / 6, swipeSlider.height() / 6);
        mPath.rewind();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
        //外面的框
        mPaint.setColor(Color.RED);
        canvas.drawRoundRect(mSwipeBar, mBarStrokeWidth, mBarStrokeWidth, mPaint);

        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = Math.max(widthSize, mBitmap.getWidth());
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.max(heightSize, mBitmap.getHeight());
        }
    }

}
