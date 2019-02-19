package test.widgetproject.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import test.widgetproject.main.R;
import test.widgetproject.main.SharePreviewActivity;

public class ShareAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private final Activity mActivity;

    public ShareAdapter(Activity activity) {
        super(R.layout.item_share);
        mActivity = activity;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        Glide.with(mContext).load(item).into((ImageView) helper.getView(R.id.image));
        helper.getView(R.id.image).setTransitionName(item);
        helper.getView(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SharePreviewActivity.newIntent(new ArrayList<String>(getData()), helper.getAdapterPosition());
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, v.getTransitionName());
                mContext.startActivity(intent, optionsCompat.toBundle());
            }
        });
    }
}
