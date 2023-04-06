package edu.uestc.carelink.data;

import android.content.Context;

import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SensorDataManager {
    private static Gson gson;
    private static SensorDataManager instance;
    private SensorDatabase db;
    private DataDAO dao;
    private SensorDataManager(Context context){
        this.db = Room.databaseBuilder(context, SensorDatabase.class, "sensor_data").build();
        this.dao = db.sensorDataDAO();
        if(gson==null){
            gson = new Gson();
        }
    }

    public static SensorDataManager getInstance(Context context){
        if(SensorDataManager.instance==null){
            SensorDataManager.instance = new SensorDataManager(context);
        }
        return SensorDataManager.instance;
    }

    public List<String> getRecordEntityNames(){
        return this.dao.queryAllEntityNames();
    }

    public SensorData querySensorDataByDate(String date){
        return SensorData.loadFromJson(this.dao.queryByDate(date).jsonText);
    }

    public List<SensorData> querySensorData(String ... dates){
        List<SensorData> list = new ArrayList<>();
        for(String date:dates){
            list.add(SensorData.loadFromJson(this.dao.queryByDate(date).jsonText));
        }
        return list;
    }

    public void addSensorData(String date, SensorData data){
        DatabaseEntity entity = new DatabaseEntity();
        String json = gson.toJson(data, SensorData.class);
        entity.date = date;
        entity.jsonText = json;
        this.dao.insertAll(entity);
    }

    public void deleteSensorData(String ...date){
        for(String toDel:date){
            this.dao.delete(this.dao.queryByDate(toDel));
        }
    }


}
