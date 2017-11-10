package com.garland.wifimouse.events;

import android.view.View;

import com.garland.wifimouse.MainActivity;


/**
 * Created by lemon on 9/1/2017.
 */

public class ButtonWithActions implements View.OnClickListener {
    char actionType,actionValue;

    public ButtonWithActions(char actionType, char actionValue) {
        this.actionType = actionType;
        this.actionValue = actionValue;
    }

    @Override
    public void onClick(View v) {
        MainActivity.sendTask(new StringBuilder().append(String.valueOf(actionType)).append(actionValue).toString());
    }
}