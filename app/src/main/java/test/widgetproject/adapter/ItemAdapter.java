package test.widgetproject.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import test.widgetproject.entity.TypeBean;
import test.widgetproject.main.R;

/**
 * Created on 2018/4/10.
 *
 * @author ChenFanlin
 */

public class ItemAdapter extends BaseQuickAdapter<TypeBean.ItemBean, BaseViewHolder> {
    public ItemAdapter() {
        super(R.layout.item_demo);
    }

    @Override
    protected void convert(BaseViewHolder helper, TypeBean.ItemBean item) {
        helper.setText(R.id.tv_demo, item.getItem());
    }
}
