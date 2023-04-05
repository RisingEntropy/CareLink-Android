package edu.uestc.carelink.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDAO {
    @Query("SELECT * FROM data_record")
    List<DatabaseEntity> getAll();

    @Query("SELECT * FROM data_record WHERE date IN (:queryDates)")
    List<DatabaseEntity> loadAllByDates(String[] queryDates);

    @Delete
    void delete(DatabaseEntity entity);

    @Insert
    void insertAll(DatabaseEntity... entities);

}
