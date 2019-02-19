package test.widgetproject.main;

import butterknife.BindView;
import test.widgetproject.widget.TopicEditText;
import test.widgetproject.widget.span.TopicSpan;

public class TopicActivity extends BaseActivity {
    @BindView(R.id.topic_edit_text)
    TopicEditText mTopicEditText;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_topic;
    }

    @Override
    public void initView() {
        mTopicEditText.setSpanMustInFirst(true);
        mTopicEditText.setText("123");
        mTopicEditText.insertTopic(new TopicSpan.TopicObject("#", "topic", "#"));
    }
}
