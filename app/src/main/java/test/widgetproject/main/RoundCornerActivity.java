package test.widgetproject.main;

import android.widget.ImageView;

import butterknife.BindView;

/**
 * Created on 2018/5/17.
 *
 * @author ChenFanlin
 */
public class RoundCornerActivity extends BaseActivity {
    @BindView(R.id.image)
    ImageView mImage;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_round;
    }

    @Override
    public void initView() {

    }
}
