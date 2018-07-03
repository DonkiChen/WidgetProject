package test.widgetproject.widget;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import com.mvp.base.util.ToastUtils;

/**
 * Created on 2018/6/8.
 *
 * @author ChenFanlin
 */
public class CollapseTextView extends android.support.v7.widget.AppCompatTextView {
    private static final int STATUS_COLLAPSED = 0x0;
    private static final int STATUS_EXPANDED = 0x1;

    private static final String ELLIPSIZE = "…";
    private static final String READ_MORE = "全文";
    private static final String COLLAPSE = "收起";
    private static final String SUFFIX = ELLIPSIZE + READ_MORE;

    private int mCollapseLineCount = 2;
    private int mStatus = STATUS_COLLAPSED;
    private CharSequence mOriginalText;
    private CharSequence mCollapsedText;
    private boolean mIsTextCollapsed = false;
    private ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            ToastUtils.showShortSafe("查看全文");
            toggle();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    };

    public CollapseTextView(Context context) {
        this(context, null);
    }

    public CollapseTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public CollapseTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 23) {
            //控制中英文混排
            setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
        }
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void setText(final CharSequence text, BufferType type) {
        if (mStatus == STATUS_EXPANDED) {
            mOriginalText = text;
            super.setText(generateExpandedText(text), type);
        } else {
            super.setText(text, type);
        }
        if (mStatus == STATUS_COLLAPSED && !mIsTextCollapsed) {
            mOriginalText = text;
            post(new Runnable() {
                @Override
                public void run() {
                    mCollapsedText = generateCollapseText(text);
                    mIsTextCollapsed = true;
                    setText(mCollapsedText);
                }
            });
        }
    }

    public void setCollapseLineCount(int collapseLineCount) {
        if (mCollapseLineCount != collapseLineCount) {
            mCollapseLineCount = collapseLineCount;
            mStatus = STATUS_COLLAPSED;
            clearCollapse();
        }
    }

    public void toggle() {
        mStatus = ~mStatus & 1;
        if (mStatus == STATUS_COLLAPSED) {
            mIsTextCollapsed = !TextUtils.isEmpty(mCollapsedText);
            setText(mIsTextCollapsed ? mCollapsedText : mOriginalText);
        } else {
           clearCollapse();
        }
    }

    private void clearCollapse(){
        mIsTextCollapsed = false;
        mCollapsedText = null;
        setText(mOriginalText);
    }

    private CharSequence generateCollapseText(CharSequence text) {
        // TODO: 2018/6/8 思路: 最后一行的宽度+后缀的宽度如果<屏幕宽度,直接在最后拼接
        // TODO: 否则:最后一行宽度 - 后缀宽度,利用breakText中断文字,然后再拼接
        // TODO 如果行末是 span,整个去掉
        Layout layout = getLayout();
        layout.getLineEnd(layout.getLineCount() - 1);
        //需要显示 后缀 的情况
        if (mStatus == STATUS_COLLAPSED && getLineCount() >= mCollapseLineCount) {
            int lineStart = layout.getLineStart(mCollapseLineCount - 1);
            int lineEnd = layout.getLineEnd(mCollapseLineCount - 1);
            float lineWidth = layout.getLineWidth(mCollapseLineCount - 1);
            float suffixWidth = getPaint().measureText(SUFFIX);
            int breakIndex = lineEnd - lineStart;
            if (lineWidth + suffixWidth > getRealWidth()) {
                //最后一行文本的最大宽度
                float textMaxWidth = lineWidth - suffixWidth;
                //目前行 中断的位置
                breakIndex = getPaint().breakText(text, lineStart, lineEnd,
                        true, textMaxWidth, null);
            }
            int textEndIndex = breakIndex + lineStart;
            SpannableStringBuilder sb = new SpannableStringBuilder(text, 0, textEndIndex);
            sb.append(SUFFIX);
            sb.setSpan(mClickableSpan, textEndIndex + ELLIPSIZE.length(),
                    textEndIndex + SUFFIX.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
        return text;
    }

    private CharSequence generateExpandedText(CharSequence text) {
        int textLength = text.length();
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.append(COLLAPSE);
        sb.setSpan(mClickableSpan, textLength, textLength + COLLAPSE.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private int getRealWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight() - getCompoundPaddingLeft() - getCompoundPaddingRight();
    }
}
