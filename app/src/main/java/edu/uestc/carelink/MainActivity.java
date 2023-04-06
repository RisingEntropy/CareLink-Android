package edu.uestc.carelink;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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
        LineChart chat = findViewById(R.id.chart);
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            float val = (float) (Math.random() * 1-15) - 30;
            values.add(new Entry(i, val));
        }

        LineDataSet data = new LineDataSet(values, "value1");
        chat.setData(new LineData(data));

    }
}