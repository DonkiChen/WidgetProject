package test.widgetproject.main;

import butterknife.BindView;
import butterknife.OnClick;
import test.widgetproject.widget.LabelView;

/**
 * Created on 2018/4/25.
 *
 * @author ChenFanlin
 */

public class LabelActivity extends BaseActivity {

    @BindView(R.id.labelView)
    LabelView mLabelView;

    private int mGravityValue = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_label;
    }

    @Override
    public void initView() {

    }

    @OnClick(R.id.btn_label)
    public void onLabelClicked() {
        mLabelView.setLabelGravity(LabelView.LabelGravity.fromValue((++mGravityValue) % 4));
    }
}
