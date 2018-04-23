package test.widgetproject.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

@Entity(tableName = "City",
        indices = {@Index({"provinceId", "cityId"})})
public class City implements MultiItemEntity {
    @Ignore
    public static final int TYPE_NORMAL = 0;
    @Ignore
    public static final int TYPE_GPS = 1;
    @Ignore
    public static final int TYPE_CITY = 2;
    @Ignore
    public int itemType = TYPE_NORMAL;


    @PrimaryKey(autoGenerate = true)
    public Integer id;
    public String provinceId;
    public String cityId;
    public String cityName;
    public String cityNamePinyin;

    @Override
    public int getItemType() {
        return itemType;
    }
}
