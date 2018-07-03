package test.widgetproject.main;

import android.widget.TextView;

import butterknife.BindView;

/**
 * Created on 2018/6/7.
 *
 * @author ChenFanlin
 */
public class DemoActivity extends BaseActivity {
    private static final String TAG = DemoActivity.class.getSimpleName();

    @BindView(R.id.textView)
    TextView mTextView;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_demo;
    }

    @Override
    public void initView() {
//        mTextView.setText("asdasdasdasdasdasda斯顿");
    }
}
