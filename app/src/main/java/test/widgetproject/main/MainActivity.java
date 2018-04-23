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

    }

    @OnClick({R.id.btn_link_recycler_view, R.id.btn_swipe_image_verify, R.id.btn_location})
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
        }
    }
}
