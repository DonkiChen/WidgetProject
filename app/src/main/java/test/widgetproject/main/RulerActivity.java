package test.widgetproject.main;

import butterknife.BindView;
import butterknife.OnClick;
import test.widgetproject.widget.RulerView;

public class RulerActivity extends BaseActivity {
    @BindView(R.id.ruler_view)
    RulerView mRulerView;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_ruler;
    }

    @Override
    public void initView() {

    }

    @OnClick(R.id.btn_random)
    public void onClick() {
        mRulerView.randomValue();
    }
}
