package test.widgetproject.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created on 2018/4/24.
 *
 * @author ChenFanlin
 */

public class PinyinAdapter extends BaseQuickAdapter<String,BaseViewHolder> {
    public PinyinAdapter(int layoutResId) {
        super(layoutResId);

    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
