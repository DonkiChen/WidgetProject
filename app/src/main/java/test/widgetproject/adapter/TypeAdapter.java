package test.widgetproject.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import test.widgetproject.entity.TypeBean;
import test.widgetproject.main.R;

/**
 * Created on 2018/4/9.
 *
 * @author ChenFanlin
 */

public class TypeAdapter extends BaseQuickAdapter<TypeBean, BaseViewHolder> {
    private TypeBean mSelectedType;

    public TypeAdapter() {
        super(R.layout.item_demo);
    }

    @Override
    protected void convert(BaseViewHolder helper, TypeBean item) {
        helper.setText(R.id.tv_demo, item.getType())
                .setBackgroundColor(R.id.tv_demo, mSelectedType == item ? Color.GREEN : Color.TRANSPARENT);
    }

    public void setSelectedType(TypeBean selectedType) {
        if (mSelectedType == selectedType) {
            return;
        }
        int oldIndex = getData().indexOf(mSelectedType);
        int newIndex = getData().indexOf(selectedType);
        mSelectedType = selectedType;
        notifyItemChanged(oldIndex);
        notifyItemChanged(newIndex);
    }

    public void setSelectedType(int index) {
        if (index < 0 || index > getData().size()) {
            return;
        }
        setSelectedType(getData().get(index));
    }
}
