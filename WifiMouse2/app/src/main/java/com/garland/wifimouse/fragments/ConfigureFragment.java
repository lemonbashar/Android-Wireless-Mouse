package com.garland.wifimouse.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.utility.Report;


/**
 * Created by lemon on 8/30/2017.
 */

public class ConfigureFragment extends Fragment {

    private View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_configure,container,false);
        RadioButton buttonForSlide= (RadioButton) mainView.findViewById(R.id.radio_button_slide_ctrl);
        RadioButton buttonForScroll= (RadioButton) mainView.findViewById(R.id.radio_button_scrollbar_ctrl);
        if(MainActivity.vol_button_for_scrolling)
            buttonForScroll.setChecked(true);
        else
            buttonForSlide.setChecked(true);
        buttonForScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((RadioButton)v).isChecked()) {
                    MainActivity.vol_button_for_scrolling = true;
                    MainActivity.ACTION_VOLUME_UP=new StringBuffer().append(Report.MOUSE).append(Report.MOUSE_V_SCROLL_UP).toString();
                    MainActivity.ACTION_VOLUME_DOWN=new StringBuffer().append(Report.MOUSE).append(Report.MOUSE_V_SCROLL_DOWN).toString();
                }
            }
        });

        buttonForSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.vol_button_for_scrolling = false;
                MainActivity.ACTION_VOLUME_UP=new StringBuffer().append(Report.KEY_ACTION).append(Report.ACTION_KEY_RIGHT).toString();
                MainActivity.ACTION_VOLUME_DOWN=new StringBuffer().append(Report.KEY_ACTION).append(Report.ACTION_KEY_LEFT).toString();
            }
        });
        return mainView;
    }
}
