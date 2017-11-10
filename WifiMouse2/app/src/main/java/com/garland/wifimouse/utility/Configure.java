package com.garland.wifimouse.utility;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.events.ButtonWithActions;
import com.garland.wifimouse.events.ButtonWithControl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by lemon on 9/1/2017.
 */

public class Configure {

    public void initControlButtons(View mainView) {
        mainView.findViewById(R.id.id_btn_ctrl).setOnClickListener(new ButtonWithControl(Report.CONTROLS_CTRL_RELEASED,Report.CONTROLS_CTRL_PRESSED,(RadioButton)mainView.findViewById(R.id.id_btn_ctrl_radio)));
        mainView.findViewById(R.id.id_btn_alt).setOnClickListener(new ButtonWithControl(Report.CONTROLS_ALT_RELEASED,Report.CONTROLS_ALT_PRESSED,(RadioButton)mainView.findViewById(R.id.id_btn_alt_radio)));
        mainView.findViewById(R.id.id_btn_shift).setOnClickListener(new ButtonWithControl(Report.CONTROLS_SHIFT_RELEASED,Report.CONTROLS_SHIFT_PRESSED,(RadioButton)mainView.findViewById(R.id.id_btn_shift_radio)));
        mainView.findViewById(R.id.id_btn_del).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_DELETE));
    }

    public void initDirectionButtons(View mainView) {
        mainView.findViewById(R.id.id_btn_up).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_UP));
        mainView.findViewById(R.id.id_btn_down).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_DOWN));
        mainView.findViewById(R.id.id_btn_left).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_LEFT));
        mainView.findViewById(R.id.id_btn_right).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_RIGHT));
        mainView.findViewById(R.id.id_btn_enter).setOnClickListener(new ButtonWithActions(Report.KEY_ACTION,Report.ACTION_KEY_ENTER));
    }

    public void initFuncButtons(View mainView) {
        mainView.findViewById(R.id.id_function_btn_1).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_ONE));
        mainView.findViewById(R.id.id_function_btn_2).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_TWO));
        mainView.findViewById(R.id.id_function_btn_3).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_THREE));
        mainView.findViewById(R.id.id_function_btn_4).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_FOUR));
        mainView.findViewById(R.id.id_function_btn_5).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_FIVE));
        mainView.findViewById(R.id.id_function_btn_6).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_SIX));
        mainView.findViewById(R.id.id_function_btn_7).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_SEVEN));
        mainView.findViewById(R.id.id_function_btn_8).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_EIGHT));
        mainView.findViewById(R.id.id_function_btn_9).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_NINE));
        mainView.findViewById(R.id.id_function_btn_10).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_TEN));
        mainView.findViewById(R.id.id_function_btn_11).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_ELEVEN));
        mainView.findViewById(R.id.id_function_btn_12).setOnClickListener(new ButtonWithActions(Report.FUNCTION,Report.F_TWELVE));
    }

    public static List<IpDetails> fetchConnectionList() {
        List<IpDetails> list=new ArrayList<>();

        BufferedReader reader=null;

        try {
            reader =new BufferedReader(new FileReader("/proc/net/arp"));
            String line=null;

            while ((line=reader.readLine())!=null){
                String[] split=line.split(" +");
                if(split.length>=4){
                    String device=split[5];
                    String ip=split[0];
                    if(ip.length()<=3) continue;
                    String mac=split[3];
                    list.add(new IpDetails(ip.trim(),"Device:"+device,"Device Name:"+device+"\nIp Address:"+ip.trim()+"\nMac Address:"+mac));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(reader!=null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return list;
    }

    public static long getInterval (int day,int month,int year,int hour,int minute) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        long sys_time=calendar.getTimeInMillis();
        calendar.set(year,month,day,hour,minute,0);
        long commanded_time=calendar.getTimeInMillis();
        if(commanded_time<=sys_time)commanded_time=0;
        else commanded_time=commanded_time-sys_time;
        commanded_time=commanded_time/1000;
        return commanded_time;
    }

}
