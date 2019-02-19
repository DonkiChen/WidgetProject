package test.widgetproject.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 2018/4/9.
 *
 * @author ChenFanlin
 */

public class TypeBean {
    private List<ItemBean> items;
    private String type;

    public TypeBean(List<ItemBean> items, String type) {
        this.items = items;
        this.type = type;
    }

    public List<ItemBean> getItems() {
        return items;
    }

    public void setItems(List<ItemBean> items) {
        this.items = items;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class ItemBean {
        @SerializedName(value = "item1", alternate = {"item2", "item3"})
        private String item;

        public ItemBean(String item) {
            this.item = item;
        }

        public String getItem() {
            return item == null ? "" : item;
        }

        public void setItem(String item) {
            this.item = item;
        }
    }
}
