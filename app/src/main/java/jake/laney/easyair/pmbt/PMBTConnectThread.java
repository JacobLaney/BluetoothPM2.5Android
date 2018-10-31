package jake.laney.easyair.pmbt;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import jake.laney.easyair.pmbt.interfaces.IPMBTConstants;

/**
 * Created by JakeL on 10/29/17.
 */

/*
 * A thread that manages the bluetooth connection with the pm2.5 sensor device.
 * Provides a function for connectin to the device and a function that transmits the
 * data to a PMBTHandler object view Android Messages (android.os.Message)
 */
class PMBTConnectThread extends Thread {
    private static final String TAG = "CONNECTED THREAD";
    private final PMBTHandler mHandler; // the handle to send message to
    private final BluetoothDevice mmDevice;
    private BluetoothSocket btSocket;

    public PMBTConnectThread(BluetoothDevice device, PMBTHandler handler) {
        mmDevice = device;
        mHandler = handler;
    }

    private void openBTConnection() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            btSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            InputStream mmIn = btSocket.getInputStream();
            OutputStream mmOut = btSocket.getOutputStream();
            mHandler.obtainMessage(IPMBTConstants.BT_CONNECT_SUCCESS).sendToTarget();
            listenForData(mmIn, mmOut); // begin listening for data
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while connecting", e);
            mHandler.obtainMessage(IPMBTConstants.BT_CONNECT_FAILURE).sendToTarget();
            this.cancel();
        }
    }

    private void listenForData(InputStream mmInStream, OutputStream mmOutStream) {
        int value;
        int readBufferPosition = 0;
        int bytesAvailable;
        byte delimiter = 10;
        byte[] readBuffer = new byte[1024];
        while (true) {
            try {
                bytesAvailable = mmInStream.available();
                if (bytesAvailable > 0) {
                    byte[] packetBytes = new byte[bytesAvailable];
                    mmInStream.read(packetBytes);
                    for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        if (b == delimiter) {
                            byte[] encodedBytes = new byte[readBufferPosition];
                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                            final String data = new String(encodedBytes);

                            char[] tmp = new char[readBufferPosition];

                            value = 0;
                            for (int j = 0; j < encodedBytes.length - 1; j++) {
                                value = value * 10 + encodedBytes[j] - 48;
                            }

                            readBufferPosition = 0; //readBuffer

                            mHandler.obtainMessage(IPMBTConstants.BT_MESSAGE_RECEIVED, value, 0).sendToTarget();
                        } else {
                            readBuffer[readBufferPosition++] = b;
                        }
                    }
                }
                // test a write to check connection
                mmOutStream.write(1);
            } catch (IOException ex) {
                Log.d(TAG, "Input stream was disconnected");
                mHandler.obtainMessage(IPMBTConstants.BT_CONNECT_DROPPED).sendToTarget();
                break;
            }
        }
    }

    @Override
    public void run() {
        openBTConnection();
    }

    public void cancel() {
        try {
            if (btSocket != null)
                btSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}