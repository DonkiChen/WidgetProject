package test.widgetproject.main;

import android.view.View;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        startActivity(RippleActivity.class);
    }

    @OnClick({R.id.btn_link_recycler_view, R.id.btn_swipe_image_verify, R.id.btn_location, R.id.btn_label,
            R.id.btn_ripple})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_link_recycler_view:
                startActivity(LinkRecyclerViewActivity.class);
                break;
            case R.id.btn_swipe_image_verify:
                startActivity(SwipeImageVerifyActivity.class);
                break;
            case R.id.btn_location:
                startActivity(LocationActivity.class);
                break;
            case R.id.btn_label:
                startActivity(LabelActivity.class);
                break;
            case R.id.btn_ripple:
                startActivity(RippleActivity.class);
                break;
        }
    }
}
