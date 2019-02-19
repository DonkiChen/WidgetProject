package test.widgetproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * Created on 2018/5/17.
 *
 * @author ChenFanlin
 */
public class RoundCornerConstraintLayout extends ConstraintLayout {
    private Paint mCornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mCornerRadius = 20;
    private RectF mRectF = new RectF();
    private PorterDuffXfermode mPorterDuffXfermodeDstIn = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Path mPath = new Path();

    public RoundCornerConstraintLayout(Context context) {
        this(context, null);
    }

    public RoundCornerConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerConstraintLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCornerPaint.setColor(Color.BLACK);
        mCornerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, h);
        mPath.reset();
        mPath.addRoundRect(mRectF, mCornerRadius, mCornerRadius, Path.Direction.CW);
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
            int saved = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            drawRoundPath(canvas, saved);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    private void drawRoundPath(Canvas canvas, int saveCount) {
        mCornerPaint.setXfermode(mPorterDuffXfermodeDstIn);
        canvas.drawPath(mPath, mCornerPaint);
        mCornerPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }
}
