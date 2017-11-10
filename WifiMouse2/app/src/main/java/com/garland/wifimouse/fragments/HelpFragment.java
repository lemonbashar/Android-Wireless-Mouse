package com.garland.wifimouse.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.garland.wifimouse.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lemon on 10/7/2017.
 */

public class HelpFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_help,container,false);
        WebView webView= (WebView) view.findViewById(R.id.web_view);
        StringBuffer buffer=new StringBuffer();

        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(getActivity().getAssets().open("help.html")));
            String line;
            while ((line=reader.readLine())!=null)
                buffer.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        webView.loadDataWithBaseURL(null, buffer.toString(), "text/html", "UTF-8", null);
        return view;
    }
}
