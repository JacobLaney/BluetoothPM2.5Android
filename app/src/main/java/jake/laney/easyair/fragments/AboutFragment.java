package jake.laney.easyair.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import jake.laney.easyair.R;

/**
 * Created by JakeL on 10/30/17.
 */


/*
 * One of the pages in the application. AboutFragment provides an HTML page that
 * has a list of some features that I implemented in the application.
 * Find the html file in ../src/main/assets/about.html
 */
public class AboutFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.about_fragment, container, false);

        webView = (WebView) rootView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/about.html");

        return rootView;
    }
}
