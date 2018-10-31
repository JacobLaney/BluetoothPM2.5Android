package jake.laney.easyair.pmbt.interfaces;

/**
 * Created by JakeL on 10/29/17.
 */


// Defines several constants used when transmitting messages between the
// PMBTConnectThread and the PMBTHandler.
public interface IPMBTConstants {
    public static final int BT_CONNECT_SUCCESS = 1;
    public static final int BT_CONNECT_FAILURE = 2;
    public static final int BT_CONNECT_DROPPED = 3;
    public static final int BT_MESSAGE_RECEIVED = 4;
}
