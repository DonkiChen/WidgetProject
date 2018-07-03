package test.widgetproject.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.widgetproject.main.R;

/**
 * Created on 2018/6/22.
 *
 * @author ChenFanlin
 */
public class NumberKeyboardDialog extends Dialog {

    @BindView(R.id.keyboardView)
    KeyboardView mKeyboardView;

    private Keyboard mKeyboard;
    private EditText mEditText;

    public NumberKeyboardDialog(@NonNull Context context, EditText editText) {
        super(context);
        setContentView(R.layout.dialog_keyboard);
        ButterKnife.bind(this);
        mEditText = editText;
        init();
    }

    private void init() {
        mKeyboard = new Keyboard(getContext(), R.xml.number_keyboard);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {

            }

            @Override
            public void onText(CharSequence text) {
                mEditText.append(text);
            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });
    }

    @Override
    public void show() {
        if (isShowing()) {
            dismiss();
        }
        super.show();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            window.setAttributes(lp);
        }
    }
}
