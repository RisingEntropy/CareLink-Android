package edu.uestc.carelink.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import edu.uestc.carelink.R;
import edu.uestc.carelink.data.SensorData;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ViewHolder>{
    private final List<SensorData> sensorData;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(sensorData.get(position).isNewData())
            holder.date.setText(sensorData.get(position).getDate()+"*");
        else
            holder.date.setText(sensorData.get(position).getDate());
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext().getApplicationContext(), ChartsActivity.class);
                intent.putExtra("date",sensorData.get(position).getDate());
                view.getContext().startActivity(intent);
            }
        };
        holder.view.setOnClickListener(listener);
        holder.curveView.setOnClickListener(listener);
        holder.verticalDivImageView.setOnClickListener(listener);
        holder.date.setOnClickListener(listener);
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
//        private final LineChart chart;
        private final TextView date;
        private final View view;
        private final ImageView curveView, verticalDivImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            chart = itemView.findViewById(R.id.data_item_chart);
            date = itemView .findViewById(R.id.data_item_date);
            view = itemView;
            curveView = itemView.findViewById(R.id.data_item_imageView);
            verticalDivImageView = itemView.findViewById(R.id.vertical_div_imageView);
        }
    }
}
