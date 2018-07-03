package test.widgetproject.main;

import android.widget.EditText;

import butterknife.BindView;
import test.widgetproject.widget.dialog.NumberKeyboardDialog;

/**
 * Created on 2018/6/22.
 *
 * @author ChenFanlin
 */
public class KeyboardActivity extends BaseActivity {
    private static final String TAG = KeyboardActivity.class.getSimpleName();
    @BindView(R.id.editText)
    EditText mEditText;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_keyboard;
    }

    @Override
    public void initView() {
        mEditText.setShowSoftInputOnFocus(false);
        new NumberKeyboardDialog(this, mEditText).show();
    }
}
