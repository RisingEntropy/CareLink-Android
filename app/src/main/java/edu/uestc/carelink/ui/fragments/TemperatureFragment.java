package edu.uestc.carelink.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.uestc.carelink.R;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class TemperatureFragment extends Fragment {

    public TemperatureFragment() {
        super(R.layout.fragment_temperature);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View res =  inflater.inflate(R.layout.fragment_temperature, container, false);
        LineChartView view = res.findViewById(R.id.temperature_chart);
        //set pattern
        view.setInteractive(true);
        view.setZoomEnabled(true);
        view.setZoomType(ZoomType.HORIZONTAL);
        //temporary data
        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData chartData = new LineChartData();
        chartData.setLines(lines);
        view.setLineChartData(chartData);
        return res;
    }
}