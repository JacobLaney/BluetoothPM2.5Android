package jake.laney.easyair.views;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jake.laney.easyair.R;

/**
 * Created by JakeL on 10/29/17.
 */


// see https://github.com/PhilJay/MPAndroidChart

/*
 * Line chart that shows a livestream of data from the pm2.5 sensor.
 * The chart uses a Timer to smoothly update values on the graph
 */
public class LiveLineChart extends LineChart {

    private final long UPDATE_RATE = 25;
    private final int DATA_SIZE = 1000;
    private final int Y_MAX = 250;

    // linechart data
    private List<Entry> mDataList;
    private LineDataSet mDataSet;
    private LineData mData;
    private int lineColor;
    private int currentY;
    private Timer updateTimer;

    public LiveLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        lineColor = ResourcesCompat.getColor(getResources(), R.color.white, null);
        currentY = -1;

        setNoDataText("");
        setTouchEnabled(false);
        getLegend().setEnabled(false);
        getAxisRight().setEnabled(false);

        XAxis xAxis = getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(DATA_SIZE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        setScaleXEnabled(false);
        YAxis yAxis = getAxisLeft();
        yAxis.setDrawGridLines(false);

        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(lineColor);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(Y_MAX);
        setScaleYEnabled(false);

        mDataList = new LinkedList<>();
    }

    // method used to add a new value to the graph
    public void updateValue(int y) {
        currentY = y;
        if (y >= Y_MAX) {
            y = Y_MAX - 1;
        }
    }

    // begin updating the graph
    public void start() {
        final Handler handle = new Handler();
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handle.post(new Runnable() {
                    @Override
                    public void run() {
                        addValue();
                    }
                });
            }
        }, 0, UPDATE_RATE);
    }

    // stop the graph from updating
    public void stop() {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }

    // updates the graph
    private void addValue() {
        for (Entry e : mDataList) {
            e.setX(e.getX() - 1);
        }
        if (mDataList.size() > 0 && mDataList.get(0).getX() < 0) {
            mDataList.remove(0);
        }
        if (currentY > 0) {
            mDataList.add(new Entry(DATA_SIZE, currentY));
            mDataSet = new LineDataSet(mDataList, "PM Values");
            mDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            mDataSet.setColor(lineColor);
            mDataSet.setDrawCircles(false);
            mDataSet.setValueFormatter(new ValueFormatter());

            mData = new LineData(mDataSet);
            this.setData(mData);
            this.invalidate();
        }

    }

    // used to format the graph
    private class ValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";
        }
    }
}
