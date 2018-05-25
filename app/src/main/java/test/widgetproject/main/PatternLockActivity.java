package test.widgetproject.main;

import android.text.TextUtils;

import com.mvp.base.util.ToastUtils;

import java.util.List;

import butterknife.BindView;
import test.widgetproject.widget.PatternLock;

/**
 * Created on 2018/5/23.
 *
 * @author ChenFanlin
 */
public class PatternLockActivity extends BaseActivity {
    @BindView(R.id.patternLock)
    PatternLock mPatternLock;
    private String mSecretKey;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_pattern_lock;
    }

    @Override
    public void initView() {
        mPatternLock.setConfigMode(true);
        mPatternLock.setOnConfigListener(new PatternLock.OnConfigListener() {
            @Override
            public void onSuccess(List<PatternLock.PatternPoint> selectedPoints, String secretKey) {
                mPatternLock.setConfigMode(false);
                mSecretKey = secretKey;
            }

            @Override
            public void onFailed(List<PatternLock.PatternPoint> selectedPoints) {
                ToastUtils.showShortSafe("密码过短");
            }
        });
        mPatternLock.setSecretKeyComparator(new PatternLock.SecretKeyComparator() {
            @Override
            public boolean onCompare(String secretKey) {
                return TextUtils.equals(mSecretKey, secretKey);
            }
        });
        mPatternLock.setOnResultListener(new PatternLock.OnResultListener() {
            @Override
            public void onSuccess(List<PatternLock.PatternPoint> selectedPoints, String secretKey) {
                ToastUtils.showShortSafe("密码正确");
            }

            @Override
            public void onFailed(List<PatternLock.PatternPoint> selectedPoints) {
                ToastUtils.showShortSafe("密码错误");
            }
        });
    }
}
