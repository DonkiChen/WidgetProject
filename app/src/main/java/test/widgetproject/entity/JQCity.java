package test.widgetproject.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created on 2018/4/27.
 *
 * @author ChenFanlin
 */

@Entity(tableName = "JQCity",
        indices = {@Index({"areaCode"})})
public class JQCity implements Comparable<JQCity> {

    @PrimaryKey
    public int cityId;
    public String cityName;
    public int parentId;
    public String initial;
    public String initials;
    public String pinyin;
    public String extra;
    public String suffix;
    public String cityCode;
    public String parentCityCode;
    public String areaCode;
    public String regionAddress;

    @Override
    public int compareTo(@NonNull JQCity o) {
        return pinyin.compareToIgnoreCase(o.pinyin);
    }
}
