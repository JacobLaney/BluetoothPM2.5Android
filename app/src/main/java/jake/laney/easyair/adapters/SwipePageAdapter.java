package jake.laney.easyair.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import jake.laney.easyair.fragments.AboutFragment;
import jake.laney.easyair.fragments.PMHistoryFragment;
import jake.laney.easyair.fragments.PMSensorFragment;

/**
 * Created by JakeL on 10/26/17.
 */

/*
 * Provides the ViewPager in MainActivity.java with the list of Fragments
 * to use for swipe navigation in the application.
 */
public class SwipePageAdapter extends FragmentPagerAdapter {
    final int count = 3;

    public SwipePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new PMSensorFragment();
                break;
            case 1:
                fragment = new PMHistoryFragment();
                break;
            case 2:
                fragment = new AboutFragment();
                break;
            default:
                fragment = new PMSensorFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return count;
    }
}
