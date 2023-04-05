package edu.uestc.carelink.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyData implements Serializable {
    private Date date;
    private List<Integer> heart_rate;
    private List<Integer> blood_oxygen;
    private List<Float> temperature;

    public DailyData(Date date, List<Integer> heart_rate, List<Integer> blood_oxygen, List<Float> temperature){
        this.date = date;
        this.heart_rate = heart_rate;
        this.blood_oxygen = blood_oxygen;
        this.temperature = temperature;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Integer> getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(List<Integer> heart_rate) {
        this.heart_rate = heart_rate;
    }

    public List<Integer> getBlood_oxygen() {
        return blood_oxygen;
    }

    public void setBlood_oxygen(List<Integer> blood_oxygen) {
        this.blood_oxygen = blood_oxygen;
    }

    public List<Float> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<Float> temperature) {
        this.temperature = temperature;
    }
}
