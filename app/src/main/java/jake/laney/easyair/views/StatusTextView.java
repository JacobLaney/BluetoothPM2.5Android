package jake.laney.easyair.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by JakeL on 10/29/17.
 */

public class StatusTextView extends android.support.v7.widget.AppCompatTextView {
    public StatusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // source: http://aqicn.org/faq/2013-09-09/revised-pm25-aqi-breakpoints/
    public void updateText(int value) {
        String text;
        if (value <= 50) {
            text = "Good";
        }
        else if (value <= 100) {
            text = "Moderate";
        }
        else if (value <= 150) {
            text = "Unhealthy";
        }
        else if (value <= 200) {
            text = "Unhealthy";
        }
        else if (value <= 300) {
            text = "Very Unhealthy";
        }
        else {
            text = "Hazardous";
        }
        setText(text);
    }

}
