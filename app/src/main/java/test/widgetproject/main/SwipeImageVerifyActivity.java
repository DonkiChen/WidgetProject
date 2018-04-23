package test.widgetproject.main;

import android.util.Log;
import android.widget.SeekBar;

import butterknife.BindView;
import test.widgetproject.widget.VerifyImage;

/**
 * Created on 2018/4/13.
 *
 * @author ChenFanlin
 */

public class SwipeImageVerifyActivity extends BaseActivity {

    private static final String TAG = SwipeImageVerifyActivity.class.getSimpleName();

    @BindView(R.id.seekBar)
    SeekBar mSeekBar;
    @BindView(R.id.verifyImage)
    VerifyImage mVerifyImage;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_swipe_image_verify;
    }

    @Override
    public void initView() {
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: ");
            }
        });

        mVerifyImage.bindSeekBar(mSeekBar);
    }
}
