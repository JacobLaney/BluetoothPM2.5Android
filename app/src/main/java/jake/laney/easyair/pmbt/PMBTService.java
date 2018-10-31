package jake.laney.easyair.pmbt;

import android.bluetooth.BluetoothDevice;

/**
 * Created by JakeL on 10/29/17.
 */



// provides a service for connecting to a bluetooth device, starts a new PMBTConnectThread thread.
public class PMBTService {
    public static Thread connect(BluetoothDevice device, PMBTHandler handler) {
        Thread mThread = new PMBTConnectThread(device, handler);
        mThread.start();
        return mThread;
    }
}
