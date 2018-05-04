package test.widgetproject.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.mvp.base.util.BaseUtils;

import test.widgetproject.entity.City;
import test.widgetproject.entity.JQCity;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

@Database(entities = {City.class, JQCity.class}, version = 1, exportSchema = false)
public abstract class DbHelper extends RoomDatabase {

    public static DbHelper getInstance() {
        return Holder.INSTANCE;
    }

    public abstract CityDao getCityDao();

    public abstract JQCityDao getJQCityDao();

    private static class Holder {
        private static final DbHelper INSTANCE = Room.databaseBuilder(BaseUtils.getApplicationContext(), DbHelper.class,
                "city.db").build();
    }
}
