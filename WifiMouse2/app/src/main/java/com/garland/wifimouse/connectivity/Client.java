package com.garland.wifimouse.connectivity;

import android.os.Handler;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.utility.Report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by lemon on 8/20/2017.
 */

public class Client extends Thread {
    public static final int TASK_SEND = 12;
    public static final int SET_BACK_HANDLER = 432;
    private static PrintWriter outStream;
    private Socket socket;
    private BufferedReader inStream;
    public boolean isConnected=false;
    private Handler handler;

    public Client(Handler handler) {
        this.handler = handler;
        setPriority(MAX_PRIORITY);
    }

    public void sendTask(String task) {
        if(outStream!=null) {
            outStream.println(task);
            outStream.flush();
        }
    }

    public void disconnect(boolean notifyServer) {
        sendTask(new StringBuffer().append(Report.MOUSE).append(Report.MOUSE_LEFT_RELEASE).toString());
        sendTask(new StringBuffer().append(Report.CONTROLS).append(Report.CONTROLS_ALT_RELEASED).toString());
        sendTask(new StringBuffer().append(Report.CONTROLS).append(Report.CONTROLS_CTRL_RELEASED).toString());
        sendTask(new StringBuffer().append(Report.CONTROLS).append(Report.CONTROLS_SHIFT_RELEASED).toString());
        if(notifyServer)sendTask(String.valueOf(Report.BASIC_CONFIG) + Report.BASIC_CONFIG_CONNECTION_CLOSE);
        isConnected=false;
    }
	
	private void closeAll() {
        handler.obtainMessage(MainActivity.STATE_CHANGED,MainActivity.DISCONNECTED).sendToTarget();
		try {
            if(outStream!=null)
                outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(inStream!=null)
                inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(socket!=null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    @Override
    public void run() {
        startSocket();
    }

    private void startSocket() {
        try {

            handler.obtainMessage(MainActivity.STATE_CHANGED,MainActivity.STARTED).sendToTarget();
            socket=new Socket(MainActivity.IP_ADDRESS,MainActivity.DEDICATED_PORT);
            handler.obtainMessage(MainActivity.STATE_CHANGED,MainActivity.CONNECTED).sendToTarget();

            outStream=new PrintWriter(socket.getOutputStream());
            outStream.println(new StringBuilder().append(Report.BASIC_CONFIG).append(Report.BASIC_CONFIG_CONNECTED).toString());
            outStream.flush();
            inStream=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            isConnected=true;
            sendTask(new StringBuffer().append(Report.MOUSE).append(Report.MOUSE_LEFT_RELEASE).toString());
            sendTask(new StringBuffer().append(Report.CONTROLS).append(Report.CONTROLS_ALT_RELEASED).toString());
            sendTask(new StringBuffer().append(Report.CONTROLS).append(Report.CONTROLS_CTRL_RELEASED).toString());
            sendTask(new StringBuffer().append(Report.CONTROLS).append(Report.CONTROLS_SHIFT_RELEASED).toString());
            String msg=null;
            while(isConnected) {
                try{
					msg=inStream.readLine();
				} catch(Exception e) {
                    MainActivity.crushReport(e);
				}
                if(msg!=null)
                    handler.obtainMessage(MainActivity.CODE_CLIENT_LISTEN, msg).sendToTarget();
            }

        } catch (Exception e) {
            MainActivity.crushReport(e);
        } finally {
            closeAll();
        }
    }
}
