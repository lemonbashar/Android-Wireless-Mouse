package com.garland.wifimouse.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.utility.Configure;
import com.garland.wifimouse.utility.Report;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by lemon on 8/30/2017.
 */

public class AdvancedFragment extends Fragment {
    private View mainView;
    private Configure configure;
    private Spinner spinner;
    public TextView timeView;
    private int date;
    private int month;
    private int year;
    ///To DO: create a time picker dialog and get time from there....


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_advanced,container,false);
        setHasOptionsMenu(true);
        try {
            configure =new Configure();
            mainView.findViewById(R.id.id_show_panel_mngr).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                MainActivity.sendTask(String.valueOf(Report.CONTROLS) + Report.CONTROLS_CTRL_PRESSED);
                MainActivity.sendTask(String.valueOf(Report.CONTROLS) + Report.CONTROLS_ALT_PRESSED);
                MainActivity.sendTask(String.valueOf(Report.KEY_ACTION) + Report.ACTION_DELETE);
                MainActivity.sendTask(String.valueOf(Report.CONTROLS) + Report.CONTROLS_ALT_RELEASED);
                MainActivity.sendTask(String.valueOf(Report.CONTROLS) + Report.CONTROLS_CTRL_RELEASED);
                }
            });
            configure.initControlButtons(mainView);
            configure.initDirectionButtons(mainView);
            configure.initFuncButtons(mainView);
            spinner= (Spinner) mainView.findViewById(R.id.id_advanced_spinner);
            timeView= (TextView) mainView.findViewById(R.id.id_select_time);
            final Calendar calendar=Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
            date=calendar.get(Calendar.DATE);
            month=calendar.get(Calendar.MONTH);
            year=calendar.get(Calendar.YEAR);
            timeView.setText(new StringBuilder().append(calendar.get(Calendar.HOUR)).append(":").append(calendar.get(Calendar.MINUTE)).toString());
            timeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

                    builder.setTitle("Select The Time");
                    View view=((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_time_picker,null);
                    final Button dateButton= (Button) view.findViewById(R.id.id_select_date);

                    dateButton.setText(new StringBuilder().append(date).append(".").append(month+1).append(".").append(year).toString());
                    dateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder1=new AlertDialog.Builder(getActivity());
                            View dPicker= ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_date_picker,null);
                            final DatePicker datePicker= (DatePicker) dPicker.findViewById(R.id.date_picker);
                            builder1.setView(dPicker);
                            builder1.setTitle("Select Date");
                            builder1.setNegativeButton(R.string.cancel,null);
                            builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    date=datePicker.getDayOfMonth();
                                    month=datePicker.getMonth();
                                    year=datePicker.getYear();
                                    dateButton.setText(new StringBuilder().append(date).append(".").append(month).append(".").append(year).toString());
                                    dialog.dismiss();
                                }
                            });
                            builder1.create().show();

                        }
                    });
                    final TimePicker timePicker= (TimePicker) view.findViewById(R.id.timePicker);
                    builder.setView(view);
                    builder.setCancelable(false);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            timeView.setText(new StringBuilder().append(timePicker.getCurrentHour()).append(":").append(timePicker.getCurrentMinute()).toString());
                            MainActivity.TIME_IN_SECONDS =Configure.getInterval(date,month,year,timePicker.getCurrentHour(),timePicker.getCurrentMinute());
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();
                }
            });
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, Report.COMMAND_LIST);
            spinner.setAdapter(adapter);
            mainView.findViewById(R.id.id_button_advanced_action_apply).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("User Confirmation");
                    builder.setMessage("Are you sure to Apply this Action..");
                    builder.setCancelable(true);
                    builder.setNegativeButton("Cancel",null);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.sendTask(new StringBuilder().append(String.valueOf(Report.COMMANDS)).append(spinner.getSelectedItem().toString().charAt(0)).append(Report.DIVIDER_FIRST).append(MainActivity.TIME_IN_SECONDS).toString());
                        }
                    });
                    builder.create().show();
                }
            });
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_advanced,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            MainActivity.sendTask(new StringBuilder().append(Report.CONTROLS).append(Report.CONTROLS_ALT_PRESSED).toString());
            MainActivity.sendTask(new StringBuilder().append(Report.CONTROLS).append(Report.CONTROLS_CTRL_PRESSED).toString());
            switch (item.getItemId()) {
                case R.id.action_rotate_90_deg:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_LEFT).toString());
                    break;
                case R.id.action_rotate_180_deg:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_DOWN).toString());
                    break;
                case R.id.action_rotate_270_deg:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_RIGHT).toString());
                    break;
                case R.id.action_rotate_360_deg:
                    MainActivity.sendTask(new StringBuilder().append(Report.KEY_ACTION).append(Report.ACTION_KEY_UP).toString());
                    break;
            }

            MainActivity.sendTask(new StringBuilder().append(Report.CONTROLS).append(Report.CONTROLS_ALT_RELEASED).toString());
            MainActivity.sendTask(new StringBuilder().append(Report.CONTROLS).append(Report.CONTROLS_CTRL_RELEASED).toString());
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        return super.onOptionsItemSelected(item);
    }
}
