package test.widgetproject.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.mvp.base.util.ToastUtils;

import java.util.Random;

/**
 * Created on 2018/4/18.
 *
 * @author ChenFanlin
 */

public class VerifyImage extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = VerifyImage.class.getSimpleName();

    private Paint mMaskPaint;
    private Paint mMaskShadowPaint;
    private Path mMaskPath = new Path();
    private Path mCirclePath = new Path();
    private int mOffsetX = 0, mOffsetY = 0, mDragOffset, mBlurMaskRadius = 10;
    private int mMaskTop, mMaskBottom, mMaskLeft, mMaskRight;
    private int mViewWidth, mViewHeight;
    private SeekBar mSeekBar;
    private int mDownX;

    private Bitmap mMaskBitmap, mMaskShadowBitmap;

    public VerifyImage(Context context) {
        this(context, null);
    }

    public VerifyImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mMaskShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mMaskShadowPaint.setColor(Color.BLACK);
        mMaskShadowPaint.setMaskFilter(new BlurMaskFilter(mBlurMaskRadius, BlurMaskFilter.Blur.SOLID));

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        Log.d(TAG, "onSizeChanged: ");
        post(new Runnable() {
            @Override
            public void run() {
                createMaskPath(true);
                invalidate();
//                setDragOffset(mOffsetX - mBlurMaskRadius);
            }
        });
    }

    private void createMaskPath(boolean sizeChanged) {
        int size = Math.min(mViewWidth, mViewHeight) / 10;
        int angle = 120;
        int radius = (int) (size / 6 / Math.sin(Math.toRadians(angle / 2)));
        //圆心与边的距离
        int circleCenterDistance = (int) (Math.cos(Math.toRadians(angle / 2)) * radius);
        if (sizeChanged) {
            mMaskPath.addRect(0, 0, size, size, Path.Direction.CW);
            mCirclePath.addCircle(circleCenterDistance, 0, radius, Path.Direction.CW);
            mCirclePath.offset(0, size / 2);
            mMaskPath.op(mCirclePath, Path.Op.DIFFERENCE);
            mCirclePath.offset(size, 0);
            mMaskPath.op(mCirclePath, Path.Op.UNION);
            mCirclePath.offset(-size / 2 - circleCenterDistance, -size / 2 - circleCenterDistance);
            mMaskPath.op(mCirclePath, Path.Op.UNION);
            //给上方圆留出位置
            mMaskPath.offset(0, circleCenterDistance + radius);

        }
        //回到初始位置
        mMaskPath.offset(-mOffsetX, -mOffsetY);
        //随机位置
        int range[] = getAvailableMaskRange();
        Random random = new Random();
        mOffsetX = random.nextInt(range[2] - range[0] + 1 - size) + range[0];
        mOffsetY = random.nextInt(range[3] - range[1] + 1 - size) + range[1];
        mMaskPath.offset(mOffsetX, mOffsetY);

        mMaskTop = mOffsetY + circleCenterDistance + radius;
        mMaskBottom = mMaskTop + size;
        mMaskLeft = mOffsetX;
        mMaskRight = mMaskLeft + size;

        createMask();
    }

    /**
     * 获取 Mask 可取的范围
     *
     * @return float[] left,top,right,bottom
     */
    private int[] getAvailableMaskRange() {
        int[] range = new int[4];
        range[0] = getMeasuredWidth() / 4;
        range[1] = getMeasuredHeight() / 4;
        range[2] = getMeasuredWidth() * 3 / 4;
        range[3] = getMeasuredHeight() * 3 / 4;
        return range;
    }

    private void createMask() {
        mMaskBitmap = getMaskBitmap(((BitmapDrawable) getDrawable()).getBitmap(), mMaskPath);
        //滑块阴影
        mMaskShadowBitmap = mMaskBitmap.extractAlpha();
        //拖动的位移重置
        mDragOffset = 0;
    }

    private Bitmap getMaskBitmap(Bitmap bitmap, Path maskPath) {
        //以控件宽高 create一块bitmap
        Bitmap tempBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //把创建的bitmap作为画板
        Canvas canvas = new Canvas(tempBitmap);
        //抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        //通过clipPath方法
        canvas.clipPath(maskPath);
        canvas.drawBitmap(bitmap, getImageMatrix(), mMaskPaint);
        //通过Xfermode方法
//        canvas.drawPath(maskPath, mMaskPaint);
//        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, getImageMatrix(), mMaskPaint);
//        mMaskPaint.setXfermode(null);
        return tempBitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //BlurMaskFilter 是根据 Alpha 通道的边界来计算模糊的，如果用它对图片进行处理，没有任何效果。
        if (mMaskBitmap != null) {
            canvas.drawBitmap(mMaskShadowBitmap, 0, 0, mMaskShadowPaint);
            canvas.drawBitmap(mMaskShadowBitmap, -mOffsetX + mBlurMaskRadius + mDragOffset, 0, mMaskShadowPaint);
            canvas.drawBitmap(mMaskBitmap, -mOffsetX + mBlurMaskRadius + mDragOffset, 0, null);
        }

    }

    private void reset() {
        createMaskPath(false);
        invalidate();
        setDragOffset(0);
        mSeekBar.post(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress(0);
            }
        });
    }

    private boolean isMatch() {
        return Math.abs((-mOffsetX + mBlurMaskRadius + mDragOffset)) <= 10;
    }

    public void setDragOffset(int dragOffset) {
        mDragOffset = dragOffset;
        postInvalidate(0, mMaskTop, mViewWidth, mMaskBottom);
    }

    public void bindSeekBar(SeekBar seekBar) {
        mSeekBar = seekBar;
//        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (!isMatch()) {
                            reset();
                            ToastUtils.showShortSafe("验证失败");
                        } else {
                            ToastUtils.showShortSafe("验证成功");
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // FIXME: 2018/4/20 左右会超出范围
                        int x;
                        if (event.getX() < 0) {
                            x = 0;
                        } else if (event.getX() > getWidth()) {
                            x = getWidth();
                        } else {
                            x = (int) event.getX();
                        }
                        Log.d(TAG, "onTouch: " + event.getX() + "`" + getWidth());
                        setDragOffset(x);
                        break;
                }
                return false;
            }
        });
    }
}
