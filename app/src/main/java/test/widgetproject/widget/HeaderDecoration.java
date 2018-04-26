package test.widgetproject.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mvp.base.util.DisplayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2018/4/24.
 *
 * @author ChenFanlin
 */

public class HeaderDecoration extends RecyclerView.ItemDecoration {
    /**
     * key:header,value:第一次出现的position
     */
    private Map<String, Integer> mHeaderMap = new HashMap<>();
    private Paint mPaint = new Paint();
    private int mHeaderHeight = DisplayUtils.dp2px(36);
    private int mHeaderBackgroundColor = Color.parseColor("#E2E2E2");
    private int mHeaderTextColor = Color.BLACK;
    private int mHeaderTextSize = DisplayUtils.sp2px(14);
    private int mHeaderTextXOffset = 0;

    public HeaderDecoration(Map<String, Integer> headerMap) {
        mHeaderMap = headerMap;
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mHeaderTextSize);
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        mHeaderHeight = headerHeight;
    }

    public int getHeaderBackgroundColor() {
        return mHeaderBackgroundColor;
    }

    public void setHeaderBackgroundColor(@ColorInt int headerBackgroundColor) {
        mHeaderBackgroundColor = headerBackgroundColor;
    }

    public int getHeaderTextColor() {
        return mHeaderTextColor;
    }

    public void setHeaderTextColor(@ColorInt int headerTextColor) {
        mHeaderTextColor = headerTextColor;
    }

    public int getHeaderTextSize() {
        return mHeaderTextSize;
    }

    public void setHeaderTextSize(int headerTextSize) {
        mHeaderTextSize = headerTextSize;
    }

    public int getHeaderTextXOffset() {
        return mHeaderTextXOffset;
    }

    public void setHeaderTextXOffset(int headerTextXOffset) {
        mHeaderTextXOffset = headerTextXOffset;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            if (mHeaderMap.containsValue(position)) {
                mPaint.setColor(mHeaderBackgroundColor);
                c.drawRect(view.getLeft(),
                        view.getTop() - mHeaderHeight,
                        view.getRight(),
                        view.getTop(), mPaint);
                String letter = null;
                for (Map.Entry<String, Integer> entry : mHeaderMap.entrySet()) {
                    if (entry.getValue() == position) {
                        letter = entry.getKey();
                        break;
                    }
                }
                if (letter == null) {
                    continue;
                }
                mPaint.setColor(mHeaderTextColor);
                // 垂直居中的baseline计算方法: ascent 和 descent 都是相对于 baseline, 所以得出等式
                // (Ascent, Descent为相对于view坐标)
                // 1. header.centerY = (Ascent + Descent)/2
                // 2. Ascent = baseline + ascent
                // 3. Descent = baseline + descent
                // => header.centerY = (baseline + ascent + baseline + descent) / 2
                // => baseline = header.centerY - (ascent + descent) / 2
                // 4. header.centerY = (view.getTop() - mHeaderHeight + view.getTop()) / 2
                // => baseline = view.getTop() - (ascent + descent + mHeaderHeight) / 2

                float textY = view.getTop() - (mPaint.ascent() + mPaint.descent() + mHeaderHeight) / 2;
                c.drawText(letter, mHeaderTextXOffset, textY, mPaint);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (mHeaderMap.containsValue(position)) {
            outRect.top = mHeaderHeight;
        }
    }
}
