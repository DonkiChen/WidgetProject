package test.widgetproject.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LazyChildFragment extends BaseLazyFragment {
    private static final String KEY_CONTENT = "CONTENT";
    @BindView(R.id.textView)
    TextView mTextView;
    private String mContent;
    private String TAG = this.toString();

    public static LazyChildFragment newInstance(String content) {

        Bundle args = new Bundle();
        args.putString(KEY_CONTENT, content);
        LazyChildFragment fragment = new LazyChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_lazy_child, container, false);
        Log.d(TAG, "onCreateView: " + mContent);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView.setText(mContent);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShareActivity.class));
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContent = getArguments().getString(KEY_CONTENT);
    }

    @Override
    public void onCombineResume() {
        Log.d("onCombine", "onCombineResume: " + mContent);
    }

    @Override
    public void onCombinePause() {
        Log.d("onCombine", "onCombinePause: " + mContent);
    }

    @Override
    public String toString() {
        return mContent;
    }
}
