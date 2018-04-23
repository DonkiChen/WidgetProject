package test.widgetproject.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import test.widgetproject.entity.City;
import test.widgetproject.main.R;

/**
 * Created on 2018/4/23.
 *
 * @author ChenFanlin
 */

public class CityAdapter extends BaseQuickAdapter<City, BaseViewHolder> {
    public CityAdapter() {
        super(R.layout.item_location);
    }

    @Override
    protected void convert(BaseViewHolder helper, City item) {
        helper.setText(R.id.tv_city, item.cityName);
    }
}
