package com.garland.wifimouse;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.garland.wifimouse.connectivity.Client;
import com.garland.wifimouse.events.FragmentListener;
import com.garland.wifimouse.fragments.AdvancedFragment;
import com.garland.wifimouse.fragments.ConfigureFragment;
import com.garland.wifimouse.fragments.ConnectionListFragment;
import com.garland.wifimouse.fragments.DeveloperFragment;
import com.garland.wifimouse.fragments.DialogRequest;
import com.garland.wifimouse.fragments.HelpFragment;
import com.garland.wifimouse.fragments.ManuelConnectionFragment;
import com.garland.wifimouse.fragments.MouseFragment;
import com.garland.wifimouse.fragments.SlideShowFragment;
import com.garland.wifimouse.utility.Configure;
import com.garland.wifimouse.utility.IpDetails;
import com.garland.wifimouse.utility.Report;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String VOL_BUTTON_PREF = "VOL_BUTTON_PREF";
    public static int DEDICATED_PORT = Report.TCP_PORT;
    public static final int CODE_CLIENT_LISTEN = 101;
    public static final int STATE_CHANGED = 201;
    private static final int PERMISSION_ACCESS_INTERNET = 101;
    public static long TIME_IN_SECONDS = 0L;
	public static final String DISCONNECTED="Disconnected";
	public static final String CONNECTED="Connected";
	public static final String STARTED="Started";

    private static Client client;

    private static String CURRENT_MSG="";
    private static String CURRENT_TITLE="";
    public static String ACTION_VOLUME_UP=new StringBuffer().append(Report.MOUSE).append(Report.MOUSE_V_SCROLL_UP).toString();
    public static String ACTION_VOLUME_DOWN=new StringBuffer().append(Report.MOUSE).append(Report.MOUSE_V_SCROLL_DOWN).toString();
    public static int ADD_MOUSE_SPEED=0;
    public static String IP_ADDRESS="";

    public static boolean vol_button_for_scrolling=true;


    private NavigationView navigationView;
    private SharedPreferences preferences;
    private static Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = null;
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.INTERNET));
                else {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PERMISSION_ACCESS_INTERNET);
                }
            }
        } catch (Exception e) {
            crushReport(e);
        }

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
        } catch (Exception e) {
            crushReport(e);        }

        preferences=getSharedPreferences(Report.SHARED_PREF_KEY,MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        try {
            handler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what){
                    case CODE_CLIENT_LISTEN:
                        listen((String) msg.obj);
                        break;
                    case STATE_CHANGED:
                        String text= (String) msg.obj;
                        switch(text) {
                            case DISCONNECTED:
                                disconnect();
                                break;
                        }
                        break;
                }
                }
            };

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        } catch (Exception e) {
            crushReport(e);
        }

        addPref(MouseFragment.SHOW_TEXT_POST,false);
        Fragment fragment=new MouseFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        analyzePreferences();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ACCESS_INTERNET:
                if(grantResults.length<=0||grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                    finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(client!=null) addPref(Report.MOUSE_PREV_CONNECTED_KEY,true);
        else addPref(Report.MOUSE_PREV_CONNECTED_KEY,false);
        super.onDestroy();
    }

    private void setPref() {
        addPref(Report.IP_ADDRESS_KEY,IP_ADDRESS);
        addPref(Report.CONNECT_OR_DISCONNECT_KEY, client !=null&& client.isConnected);
        addPref(Report.MOUSE_SPEED_KEY,ADD_MOUSE_SPEED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setPref();
        if(client!=null&&client.isConnected)
            disconnect();
    }

    @Override
    protected void onStart() {
        if(preferences.getBoolean(Report.CONNECT_OR_DISCONNECT_KEY,false)&&IP_ADDRESS.matches(Report.PATTERN_IP_ADDRESS))
            connectMouseAtLast();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Confirmation Dialog!");
                builder.setMessage("Do You want to Close the App?");
                builder.setCancelable(true);
                builder.setNegativeButton("Cancel",null);
                builder.setPositiveButton("Exit App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disconnect();
                        setPref();
                        finish();
                    }
                });

                builder.create().show();
            }
        } catch (Exception e) {
            crushReport(e);        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    sendTask(ACTION_VOLUME_DOWN);
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    sendTask(ACTION_VOLUME_UP);
                    return true;
            }
        } catch (Exception e) {
            crushReport(e);        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                sendTask(new StringBuffer().append(Report.MEDIA).append(Report.MEDIA_VOLUME_DOWN).toString());
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                sendTask(new StringBuffer().append(Report.MEDIA).append(Report.MEDIA_VOLUME_UP).toString());
                return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        try {
            Fragment fragment=null;
            switch(id) {
                case R.id.nav_mode_slideshow:
                    fragment=new SlideShowFragment();
                    break;
                case R.id.nav_mode_mouse:
                    addPref(MouseFragment.SHOW_TEXT_POST,false);
                    fragment=new MouseFragment();
                    break;
                case R.id.nav_mode_text_post:
                    addPref(MouseFragment.SHOW_TEXT_POST,true);
                    fragment=new MouseFragment();
                    break;
                case R.id.nav_mode_advanced://all key buttons without mouse touch pad with shutdown and restart
                    fragment=new AdvancedFragment();
                    break;
                case R.id.nav_hide_title:
                    try {
                        ActionBar actionBar=getSupportActionBar();
                        if(actionBar!=null){
                            if(item.getTitle().equals(getString(R.string.hide_title))){
                                actionBar.hide();
                                item.setTitle(R.string.show_title);
                            }
                            else {
                                item.setTitle(R.string.hide_title);
                                actionBar.show();
                            }
                        }
                    } catch (NullPointerException e) {
                        crushReport(e);                    }
                    break;
                case R.id.nav_exit_app:
					disconnect();
                    setPref();
                    finish();
                    break;
                case R.id.nav_connect_pc:
                    try {
                        if(client !=null&& client.isConnected){
                            setCurrentTitle("Connection Error!");
                            setCurrentMsg("Sorry, The Mouse already Connected!");
                            DialogRequest request=new DialogRequest();
                            request.show(getSupportFragmentManager(),"Error");
                        }
                        else{
                            connectMouse();
                        }
                    } catch (Exception e) {
                        crushReport(e);                    }
                    break;
                case R.id.nav_connect_pc_forcefully:
                    try {
                        if(client !=null)
                            client.disconnect(true);
                        client =null;
                        connectMouse();
                    } catch (Exception e) {
                        crushReport(e);                    }

                    break;
                case R.id.nav_connect_manually:
                    connectManually();
                    break;
                case R.id.nav_disconnect:
                    disconnect();
                    break;
                case R.id.nav_show_connection_list:
                    showConnectionFragment();
                    break;
                case R.id.nav_select_mouse_speed:
                    selectMouseSpeed();
                    break;
                case R.id.nav_configure://Configure something like use volume button's for slide presentation enable sound for click etc
                    fragment=new ConfigureFragment();
                    break;
                case R.id.nav_show_developer:
                    fragment=new DeveloperFragment();
                    break;
                case R.id.nav_copy:
                    sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_COPY).toString());
                    break;
                case R.id.nav_cut:
                    sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_CUT).toString());
                    break;
                case R.id.nav_paste:
                    sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_PASTE).toString());
                    break;
                case R.id.nav_delete:
                    sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_DELETE).toString());
                    break;
                case R.id.nav_close_current_win:
                    sendTask(new StringBuilder().append(String.valueOf(Report.CONTROLS)).append(Report.CONTROLS_ALT_PRESSED).toString());
                    sendTask(new StringBuilder().append(String.valueOf(Report.FUNCTION)).append(Report.F_FOUR).toString());
                    sendTask(new StringBuilder().append(String.valueOf(Report.CONTROLS)).append(Report.CONTROLS_ALT_RELEASED).toString());
                    break;
                case R.id.nav_delete_permanently:
                    sendTask(new StringBuilder().append(String.valueOf(Report.CONTROLS)).append(Report.CONTROLS_SHIFT_PRESSED).toString());
                    sendTask(new StringBuilder().append(String.valueOf(Report.KEY_ACTION)).append(Report.ACTION_DELETE).toString());
                    sendTask(new StringBuilder().append(String.valueOf(Report.CONTROLS)).append(Report.CONTROLS_SHIFT_RELEASED).toString());
                    break;
                case R.id.nav_help:
                    fragment=new HelpFragment();
                    break;
            }
            if(fragment!=null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            crushReport(e);        }
        return true;
    }

    private void listen(String message) {
        try {
            switch (message.charAt(0)){
                case Report.BASIC_CONFIG:
                    basicTask(message.charAt(1));
                    break;
            }
        } catch (Exception e) {
            crushReport(e);        }
    }

    private void selectMouseSpeed() {
        try {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            View view=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_mouse_speed,null);
            final SeekBar seekBar= (SeekBar) view.findViewById(R.id.seek_bar);
            seekBar.setProgress(MainActivity.ADD_MOUSE_SPEED);
            final TextView textView= (TextView) view.findViewById(R.id.seek_text);
            textView.setText(new StringBuilder().append("").append(MainActivity.ADD_MOUSE_SPEED).toString());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textView.setText(new StringBuilder().append("").append(progress).toString());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            builder.setView(view);
            builder.setTitle("Select Mouse Speed");
            builder.setNegativeButton(R.string.cancel,null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.ADD_MOUSE_SPEED=seekBar.getProgress();
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } catch (Exception e) {
            crushReport(e);        }
    }

    private void connectManually() {
        try {
            ManuelConnectionFragment fragment=new ManuelConnectionFragment();
            fragment.setListener(CONNECTION_CALLBACK_LISTENER);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } catch (Exception e) {
            crushReport(e);        }
    }

    private void addPref(String key, boolean value) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    private void addPref(String key, int value) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    private void addPref(String key, String value) {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    private void disconnect() {
        try {
            MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_status);
            menuItem.setTitle(DISCONNECTED);
            menuItem.setIcon(R.drawable.icon_disconnect);
        } catch (Exception e) {
            crushReport(e);        }
        try {
            if(client !=null)
                client.disconnect(true);
            client =null;
        } catch (Exception e) {
            crushReport(e);        }

        Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
    }

    private void connectMouse() {
        if(checkIp())
            connectMouseAtLast();
        else {
            showConnectionFragment();
        }
    }

    private boolean checkIp() {
        if(!IP_ADDRESS.matches(Report.PATTERN_IP_ADDRESS)) return false;
        for(IpDetails ips:Configure.fetchConnectionList())
            if(ips.getIpAddress().equals(IP_ADDRESS)) return true;
        return false;
    }

    private void showConnectionFragment() {
        try {
            ConnectionListFragment fragment=new ConnectionListFragment();
            fragment.setListener(CONNECTION_CALLBACK_LISTENER);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,fragment).commit();
        } catch (Exception e) {
            crushReport(e);        }
    }

    private void connectMouseAtLast() {
		if(client!=null && client.isConnected){
			disconnect();
		}
        try {
            client =new Client(handler);
            client.start();
            MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_status);
            menuItem.setTitle(R.string.status_connected);
            menuItem.setIcon(R.drawable.icon_connect);
        } catch (Exception e) {
            crushReport(e);        }
    }


    private void basicTask(char ch) {
        try {
            MenuItem menuItem=navigationView.getMenu().findItem(R.id.nav_status);
            switch (ch){
                case Report.BASIC_CONFIG_CONNECTION_CLOSE:
                    disconnect();
                    break;
                case Report.BASIC_CONFIG_CONNECTED:
                    menuItem.setTitle(R.string.status_connected);
                    menuItem.setIcon(R.drawable.icon_connect);
                    break;
                case Report.BASIC_CONFIG_SERVER_CLOSE_BOTH:
                    disconnect();
                    finish();
                    break;
            }
        } catch (Exception e) {
            crushReport(e);
        }
    }

    public static void sendTask(String task) {
        if(client !=null) {
            client.sendTask(task);
        }
    }

    public static String getCurrentMsg() {
        return CURRENT_MSG;
    }

    public static void setCurrentMsg(String currentMsg) {
        CURRENT_MSG = currentMsg;
    }

    public static String getCurrentTitle() {
        return CURRENT_TITLE;
    }

    public static void setCurrentTitle(String currentTitle) {
        CURRENT_TITLE = currentTitle;
    }

    private void analyzePreferences() {
        ACTION_VOLUME_DOWN=preferences.getString(ACTION_VOLUME_DOWN, new StringBuilder().append("").append(Report.MOUSE).append(Report.MOUSE_V_SCROLL).append(Report.DIVIDER_FIRST).append("-4").toString());
        ACTION_VOLUME_UP=preferences.getString(ACTION_VOLUME_UP, new StringBuilder().append("").append(Report.MOUSE).append(Report.MOUSE_V_SCROLL).append(Report.DIVIDER_FIRST).append("4").toString());
        IP_ADDRESS=preferences.getString(Report.IP_ADDRESS_KEY,"");
        ADD_MOUSE_SPEED=preferences.getInt(Report.MOUSE_SPEED_KEY,0);
        vol_button_for_scrolling=preferences.getBoolean(VOL_BUTTON_PREF,true);
        if(!vol_button_for_scrolling) {
            MainActivity.ACTION_VOLUME_UP=new StringBuffer().append(Report.KEY_ACTION).append(Report.ACTION_KEY_RIGHT).toString();
            MainActivity.ACTION_VOLUME_DOWN=new StringBuffer().append(Report.KEY_ACTION).append(Report.ACTION_KEY_LEFT).toString();
        }
    }

    private final FragmentListener CONNECTION_CALLBACK_LISTENER = new FragmentListener() {
        @Override
        public void onListen() {
        }

        @Override
        public void onListen(List<String> listTask) {
            IP_ADDRESS=listTask.get(0);
            connectMouseAtLast();
        }

        @Override
        public void onListen(String task) {
            IP_ADDRESS=task;
            connectMouseAtLast();
        }
    };


    @SuppressWarnings("unused")
    public static void crushReport(Exception e) {
        StackTraceElement trace=e.getStackTrace()[0];
        String report=new StringBuffer().append("FileName:").append(trace.getFileName())
                .append("[]Class Name:").append(trace.getClassName())
                .append("[]Method Name:").append(trace.getMethodName())
                .append("[]Line Number:").append(trace.getLineNumber())
                .append("[]Message:").append(e.getMessage())
                .append("[]Cause:").append(e.getCause()).toString();

        String databaseKeyToPush=new StringBuffer().append(trace.getClassName())
                .append("|").append(trace.getMethodName())
                .append("|").append(trace.getLineNumber()).toString();
        e.printStackTrace();
    }

}
