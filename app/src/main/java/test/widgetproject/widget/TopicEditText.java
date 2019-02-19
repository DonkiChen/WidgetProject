package test.widgetproject.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.mvp.base.util.KeyboardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import test.widgetproject.widget.span.TopicSpan;

public class TopicEditText extends android.support.v7.widget.AppCompatEditText {
    private List<TopicSpan.TopicObject> topicList = new ArrayList<>();//维护话题列表
    private boolean spanMustInFirst = false;//回复框中span必须要在最前面
    private boolean canSelectedWhenMustInFirst = false;

    public TopicEditText(Context context) {
        super(context);
        init();
    }

    public TopicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopicEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnKeyListener(null);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (TopicSpan topicSpan : s.getSpans(0, length(), TopicSpan.class)) {
                    if (!topicList.contains(topicSpan.getTopic())) {
                        topicList.add(topicSpan.getTopic());
                    }
                }
            }
        });
    }

    /**
     * 插入话题
     *
     * @param topic 话题内容
     */
    public void insertTopic(TopicSpan.TopicObject topic) {
        topicList.add(topic);
        SpannableStringBuilder span = new SpannableStringBuilder(topic.getPreRule() + topic.getContent() + topic.getSufRule());
        TopicSpan foreSpan = new TopicSpan(Color.RED, topic);
        span.setSpan(foreSpan, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (spanMustInFirst) {
            setText(span.append(getText()));
        } else {
            if (start == end) {
                getText().append(span);
            } else {
                for (TopicSpan selectionTopic : getSelectionTopic()) {
                    //话题被替换时 移除
                    topicList.remove(selectionTopic.getTopic());
                }
                getText().replace(start, end, span);
            }
        }
        setSelection(length());
    }

    /**
     * 获取话题文本
     *
     * @return 话题文本列表
     */
    private List<TopicSpan> getTopicSpans() {
        List<TopicSpan> topics = new ArrayList<>();
        Collections.addAll(topics, getEditableText().getSpans(0, getEditableText().length(), TopicSpan.class));
        return topics;
    }

    /**
     * 获取选中的 话题列表
     *
     * @return 选中的话题列表
     */
    private List<TopicSpan> getSelectionTopic() {
        Editable temp = getEditableText();
        List<TopicSpan> selectionTopic = new ArrayList<>();
        if (topicList.size() != 0) {
            for (TopicSpan span : getTopicSpans()) {
                if (temp.getSpanStart(span) > getSelectionStart() && temp.getSpanEnd(span) < getSelectionEnd()) {
                    selectionTopic.add(span);
                }
            }
        }

        return selectionTopic;
    }

    public String getPureText() {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(getEditableText());
        for (TopicSpan item : getTopicSpans()) {
            stringBuilder.replace(stringBuilder.getSpanStart(item), stringBuilder.getSpanEnd(item), "");
        }
        return stringBuilder.toString().trim();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (topicList == null || topicList.size() == 0) {
            return;
        }
        Editable temp = getEditableText();
        if (!spanMustInFirst || canSelectedWhenMustInFirst) {
            //span不一定要在首位 或者 在首位可以被删除时
            //span可以被选中
            if (selStart == selEnd) {
                //单选 点击话题时 光标出现在话题后
                for (TopicSpan span : getTopicSpans()) {
                    if (temp.getSpanStart(span) < selStart && temp.getSpanEnd(span) > selStart) {
                        setSelection(temp.getSpanEnd(span));
                        break;
                    }
                }
            } else {
                //长按后多选 光标不会出现在话题中间
                for (TopicSpan span : getTopicSpans()) {
                    if (temp.getSpanStart(span) < selStart && temp.getSpanEnd(span) > selStart) {
                        //前光标在话题中
                        selStart = temp.getSpanStart(span);
                    }
                    if (temp.getSpanStart(span) < selEnd && temp.getSpanEnd(span) > selEnd) {
                        //后光标在话题中
                        selEnd = temp.getSpanEnd(span);
                    }
                }
                setSelection(selStart, selEnd);
            }
        } else {
            //span不可以被选择

            //spanMustInFirst 默认只有一个span
            List<TopicSpan> spans = getTopicSpans();
            if (!spans.isEmpty()) {
                TopicSpan topicSpan = spans.get(0);
                int spanEnd = temp.getSpanEnd(topicSpan);
                setSelection(Math.max(spanEnd, selStart), Math.max(spanEnd, selEnd));
            }
        }
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        if (l == null) {
            OnKeyListener listener = new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (topicList.size() == 0) {
                            return false;
                        }
                        Editable temp = getEditableText();
                        if (getSelectionStart() == getSelectionEnd()) {
                            for (TopicSpan span : getTopicSpans()) {
                                if (temp.getSpanEnd(span) == getSelectionStart()) {
                                    //当光标为单选,在话题后时,点击删除选择整个话题 有个BUG解决不了 --有时候会无法选中
                                    //setSelection(temp.getSpanStart(span),temp.getSpanEnd(span));
                                    //无奈只能直接删除
                                    getEditableText().replace(temp.getSpanStart(span), temp.getSpanEnd(span), "");
                                    topicList.remove(span.getTopic());
                                    return true;
                                }
                            }
                            return false;
                        } else {
                            //多选情况下,移除被选择的话题,其余交给系统处理
                            for (TopicSpan span : getSelectionTopic()) {
                                topicList.remove(span.getTopic());
                            }
                            return false;
                        }
                    }
/*
                    if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (getContext() instanceof Activity) {
                            KeyboardUtils.hideSoftInput((Activity) getContext());
                            return true;
                        }
                    }
*/
                    return false;
                }
            };
            super.setOnKeyListener(listener);
        } else {
            super.setOnKeyListener(l);
        }
    }

    public List<TopicSpan.TopicObject> getTopicList() {
        return topicList;
    }

    public void setSpanMustInFirst(boolean spanMustInFirst) {
        this.spanMustInFirst = spanMustInFirst;
    }

    public void setCanSelectedWhenMustInFirst(boolean canSelectedWhenMustInFirst) {
        this.canSelectedWhenMustInFirst = canSelectedWhenMustInFirst;
    }

    public void cleanTopic() {
        topicList.clear();
        setText("");
    }

    public void setKeyboardVisibility(boolean visible) {
        if (visible) {
            KeyboardUtils.showSoftInput(this);
        } else {
            if (getContext() instanceof Activity) {
                KeyboardUtils.hideSoftInput((Activity) getContext());
            }
        }
    }
}
