package test.widgetproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.Gravity;

import test.widgetproject.main.R;

/**
 * Created on 2018/4/25.
 *
 * @author ChenFanlin
 */

public class LabelView extends android.support.v7.widget.AppCompatTextView {

    private Paint mPaint;
    private Path mPath;
    private int mRotationDegrees = 0;
    private LabelGravity mLabelGravity;
    private int mRotationX, mRotationY;
    private int mLabelBackgroundColor;
    private float mOffset;
    private PorterDuffXfermode mPorterDuffXfermodeATop = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);

    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayerType(LAYER_TYPE_NONE, null);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelView);
            mLabelGravity = LabelGravity.fromValue(typedArray.getInt(R.styleable.LabelView_labelGravity, 0));
            mLabelBackgroundColor = typedArray.getColor(R.styleable.LabelView_labelBackgroundColor, Color.WHITE);
            typedArray.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mLabelBackgroundColor);
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        makePath();
        computeRotation();
        computeOffset();

        //离屏缓冲,确保Xfermode达到预期效果
        int saved1 = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        //对canvas的旋转和偏移,让文字在预期位置
        int saved2 = canvas.save();
        canvas.rotate(mRotationDegrees, mRotationX, mRotationY);
        canvas.translate(mOffset, 0);
        super.onDraw(canvas);
        canvas.restoreToCount(saved2);
        mPaint.setXfermode(mPorterDuffXfermodeATop);
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saved1);
    }

    // TODO: 2018/4/25 计算最小高度,宽度
    // TODO: 2018/4/25 添加长宽比,宽高自适应
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void computeOffset() {
        int w = getWidth();
        float newWidth = (float) (w / Math.cos(Math.toRadians(mRotationDegrees)));
        switch (mLabelGravity) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
                mOffset = (newWidth - w) / 2;
                break;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                mOffset = -(newWidth - w) / 2;
                break;
        }
    }

    private void makePath() {
        int w = getWidth();
        int h = getHeight();
        mPath.reset();
        switch (mLabelGravity) {
            case TOP_LEFT: {
                mPath.moveTo(0, 0);
                mPath.rLineTo(0, h);//↓
                mPath.rLineTo(w, -h);//↗
                break;
            }
            case TOP_RIGHT: {
                mPath.moveTo(w, 0);
                mPath.rLineTo(0, h);//↓
                mPath.rLineTo(-w, -h);//↖
                break;
            }
            case BOTTOM_LEFT: {
                mPath.moveTo(0, h);
                mPath.rLineTo(0, -h);//↑
                mPath.rLineTo(w, h);//↘
                break;
            }
            case BOTTOM_RIGHT: {
                mPath.moveTo(w, h);
                mPath.rLineTo(-w, 0);//←
                mPath.rLineTo(w, -h);//↗
                break;
            }
        }
        mPath.close();
    }

    private void computeRotation() {
        int w = getWidth();
        int h = getHeight();
        int degrees = (int) Math.toDegrees(Math.atan(h * 1.0 / w));
        switch (mLabelGravity) {
            case TOP_LEFT:
                mRotationDegrees = -degrees;
                mRotationX = 0;
                mRotationY = h;
                setGravity(Gravity.BOTTOM | Gravity.CENTER);
                break;
            case TOP_RIGHT:
                mRotationDegrees = degrees;
                mRotationX = w;
                mRotationY = h;
                setGravity(Gravity.BOTTOM | Gravity.CENTER);
                break;
            case BOTTOM_LEFT:
                mRotationDegrees = degrees;
                mRotationX = 0;
                mRotationY = 0;
                setGravity(Gravity.TOP | Gravity.CENTER);
                break;
            case BOTTOM_RIGHT:
                mRotationDegrees = -degrees;
                mRotationX = w;
                mRotationY = 0;
                setGravity(Gravity.TOP | Gravity.CENTER);
                break;
        }
    }

    public void setRotationDegrees(int rotationDegrees) {
        mRotationDegrees = rotationDegrees;
        postInvalidate();
    }

    public void setLabelGravity(LabelGravity labelGravity) {
        mLabelGravity = labelGravity;
        invalidate();
    }

    public enum LabelGravity {
        TOP_LEFT(0),
        TOP_RIGHT(1),
        BOTTOM_LEFT(2),
        BOTTOM_RIGHT(3);
        private int mValue;

        LabelGravity(int value) {
            mValue = value;
        }

        public static LabelGravity fromValue(int value) {
            switch (value) {
                case 0:
                default:
                    return TOP_LEFT;
                case 1:
                    return TOP_RIGHT;
                case 2:
                    return BOTTOM_LEFT;
                case 3:
                    return BOTTOM_RIGHT;
            }
        }

        public int getValue() {
            return mValue;
        }
    }
}
