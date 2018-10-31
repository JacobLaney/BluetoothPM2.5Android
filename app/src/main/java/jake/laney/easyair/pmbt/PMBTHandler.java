package jake.laney.easyair.pmbt;

import android.os.Handler;
import android.os.Message;

import jake.laney.easyair.pmbt.interfaces.IPMBTConstants;
import jake.laney.easyair.pmbt.interfaces.IPMBTDeviceManager;

/**
 * Created by JakeL on 10/29/17.
 */

/*
 * Defines a handler for thread messaging between the PMBTConnectThread and the PMBTHandler
 */
public class PMBTHandler extends Handler {
    private IPMBTDeviceManager mDeviceManger;

    public PMBTHandler(IPMBTDeviceManager manager) {
        mDeviceManger = manager;
    }

    public void handleMessage(Message msg) {
        switch(msg.what) {
            case IPMBTConstants.BT_CONNECT_SUCCESS:
                mDeviceManger.pmbtConnectSuccess();
                break;
            case IPMBTConstants.BT_CONNECT_FAILURE:
                mDeviceManger.pmbtConnectFailure();
                break;
            case IPMBTConstants.BT_CONNECT_DROPPED:
                mDeviceManger.pmbtConnectDropped();
                break;
            case IPMBTConstants.BT_MESSAGE_RECEIVED:
                mDeviceManger.pmbtSetData(msg.arg1);
                break;
            default:
                break;
        }
    }
}
