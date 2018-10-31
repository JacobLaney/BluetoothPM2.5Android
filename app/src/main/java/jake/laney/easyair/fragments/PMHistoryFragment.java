package jake.laney.easyair.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jake.laney.easyair.R;
import jake.laney.easyair.pmbt.interfaces.EnumPMBTDates;
import jake.laney.easyair.views.HistoryLineChart;

/**
 * Created by JakeL on 10/26/17.
 */

public class PMHistoryFragment extends Fragment {

    private HistoryLineChart mHistoryLineChart;
    private View rootView;
    private Button dayButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        rootView = inflater.inflate(
                R.layout.pm_history_fragment, container, false);

        configureViews();

        return rootView;
    }

    private void configureViews() {
        mHistoryLineChart = (HistoryLineChart) rootView.findViewById(R.id.pmHistoryChart);
        mHistoryLineChart.setData(EnumPMBTDates.DAY);

        dayButton = (Button) rootView.findViewById(R.id.dayButton);
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("HISTORY FRAGMENT", "Clicked day button");
                mHistoryLineChart.setData(EnumPMBTDates.DAY);
            }
        });
    }
}
