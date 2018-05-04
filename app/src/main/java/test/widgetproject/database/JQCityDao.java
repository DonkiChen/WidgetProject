package test.widgetproject.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import test.widgetproject.entity.JQCity;

/**
 * Created on 2018/4/27.
 *
 * @author ChenFanlin
 */

@Dao
public interface JQCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(JQCity... cities);

    @Query("SELECT * FROM JQCity ORDER BY pinyin ASC")
    Flowable<List<JQCity>> queryCities();

    @Delete
    void deleteCities(JQCity... cities);
}
