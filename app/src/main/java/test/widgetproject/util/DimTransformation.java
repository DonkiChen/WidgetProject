package test.widgetproject.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.nio.charset.Charset;
import java.security.MessageDigest;

import jp.wasabeef.glide.transformations.BitmapTransformation;

public class DimTransformation extends BitmapTransformation {
    private static final String ID = DimTransformation.class.getSimpleName();
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));
    private float mDimValue;
    private Paint mPaint = new Paint();

    public DimTransformation(float dimValue) {
        mDimValue = dimValue;
    }

    @Override
    public String key() {
        return "DimTransformation" + mDimValue;
    }

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(toTransform, 0, 0, mPaint);
        canvas.drawColor(Color.argb((int) (mDimValue * 255), 0, 0, 0));
        return bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DimTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}