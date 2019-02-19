package test.widgetproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RoundFrameLayout extends FrameLayout {
    private static final String TAG = RoundFrameLayout.class.getSimpleName();

    private Paint mPaint = new Paint();
    private PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private float mCenterX;
    private float mCenterY;
    private float mRadius;
    private Path mPath = new Path();
    private RectF mRectF = new RectF();

    public RoundFrameLayout(Context context) {
        this(context, null);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = (w - getPaddingLeft() - getPaddingRight()) / 2F;
        mCenterY = (h - getPaddingTop() - getPaddingBottom()) / 2F;

        mRadius = Math.min(mCenterX, mCenterY);
        mPath.reset();
        mPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);
        //mPath.addRect(0, 0, mCenterX, mCenterY, Path.Direction.CW);

        mRectF.set(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        int saved = canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        drawRoundPath(canvas, saved);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        if (getBackground() == null) {
            //在没有背景时,不会调用draw,所以在这绘制圆角
            int saved = canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            drawRoundPath(canvas, saved);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    private void drawRoundPath(Canvas canvas, int saveCount) {
        mPaint.setXfermode(mXfermode);
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }
}
