package com.garland.wifimouse.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.events.ButtonWithActions;
import com.garland.wifimouse.utility.Report;


/**
 * Created by lemon on 9/1/2017.
 */

public class SlideShowFragment extends Fragment {

    private String PRESERVED_VOL_UP=MainActivity.ACTION_VOLUME_UP,PRESERVED_VOL_DOWN=MainActivity.ACTION_VOLUME_DOWN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_slideshow,container,false);
        view.findViewById(R.id.id_slide_next).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_RIGHT));
        view.findViewById(R.id.id_slide_prev).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_LEFT));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_slideshow,menu);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        MainActivity.sendTask(new StringBuilder().append(Report.FUNCTION).append(Report.F_FIVE).toString());
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.id_action_esc2:
                case R.id.id_action_esc:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_ESCAPE).toString());
                    return true;
                case R.id.id_action_slide_next2:
                case R.id.id_action_slide_next:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_RIGHT).toString());
                    return true;
                case R.id.id_action_slide_prev2:
                case R.id.id_action_slide_prev:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_LEFT).toString());
                    return true;
                case R.id.action_play_slide2:
                case R.id.action_play_slide:
                    MainActivity.sendTask(new StringBuilder().append(Report.FUNCTION).append(Report.F_FIVE).toString());
                    return true;
            }
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        return super.onOptionsItemSelected(item);
    }
}
