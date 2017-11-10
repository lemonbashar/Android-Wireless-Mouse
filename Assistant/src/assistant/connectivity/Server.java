package assistant.connectivity;


import assistant.Assistant;
import assistant.events.MessageListener;
import assistant.utility.Report;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private static boolean isBusyToFileDownload;
    private ServerSocket server;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    public boolean isConnected=false;
    private MessageListener listener;
    private MouseThread mouseThread;

    public Server(MessageListener listener) {
        this.listener = listener;
    }

    public void sendMessage(String message){
        if(writer!=null&&!writer.checkError()){
            writer.println(message);
            writer.flush();
        }
    }

    public void close(boolean notifyClient){
        if(notifyClient)
            sendMessage(new StringBuilder().append(Report.BASIC_CONFIG).append(Report.BASIC_CONFIG_CONNECTION_CLOSE).toString());
        closeSockets();
        closeServer();
    }

    private void closeServer() {
        try {
            if(server!=null)
                server.close();
        } catch (Exception e) {
            showError(e);
        }
        server=null;
    }

    private void closeSockets() {
        try {
            isConnected=false;
            if(writer!=null){
                writer.flush();
                writer.close();
            }
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    showError(e);
                }
            }
            writer=null;
            reader=null;
            try {
                if(socket!=null)
                    socket.close();
            } catch (Exception e) {
                showError(e);
            }

            socket=null;
            listener=null;
        } catch (Exception e) {
            showError(e);
        }
    }

    private boolean hasCommand(String message){
        if(message.charAt(0)==Report.BASIC_CONFIG){
            if(message.charAt(1)==Report.BASIC_CONFIG_CONNECTION_CLOSE) {
                isConnected=false;
                mouseThread.interrupt();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    showError(e);
                }
                listener.setStatus(Assistant.SERVER_IS_DISCONNECTED);
                runServer();
                return true;
            }
            else if(message.charAt(1)==Report.BASIC_CONFIG_CONNECTED){
                listener.setStatus(Assistant.SERVER_IS_CONNECTED);
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        runServer();
    }

    private void runServer() {
        mouseThread=new MouseThread();
        mouseThread.start();
    }



    private class MouseThread extends Thread {

        public MouseThread() {
            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            isConnected=true;
            try {
                try {
                    server=new ServerSocket(Assistant.TCP_PORT);
                } catch (IOException e) {
                    showError(e);
                }
                listener.setStatus(Assistant.SERVER_IS_RUNNING);
                try {
                    socket=server.accept();
                } catch (IOException e) {
                    showError(e);
                }
                listener.setStatus(Assistant.SERVER_IS_CONNECTED);
                try {
                    writer=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                    writer.flush();
                    reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    showError(e);
                }

                sendMessage(new StringBuilder().append(Report.BASIC_CONFIG).append(Report.BASIC_CONFIG_CONNECTED).toString());

                String msg=null;
                while (isConnected){
                    try {
                        msg=reader.readLine();
                    } catch (Exception e) {
                        showError(e);
                        continue;
                    }
                    if(msg!=null){
                        if (hasCommand(msg))
                            continue;
                        listener.onListen(msg);
                    }
                }

            } catch (Exception e) {
                showError(e);
            } finally {
                System.out.println("Reach The End...");
            }
        }
    }


    private void showError(Exception e) {
        e.printStackTrace();
        /*System.out.println(".........From Show Error..........");
        e.printStackTrace();
        System.out.println(".........From Show Error..........");*/
    }

}
