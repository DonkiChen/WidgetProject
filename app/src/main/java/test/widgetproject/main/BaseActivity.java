package test.widgetproject.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2018/4/13.
 *
 * @author ChenFanlin
 */

@SuppressLint("Registered")
public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        mUnbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        mDisposables.clear();
        mUnbinder.unbind();
        super.onDestroy();
    }

    public void addDisposable(Disposable... disposables) {
        mDisposables.addAll(disposables);
    }

    public abstract int getLayoutRes();

    public abstract void initView();

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}
