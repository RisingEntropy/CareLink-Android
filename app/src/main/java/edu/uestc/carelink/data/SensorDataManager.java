package edu.uestc.carelink.data;

import android.content.Context;

import androidx.room.Room;

public class SensorDataManager {
    private static SensorDataManager instance;
    private SensorDatabase db;
    private SensorDataManager(Context context){
        this.db = Room.databaseBuilder(context, SensorDatabase.class, "sensor_data").build();
    }

    public static SensorDataManager getInstance(Context context){
        if(SensorDataManager.instance==null){
            SensorDataManager.instance = new SensorDataManager(context);
        }
        return SensorDataManager.instance;
    }
}
