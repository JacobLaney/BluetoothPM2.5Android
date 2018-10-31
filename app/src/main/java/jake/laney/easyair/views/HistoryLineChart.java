package jake.laney.easyair.views;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jake.laney.easyair.R;
import jake.laney.easyair.pmbt.PMBTDataModel;
import jake.laney.easyair.pmbt.PMBTFileService;
import jake.laney.easyair.pmbt.interfaces.EnumPMBTDates;

/**
 * Created by JakeL on 10/30/17.
 */

// see https://github.com/PhilJay/MPAndroidChart
// displays historical pm2.5 data
// only partially complete, but visible when the PMHistoryFragment is displayed
public class HistoryLineChart extends LineChart {

    // linechart data
    private List<Entry> mDataList;
    private LineDataSet mDataSet;
    private LineData mData;
    private int lineColor;
    private PMBTFileService fileService;

    public HistoryLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        lineColor = ResourcesCompat.getColor(getResources(), R.color.white, null);
        fileService = new PMBTFileService(context);

        setNoDataText("");
        setTouchEnabled(false);
        getLegend().setEnabled(false);
        getAxisRight().setEnabled(false);

        XAxis xAxis = getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        YAxis yAxis = getAxisLeft();
        yAxis.setDrawGridLines(false);

        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(lineColor);
    }

    // should look for the data that was produced today and then
    // display that on the graph
    private void setDataForDay(List<PMBTDataModel> data) {
        mDataList = new LinkedList<>();
        Calendar c = Calendar.getInstance();
        for (PMBTDataModel model : data) {
            c.setTime(model.time);
            if (mDataList.size() > 0) {
                if (mDataList.get(mDataList.size() - 1).getX() == model.time.getTime()) {
                    continue;
                }
            }
            Entry e = new Entry(model.time.getTime(), model.pmValue);
            mDataList.add(e);
        }
        mDataSet = new LineDataSet(mDataList, "PM TODAY");
        mDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mDataSet.setColor(lineColor);
        mDataSet.setDrawCircles(false);
        mDataSet.setValueFormatter(new ValueFormatter());

        mData = new LineData(mDataSet);
        this.setData(mData);
        this.invalidate();
    }


    // request display data for a time period
    public void setData(EnumPMBTDates dateChoice) {
        List<PMBTDataModel> data;
        try {
            data = fileService.readAll();
        } catch (IOException e) {
            setNoDataText("FAILED TO RETRIEVE DATA");
            return;
        }
        setDataForDay(data); // TODO update for other time requests
    }

    private class ValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return "";
        }
    }

    public class XAxisValueFormatter implements IAxisValueFormatter {
        private final Calendar c = Calendar.getInstance();
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            c.setTime(new Date((long)value));
            return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        }
    }



}
