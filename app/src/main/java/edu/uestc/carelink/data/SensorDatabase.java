package edu.uestc.carelink.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DatabaseEntity.class}, version = 1, exportSchema=false)
public abstract class SensorDatabase extends RoomDatabase {
    public abstract DataDAO sensorDataDAO();
}
