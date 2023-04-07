package edu.uestc.carelink.data;


import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SensorData implements Serializable {
    private static Gson gson;
    private String date;
    private List<Float> heart_rate;
    private List<Float> blood_oxygen;
    private List<Float> temperature;
    private List<Integer> timestamp;// when was the data recorded

    public SensorData(List<Float> heart_rate, List<Float> blood_oxygen, List<Float> temperature, List<Integer> timestamp){
        this.heart_rate = heart_rate;
        this.blood_oxygen = blood_oxygen;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }
    public SensorData(){
        this.heart_rate = new ArrayList<>();
        this.blood_oxygen = new ArrayList<>();
        this.temperature = new ArrayList<>();
        this.timestamp = new ArrayList<>();
    }

    public static SensorData loadFromJson(String json){
        if(gson==null){
            gson = new Gson();
        }
        return gson.fromJson(json, SensorData.class);
    }

    public List<Float> getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(List<Float> heart_rate) {
        this.heart_rate = heart_rate;
    }

    public List<Float> getBlood_oxygen() {
        return blood_oxygen;
    }

    public void setBlood_oxygen(List<Float> blood_oxygen) {
        this.blood_oxygen = blood_oxygen;
    }

    public List<Float> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<Float> temperature) {
        this.temperature = temperature;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)return false;
        SensorData data2 = (SensorData) obj;
        return temperature.equals(data2.temperature)&&heart_rate.equals(data2.heart_rate)&&blood_oxygen.equals(data2.blood_oxygen);
    }


    public List<Integer> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(List<Integer> timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.getClass().getName()).append("{");
        buffer.append("heart_rate:");
        for(Float val:heart_rate){
            buffer.append(val).append(",");
        }
        buffer.append("blood_oxygen:");
        for(Float val:blood_oxygen){
            buffer.append(val).append(",");
        }
        buffer.append("temperature:");
        for(Float val:temperature){
            buffer.append(val).append(",");
        }
        return buffer.toString();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

