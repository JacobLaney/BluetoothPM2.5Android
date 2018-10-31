package jake.laney.easyair;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Random;

import jake.laney.easyair.adapters.SwipePageAdapter;
import jake.laney.easyair.pmbt.PMBTDataModel;
import jake.laney.easyair.pmbt.PMBTFileService;

/*
 * The Main Activity
 *
 * See ./fragments/PMSensorFragment for the core functionality of the application.
 *
 * MainActivity.java just initializes the ViewPager object that allows for swipe navigation
 * within the application.
 */
public class MainActivity extends FragmentActivity {
    private static final String Tag = "MainActivity";

    private ViewPager mViewPager;
    private Button leftButton; // navigate to fragment on the left
    private Button rightButton; // navigate to fragment on the right

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set content
        setContentView(R.layout.activity_main);

        // add some test data
        checkFirstTimeOpen();

        // set up components
        configureViews();
    }


    // use shared preferences
    public void checkFirstTimeOpen() {
        SharedPreferences settings = this.getSharedPreferences("EasyAir", 0);
        boolean firstTime = settings.getBoolean("isFirstTime", true);
        if (firstTime) {
            configureTestMode();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstTime", false); // set for future accesses
            editor.apply();
        }
    }

    // method that populates the data store file
    // this is used to test the display of historical data
    public void configureTestMode() {
        PMBTFileService fileService = new PMBTFileService(getBaseContext()); // uses file service
        Random rand = new Random();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        for (int i = 0; i < 23; i++) {
            for (int j = 0; j < 60; j++) {
                try {
                    fileService.write(rand.nextInt(300), today.getTime());
                }
                catch (Exception e) {
                    Log.e(Tag, "Failed to insert a test point");
                }
                today.add(Calendar.MINUTE, 1);
            }
        }
    }

    public void configureViews() {
        // setup swipe navigation
        mViewPager = (ViewPager) findViewById(R.id.pager);
        final SwipePageAdapter mSwipePageAdapter = new SwipePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSwipePageAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    leftButton.setVisibility(Button.GONE);
                else
                    leftButton.setVisibility(Button.VISIBLE);
                if (position == mSwipePageAdapter.getCount() - 1)
                    rightButton.setVisibility(Button.GONE);
                else
                    rightButton.setVisibility(Button.VISIBLE);
            }
        });

        // add navigation buttons
        leftButton = (Button) findViewById(R.id.leftButton);
        leftButton.setVisibility(Button.GONE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.arrowScroll(ViewPager.FOCUS_LEFT);
            }
        });

        rightButton = (Button) findViewById(R.id.rightButton);
        rightButton.setVisibility(Button.VISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
            }
        });
    }

}
