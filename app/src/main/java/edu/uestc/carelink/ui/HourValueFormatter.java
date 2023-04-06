package edu.uestc.carelink.ui;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class HourValueFormatter extends ValueFormatter {
    public static final String TAG = "HourValueFormatter";
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int hour = (int)value;
        return Integer.toString(hour);
        
    }
}
