package edu.uestc.carelink;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uestc.carelink.data.SensorData;
import edu.uestc.carelink.data.SensorDataManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SensorData data = new SensorData();
                List<Float> a1 = new ArrayList<>();
                List<Float> a2 = new ArrayList<>();
                List<Float> a3 = new ArrayList<>();
                List<Integer> time = new ArrayList<>();
                Random random = new Random();
                for(int i = 1;i<40;i++){
                    a1.add(random.nextFloat());
                    a2.add(random.nextFloat());
                    a3.add(random.nextFloat());
                    time.add(i);
                }
                data.setDate("20220409");
                data.setHeart_rate(a1);
                data.setTemperature(a2);
                data.setBlood_oxygen(a3);
                data.setTimestamp(time);
                SensorDataManager.getInstance(MainActivity.this).addSensorData(data);

            }
        }).start();
    }
}