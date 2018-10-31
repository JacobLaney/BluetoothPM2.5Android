package jake.laney.easyair.fragments;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import jake.laney.easyair.R;
import jake.laney.easyair.adapters.BluetoothDevicesAdapter;
import jake.laney.easyair.pmbt.PMBTFileService;
import jake.laney.easyair.views.CircleView;
import jake.laney.easyair.views.LiveLineChart;
import jake.laney.easyair.pmbt.PMBTHandler;
import jake.laney.easyair.pmbt.PMBTService;
import jake.laney.easyair.pmbt.interfaces.IPMBTDeviceManager;
import jake.laney.easyair.views.StatusTextView;

/**
 * Created by JakeL on 10/26/17.
 */

/*
 * Provides a page for connecting to and viewing data from the pm2.5 sensor device.
 * This is the core class of the application.
 *
 * First, it provides a view that allows the user to connect to the bluetooth device
 * Second, it displays the data from a connected pm2.5 sensor device
 *
 * The order of operations:
 * 1) initialize all views and objects
 * 2) run a Timer thread that searches for paired devices and updates the btListView
 * 3) when a device is selected from the list, a PMBTConnectThread is started that tries to
 *      connect and read data from the device
 * 4) once connected, the view is changed to display the pm2.5 value, a circle that changes color
 *      with pm2.5 value, a TextView that describes the air quality, and a live graph that updates
 *      as new values arrive
 * 5) if the device is disconnected, the user is returned to the Bluetooth connection view
 *
 */
public class PMSensorFragment extends Fragment implements IPMBTDeviceManager {

    // Constants
    private final long BT_REFRESH_DELAY = 1000;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
    private static final String TAG = "Sensor Fragment";

    // Threads
    private final PMBTHandler mPMBTHandler;
    Thread deviceConnectionThread;

    // Top Level Views
    private View rootView;
    private ProgressBar loadingSpinner;
    private LinearLayout connectionLayout;
    private ConstraintLayout dataLayout;

    // BT Connection Views
    private ListView btListView;
    private ImageView dataImageView;

    // Data Visualization Views
    private CircleView circleStatusView;
    private StatusTextView mStatusTextView;
    private TextView dataDisplay;
    private LiveLineChart liveLineChart;

    // Bluetooth data
    private BluetoothDevicesAdapter devices;
    private BluetoothAdapter mBluetoothAdapter;
    private Timer refreshBluetoothSearch;
    private LocationManager locMgr;

    // File Management
    private PMBTFileService fileService;

    public PMSensorFragment() {
        super();

        mPMBTHandler = new PMBTHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        rootView = inflater.inflate(
                R.layout.pm_sensor_fragment, container, false);

        requestLocationPermission();
        requestBluetoothPermission();

        configureViews();

        return rootView;
    }

    // if the app has been reopened, show the correct layout
    @Override
    public void onResume() {
        super.onResume();
        if (deviceConnectionThread != null && deviceConnectionThread.isAlive()) {
            displayDataView();
        }
        else {
            displayPMBTConnectView();
            findBluetoothDevices();
        }
    }

    private void configureViews() {
        connectionLayout = (LinearLayout) rootView.findViewById(R.id.connectionLayout);
        dataLayout = (ConstraintLayout) rootView.findViewById(R.id.dataLayout);

        dataImageView = (ImageView) rootView.findViewById(R.id.dataImageView);

        dataDisplay = (TextView) rootView.findViewById(R.id.dataDisplay);

        fileService = new PMBTFileService(getContext());

        mStatusTextView = (StatusTextView) rootView.findViewById(R.id.statusText);

        loadingSpinner = (ProgressBar) rootView.findViewById(R.id.loadingSpinner);

        liveLineChart = (LiveLineChart) rootView.findViewById(R.id.liveLineChart);

        devices = new BluetoothDevicesAdapter(getContext(), R.layout.bluetooth_device_item);

        circleStatusView = (CircleView) rootView.findViewById(R.id.circle);

        btListView = (ListView) rootView.findViewById(R.id.btList);
        btListView.setAdapter(devices);
        btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                openBluetoothConnection(devices.getItem(i));
            }
        });

    }

    // BLUETOOTH CONFIGURATION METHODS //

    private void requestLocationPermission() {
        locMgr = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProv = locMgr.getBestProvider(criteria, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = getActivity().checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }
        }
    }

    private void requestBluetoothPermission() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "Failed to acquire Bluetooth!", Toast.LENGTH_LONG).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);
        }
    }

    // update the paired devices list regularly
    public void findBluetoothDevices() {
        devices.clear();
        final Handler handle = new Handler();
        refreshBluetoothSearch = new Timer();
        refreshBluetoothSearch.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handle.post(new Runnable() {
                    @Override
                    public void run() {
                        devices.addAll(mBluetoothAdapter.getBondedDevices());
                        btListView.setAdapter(devices);
                    }
                });
            }
        }, 0, BT_REFRESH_DELAY);
    }

    // open a new connection
    public void openBluetoothConnection(final BluetoothDevice device) {
        ((AnimationDrawable) dataImageView.getBackground()).stop();
        loadingSpinner.setVisibility(View.VISIBLE);
        deviceConnectionThread = PMBTService.connect(device, mPMBTHandler); // connect the service
    }

    // BLUETOOTH DEVICE INTERFACE METHODS - called by the device thread //

    @Override
    public void pmbtSetData(int pmValue) {
        dataDisplay.setText(String.valueOf(pmValue)); // update displayed numebr
        mStatusTextView.updateText(pmValue); // get the qualitative status
        liveLineChart.updateValue(pmValue); // update the graph
        circleStatusView.adjustBackground(pmValue); // adjust color of the circle
        /*
        try {
            fileService.write(pmValue); // write a new value
        } catch (IOException e) {
            Log.e(TAG, "Data write failed");
        }
        */
    }

    @Override
    public void pmbtConnectFailure() {
        // error message
        Toast toast = Toast.makeText(getContext(), "CONNECTION FAILED!", Toast.LENGTH_LONG);
        toast.show();

        // configure view
        displayPMBTConnectView();

        // act
        findBluetoothDevices();
    }

    @Override
    public void pmbtConnectSuccess() {
        displayDataView(); // change views
        refreshBluetoothSearch.cancel(); // cancel the update timer
    }

    @Override
    public void pmbtConnectDropped() {
        // display an alert that the connection failed
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("ERROR: Connection Lost!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // configure view
                        displayPMBTConnectView();

                        // act
                        findBluetoothDevices();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // UTILITIES //

    // items used in selection of bt device
    private void displayPMBTConnectView() {
        loadingSpinner.setVisibility(View.GONE);
        liveLineChart.stop();
        dataLayout.setVisibility(View.GONE);
        connectionLayout.setVisibility(View.VISIBLE);
        dataImageView.setBackgroundResource(R.drawable.searching_bt_animation);
        ((AnimationDrawable) dataImageView.getBackground()).start();
    }

    // items used to view current pm2.5 data
    private void displayDataView() {
        connectionLayout.setVisibility(View.GONE);
        dataLayout.setVisibility(View.VISIBLE);
        liveLineChart.start();
        dataDisplay.setText("...");
        loadingSpinner.setVisibility(View.GONE);
    }
}
