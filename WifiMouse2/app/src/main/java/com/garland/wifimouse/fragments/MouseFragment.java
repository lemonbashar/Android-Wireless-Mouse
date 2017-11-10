package com.garland.wifimouse.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.events.ButtonWithActions;
import com.garland.wifimouse.utility.Configure;
import com.garland.wifimouse.utility.Report;


/**
 * Created by lemon on 8/20/2017.
 */

public class MouseFragment extends Fragment  {

    private View mainView;
    private GestureDetector mouseGesture,verticalGesture;
    private LinearLayout buttonLayout,ctrlLayout,directionLayout,layoutTextPost;
    private static SharedPreferences preferences;

    public static final String SHOW_TEXT_POST="SHOW_TEXT_POST",SHOW_MOUSE_BUTTON="SHOW_MOUSE_BUTTON",SHOW_CTRL_BUTTON="SHOW_CTRL_BUTTON",SHOW_DIRECTION_BUTTON="SHOW_DIRECTION_BUTTON";
    private Button leftButton;

    private Configure configure;
    private boolean showButtonLayout;
    private boolean showCtrlLayout;
    private boolean showDirectionLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_mouse,container,false);
        setHasOptionsMenu(true);
        try {
            configure=new Configure();

            preferences=getActivity().getSharedPreferences(Report.SHARED_PREF_KEY, Context.MODE_PRIVATE);

            mouseGesture =new GestureDetector(getActivity(),DETECT_MOUSE);
            verticalGesture=new GestureDetector(getActivity(),DETECT_SCROLL);
            buttonLayout= (LinearLayout) mainView.findViewById(R.id.layout_mouse_button);
            layoutTextPost= (LinearLayout) mainView.findViewById(R.id.layout_text_post);
            ctrlLayout= (LinearLayout) mainView.findViewById(R.id.layout_control_button);
            directionLayout= (LinearLayout) mainView.findViewById(R.id.layout_direction);
            initialize(mainView);

            mainView.findViewById(R.id.id_mouse_main).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mouseGesture.onTouchEvent(event);
                }
            });

            mainView.findViewById(R.id.vertical_scroll).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return verticalGesture.onTouchEvent(event);
                }
            });
        } catch (Exception e) {
            MainActivity.crushReport(e);

        }

        return mainView;
    }

    private void initialize(View mainView) {
        try {
            configure.initDirectionButtons(mainView);
            leftButton= (Button) mainView.findViewById(R.id.mouse_left);
            leftButton.setOnClickListener(new ButtonWithActions(Report.MOUSE,Report.MOUSE_LEFT_CLICK));
            mainView.findViewById(R.id.mouse_right).setOnClickListener(new ButtonWithActions(Report.MOUSE,Report.MOUSE_RIGHT_CLICK));
            ((CheckBox)mainView.findViewById(R.id.mouse_left_pressed)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        leftButton.setEnabled(false);
                        MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.MOUSE)).append(Report.MOUSE_LEFT_PRESSED).toString());
                    }
                    else {
                        leftButton.setEnabled(true);
                        MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.MOUSE)).append(Report.MOUSE_LEFT_RELEASE).toString());
                    }
                }
            });
            configure.initControlButtons(mainView);
            final EditText inputText= (EditText) mainView.findViewById(R.id.inputText);
            mainView.findViewById(R.id.button_text_post).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.SETTERS)).append(Report.SETTERS_SET_TEXT).append(Report.DIVIDER_FIRST).append(inputText.getText().toString()).toString());
                    inputText.setText("");
                }
            });
        } catch (Exception e) {
            MainActivity.crushReport(e);

        }
    }

    private SimpleOnGestureListener DETECT_SCROLL=new SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            MainActivity.sendTask(new StringBuilder().append(Report.MOUSE).append(Report.MOUSE_MIDDLE_CLICK).toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            MainActivity.sendTask(new StringBuilder().append(Report.MOUSE).append(Report.MOUSE_RIGHT_CLICK).toString());
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            MainActivity.sendTask(new StringBuilder().append(Report.MOUSE).append(Report.MOUSE_V_SCROLL).append(Report.DIVIDER_FIRST).append(distanceY).toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_ENTER).toString());
            return true;
        }
    };

    private SimpleOnGestureListener DETECT_MOUSE=new SimpleOnGestureListener(){

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            MainActivity.sendTask(new StringBuilder().append(Report.MOUSE).append(Report.MOUSE_LEFT_CLICK).toString());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            MainActivity.sendTask(new StringBuilder().append(Report.MOUSE).append(Report.MOUSE_RIGHT_CLICK).toString());
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int x= (int) (distanceX*-1);
            if(x<0) x-=MainActivity.ADD_MOUSE_SPEED;
            else if(x>0) x+=MainActivity.ADD_MOUSE_SPEED;
            int y= (int) (distanceY*-1);
            if(y<0) y-=MainActivity.ADD_MOUSE_SPEED;
            else if(y>0) y+=MainActivity.ADD_MOUSE_SPEED;
            MainActivity.sendTask(new StringBuilder().append(Report.MOUSE).append(Report.MOUSE_CURSOR_MOVE).append(Report.DIVIDER_FIRST).append(x).append(Report.DIVIDER_SECOND).append(y).toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_ENTER).toString());
            return true;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        showPref();
    }

    private void showPref() {
        try {
            showButtonLayout=preferences.getBoolean(SHOW_MOUSE_BUTTON,false);
            buttonLayout.setVisibility(showButtonLayout?View.VISIBLE:View.GONE);

            showCtrlLayout=preferences.getBoolean(SHOW_CTRL_BUTTON,false);
            ctrlLayout.setVisibility(showCtrlLayout?View.VISIBLE:View.GONE);

            showDirectionLayout=preferences.getBoolean(SHOW_DIRECTION_BUTTON,false);
            directionLayout.setVisibility(showDirectionLayout?View.VISIBLE:View.GONE);

            layoutTextPost.setVisibility(preferences.getBoolean(SHOW_TEXT_POST,false)?View.VISIBLE:View.GONE);
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
    }

    @Override
    public void onDestroy() {

        try {
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean(SHOW_CTRL_BUTTON,showCtrlLayout);
            editor.putBoolean(SHOW_DIRECTION_BUTTON,showDirectionLayout);
            editor.putBoolean(SHOW_MOUSE_BUTTON,showButtonLayout);
            editor.apply();
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mouse_menu,menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {
            menu.findItem(R.id.action_mouse_button).setChecked(showButtonLayout);
            menu.findItem(R.id.action_ctrl_button).setChecked(showCtrlLayout);
            menu.findItem(R.id.action_direction).setChecked(showDirectionLayout);
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.action_mouse_button:
                    showButtonLayout=!item.isChecked();
                    //item.setChecked(showButtonLayout);
                    buttonLayout.setVisibility(showButtonLayout?View.VISIBLE:View.GONE);
                    return true;
                case R.id.action_ctrl_button:
                    showCtrlLayout=!item.isChecked();
                    //item.setChecked(showCtrlLayout);
                    ctrlLayout.setVisibility(showCtrlLayout?View.VISIBLE:View.GONE);
                    return true;
                case R.id.action_direction:
                    showDirectionLayout=!item.isChecked();
                    //item.setChecked(showDirectionLayout);
                    directionLayout.setVisibility(showDirectionLayout?View.VISIBLE:View.GONE);
                    return true;
                case R.id.id_action_copy:
                    MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_COPY).toString());
                    return true;
                case R.id.id_action_cut:
                    MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_CUT).toString());
                    return true;
                case R.id.id_action_paste:
                    MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_PASTE).toString());
                    return true;
                case R.id.id_action_delete:
                    MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_DELETE).toString());
                    return true;
            }
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }

        return false;
    }

}
