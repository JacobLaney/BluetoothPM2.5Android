package jake.laney.easyair.pmbt.interfaces;

/**
 * Created by JakeL on 10/28/17.
 */

/*
 * Interface used by the PMBTHandler. The handler calls these methods from the PMSensorFragment
 * to update user interface.
 */

public interface IPMBTDeviceManager {
    public void pmbtSetData(int pmValue);

    public void pmbtConnectFailure();

    public void pmbtConnectSuccess();

    public void pmbtConnectDropped();
}
