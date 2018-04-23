package test.widgetproject.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

@Entity(tableName = "City",
        indices = {@Index({"provinceId", "cityId"})})
public class City {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    public String provinceId;
    public String cityId;
    public String cityName;
    public String cityNamePinyin;
}
