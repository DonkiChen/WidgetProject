package test.widgetproject.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import test.widgetproject.entity.City;

/**
 * Created on 2018/4/20.
 *
 * @author ChenFanlin
 */

@Dao
public interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(City... cities);

    @Query("SELECT * FROM City ORDER BY cityNamePinyin ASC")
    Flowable<List<City>> queryCities();

    @Query("SELECT DISTINCT cityNamePinyin FROM City ORDER BY cityNamePinyin ASC")
    Flowable<List<String>> queryCitiesPinyin();

    @Delete
    void deleteCities(City... cities);
}
