package jake.laney.easyair.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import jake.laney.easyair.R;

/**
 * Author: Jake Laney on 10/27/17.
 */

/*
 * Provides an adapter to be used by a list view.
 * Manages the display of a BluetoothDevice within a ListItem.
 * The ListView that shows what bt devices are paired uses an instance of this object.
 */
public class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothDevicesAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_device_item, parent, false);
        }
        // Lookup view for data population
        TextView btName = (TextView) convertView.findViewById(R.id.bluetoothDeviceName);
        // Populate the data into the template view using the data object
        btName.setText(device.getName());
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void addAll(Collection<? extends BluetoothDevice> devices) {
        for (BluetoothDevice device : devices) {
            if (!contains(device)) {
                add(device);
            }
        }
        notifyDataSetChanged();
    }

    private boolean contains(BluetoothDevice dev) {
        for (int i = 0; i < getCount(); i++) {
            if (dev.getAddress().equals(getItem(i).getAddress())) {
                return true;
            }
        }
        return false;
    }


}
