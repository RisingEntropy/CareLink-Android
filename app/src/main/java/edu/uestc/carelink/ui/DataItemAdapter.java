package edu.uestc.carelink.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import edu.uestc.carelink.R;
import edu.uestc.carelink.data.SensorData;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ViewHolder>{
    private List<SensorData> sensorData;
    public DataItemAdapter(){
        sensorData = new ArrayList<>();
    }
    public DataItemAdapter(List<SensorData> data){
        this.sensorData = data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_data_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(sensorData.get(position).getDate());
        ArrayList<Entry> heart_rate_entries = new ArrayList<>();
        ArrayList<Entry> temperature_entries = new ArrayList<>();
        ArrayList<Entry> blood_oxygen_entries = new ArrayList<>();
        List<Float> hrReadings = sensorData.get(position).getHeart_rate();
        List<Float> tmpReadings = sensorData.get(position).getTemperature();
        List<Float> boReadings = sensorData.get(position).getBlood_oxygen();
        List<Integer> timestamps = sensorData.get(position).getTimestamp();
        for(int i = 0;i<timestamps.size();i++){
            heart_rate_entries.add(new Entry(timestamps.get(i), hrReadings.get(i)));
            temperature_entries.add(new Entry(timestamps.get(i), tmpReadings.get(i)));
            blood_oxygen_entries.add(new Entry(timestamps.get(i), boReadings.get(i)));
        }
        LineDataSet hrSet = new LineDataSet(heart_rate_entries, "hear rate");
        hrSet.setColor(Color.RED);
        LineDataSet tmpSet = new LineDataSet(temperature_entries, "temperature setting");
        tmpSet.setColor(Color.GREEN);
        LineDataSet boSet = new LineDataSet(blood_oxygen_entries, "blood oxygen");
        boSet.setColor(Color.BLACK);
        List<ILineDataSet> curve = new ArrayList<>();
        curve.add(hrSet);curve.add(tmpSet);curve.add(boSet);
        LineData lineData = new LineData(curve);
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setPinchZoom(false);
        holder.chart.setData(lineData);
        holder.chart.getXAxis().setEnabled(false);
        holder.chart.getLegend().setEnabled(false);
    }
    public synchronized void add(SensorData data){
        this.sensorData.add(data);
        notifyItemInserted(this.sensorData.size()-1);
    }
    @Override
    public int getItemCount() {
        return sensorData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final LineChart chart;
        private final TextView date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.data_item_chart);
            date = itemView .findViewById(R.id.data_item_date);
        }
    }
}
