package test.widgetproject.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.mvp.base.util.BaseUtils;

import test.widgetproject.entity.City;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class CityDatabase extends RoomDatabase {

    public static CityDatabase getInstance() {
        return Holder.INSTANCE;
    }

    public abstract CityDao getCityDao();

    private static class Holder {
        private static final CityDatabase INSTANCE = Room.databaseBuilder(BaseUtils.getApplicationContext(), CityDatabase.class,
                "city.db").build();
    }
}
