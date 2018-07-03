package test.widgetproject.main;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import test.widgetproject.adapter.MainAdapter;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rv_main)
    RecyclerView mRvMain;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mRvMain.setLayoutManager(new LinearLayoutManager(this));
        mRvMain.setAdapter(new MainAdapter());
        startActivity(KeyboardActivity.class);
    }

}
