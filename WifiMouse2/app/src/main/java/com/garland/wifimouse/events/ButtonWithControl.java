package com.garland.wifimouse.events;

import android.view.View;
import android.widget.RadioButton;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.utility.Report;


/**
 * Created by lemon on 9/1/2017.
 */

public class ButtonWithControl implements View.OnClickListener {
    char actionType,ifTrue,ifFalse;
    RadioButton withControl;

    public ButtonWithControl(char ifTrue, char ifFalse, RadioButton withControl) {
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
        this.withControl = withControl;
        actionType= Report.CONTROLS;
    }

    @Override
    public void onClick(View v) {
        if(withControl.isChecked()){
            withControl.setChecked(false);
            MainActivity.sendTask(new StringBuilder().append(String.valueOf(actionType)).append(ifTrue).toString());
        }
        else {
            withControl.setChecked(true);
            MainActivity.sendTask(new StringBuilder().append(String.valueOf(actionType)).append(ifFalse).toString());
        }
    }
}