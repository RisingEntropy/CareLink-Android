package edu.uestc.carelink.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uestc.carelink.R;
import edu.uestc.carelink.data.SensorData;
import edu.uestc.carelink.data.SensorDataManager;

public class ChartsActivity extends AppCompatActivity {
    private ExecutorService dbExecutor;
    private BarChart temperatureChart;
    private BarChart heartRateChart;
    private BarChart bloodOxygenChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        this.temperatureChart = findViewById(R.id.temperature_chart);
        this.heartRateChart = findViewById(R.id.heart_rate_chart);
        this.bloodOxygenChart = findViewById(R.id.blood_oxygen_chart);
        setupChartStyle(this.temperatureChart);
        setupChartStyle(this.heartRateChart);
        setupChartStyle(this.bloodOxygenChart);
        this.dbExecutor = Executors.newSingleThreadExecutor();
        queryChartData("20230406");

    }
    private void setupChartStyle(BarChart chart){
        chart.setPinchZoom(true);//enable zoom by directly scale it
        chart.setDrawGridBackground(false);// no grid

        ValueFormatter formatter = new HourValueFormatter();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setMaxVisibleValueCount(60);
        xAxis.setGranularity(1);// label every 2 hours
        xAxis.setLabelCount(24);
        xAxis.setValueFormatter(formatter);


    }
    private void queryChartData(String date){
        this.dbExecutor.submit(new Runnable() {
            @Override
            public void run() {
                SensorDataManager manager = SensorDataManager.getInstance(getApplication());
                SensorData dataToShow = manager.querySensorData(date).get(0);
                updateUI(dataToShow, date);
            }
        });
    }

    private void updateUI(SensorData dataToShow, String date){
        Handler uiThead = new Handler(Looper.getMainLooper());
        uiThead.post(new Runnable() {
            //TODO Analyze data and generate comment, maybe chatGPT is ok to USE.
            private void processComment(){

            }
            @Override
            public void run() {
                ArrayList<BarEntry> blood_oxygen = new ArrayList<>();
                ArrayList<BarEntry> heart_rate = new ArrayList<>();
                ArrayList<BarEntry> temperature=  new ArrayList<>();
                List<Integer> timestamp = dataToShow.getTimestamp();
                for(int i = 0;i<timestamp.size();i++){
                    blood_oxygen.add(new BarEntry(timestamp.get(i), dataToShow.getBlood_oxygen().get(i)));
                    heart_rate.add(new BarEntry(timestamp.get(i), dataToShow.getHeart_rate().get(i)));
                    temperature.add(new BarEntry(timestamp.get(i), dataToShow.getTemperature().get(i)));
                }
                BarDataSet blood_oxygen_dataset = new BarDataSet(blood_oxygen, getString(R.string.label_blood_oxygen));
                BarDataSet heart_rate_dataset = new BarDataSet(heart_rate, getString(R.string.label_heart_rate));
                BarDataSet temperature_dataset = new BarDataSet(temperature, getString(R.string.label_temperature));
                ArrayList<IBarDataSet> b_oSet = new ArrayList<>();
                ArrayList<IBarDataSet> h_rset = new ArrayList<>();
                ArrayList<IBarDataSet> tSet = new ArrayList<>();
                b_oSet.add(blood_oxygen_dataset);
                h_rset.add(heart_rate_dataset);
                tSet.add(temperature_dataset);
                ChartsActivity.this.bloodOxygenChart.getDescription().setText(date);
                ChartsActivity.this.heartRateChart.getDescription().setText(date);
                ChartsActivity.this.temperatureChart.getDescription().setText(date);
                ChartsActivity.this.bloodOxygenChart.setData(new BarData(b_oSet));
                ChartsActivity.this.heartRateChart.setData(new BarData(h_rset));
                ChartsActivity.this.temperatureChart.setData(new BarData(tSet));
                ChartsActivity.this.bloodOxygenChart.invalidate();
                ChartsActivity.this.heartRateChart.invalidate();
                ChartsActivity.this.temperatureChart.invalidate();
            }
        });
    }

    @Override
    protected void onStop() {

        super.onStop();
        if(!this.dbExecutor.isShutdown()){
            this.dbExecutor.shutdownNow();
        }
    }
}