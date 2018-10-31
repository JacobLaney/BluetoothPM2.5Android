package jake.laney.easyair.pmbt;

import java.util.Date;

/**
 * Created by JakeL on 10/29/17.
 */


// used to represent pm2.5 data in local data storage
public class PMBTDataModel {
    public Date time;
    public int pmValue;

    public PMBTDataModel(Date t, int v) {
        time = t;
        pmValue = v;
    }

}
