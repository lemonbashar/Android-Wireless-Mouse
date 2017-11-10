package assistant;

import assistant.connectivity.Server;
import assistant.events.MessageListener;
import assistant.utility.Report;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Assistant extends JFrame implements Report {

    public static final String SERVER_IS_RUNNING = "Server Is Running...";
    public static final String SERVER_IS_CONNECTED = "Server Is Connected...";
    public static final String SERVER_IS_DISCONNECTED = "Server is Disconnected...";
    public static final Image ICON_FILE = new ImageIcon("com/lemon/assistant/utility/icon_mouse.png").getImage();
    public static final String SERVER_IS_NOT_RUNNING = "Server Is Not Running...";
    public static int TCP_PORT= Report.TCP_PORT;


    private JButton start,stop;
    private JLabel statusLabel =new JLabel(SERVER_IS_NOT_RUNNING);
    private Server server;
    private Robot robot;
    private boolean isBusy=false;
    private Vector<String> taskList;

    public static void main(String[] args) {
        Assistant assistant=new Assistant();
        assistant.setSize(400,500);
        assistant.setVisible(true);
        assistant.setResizable(false);
        assistant.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private Assistant() {
        super("Wireless Mouse Assistant..");
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException ignored) {

        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
        setIconImage(ICON_FILE);
        setLayout(new BorderLayout());

        JPanel topPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        start=new JButton("Start");
        start.addActionListener(e->startServer());
        stop=new JButton("Stop");
        stop.addActionListener(e->close());
        enableStart(true);
        topPanel.add(start);
        topPanel.add(stop);
        add(topPanel,BorderLayout.NORTH);

        add(statusLabel,BorderLayout.SOUTH);

        JPanel centerPanel=new JPanel(new FlowLayout());
        add(centerPanel,BorderLayout.CENTER);

        try {
            robot=new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        taskList=new Vector<>();
    }

    private void close() {
        Assistant.this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        enableStart(true);
        statusLabel.setText("Server is Disconnected...");
        if(server!=null)
            server.close(true);
        server=null;
    }

    private void startServer() {
        Assistant.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        enableStart(false);

        server=new Server(new Listener());
        server.start();
    }



    private void listen(String message) {
        isBusy=true;
        switch (message.charAt(0)) {
            case BASIC_CONFIG:
                checkBasic(message.charAt(1));
                break;
            case MOUSE:
                checkMouse(message);
                break;
            case KEY_ACTION:
                checkActions(message.charAt(1));
                break;
            case CONTROLS:
                checkControls(message.charAt(1));
                break;
            case COMMANDS:
                checkCommands(message);
                break;
            case SETTERS:
                checkSetters(message);
                break;
            case FUNCTION:
                checkFunction(message.charAt(1));
                break;
        }
        isBusy=false;
    }


    private void enableStart(boolean enable) {
        start.setEnabled(enable);
        stop.setEnabled(!enable);
    }

    private void checkBasic(char key) {
        switch (key) {
            case BASIC_CONFIG_SERVER_CLOSE_BOTH:
                close();
                dispose();
                break;
        }
    }


    @SuppressWarnings("unused")
    private void checkMouse(String message) {
        switch (message.charAt(1)){
            case MOUSE_CURSOR_MOVE:
                mouseMoving(message);
                break;
            case MOUSE_LEFT_CLICK:
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case MOUSE_RIGHT_CLICK:
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                return;
            case MOUSE_LEFT_PRESSED:
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                break;
            case MOUSE_LEFT_RELEASE:
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case MOUSE_V_SCROLL:
                if(message.charAt(3)=='-')robot.mouseWheel(1);
                else robot.mouseWheel(-1);
                break;
            case MOUSE_MIDDLE_CLICK:
                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON2_MASK);
                break;
            case MOUSE_V_SCROLL_UP:
                robot.mouseWheel(7);
                break;
            case MOUSE_V_SCROLL_DOWN:
                robot.mouseWheel(-7);
                break;
        }
    }

    @SuppressWarnings("unused")
    private void checkActions(char key) {
        int keyToPress=KeyEvent.VK_SHIFT;
        switch (key) {
            case ACTION_KEY_LEFT:
                keyToPress=KeyEvent.VK_LEFT;
                break;
            case ACTION_KEY_RIGHT:
                keyToPress=KeyEvent.VK_RIGHT;
                break;
            case ACTION_KEY_UP:
                keyToPress=KeyEvent.VK_UP;
                break;
            case ACTION_KEY_DOWN:
                keyToPress=KeyEvent.VK_DOWN;
                break;
            case ACTION_KEY_ENTER:
                keyToPress=KeyEvent.VK_ENTER;
                break;
            case ACTION_DELETE:
                keyToPress=KeyEvent.VK_DELETE;
                break;
            case ACTION_KEY_ESCAPE:
                keyToPress=KeyEvent.VK_ESCAPE;
                break;
            case ACTION_KEY_TAB:
                keyToPress=KeyEvent.VK_TAB;
                break;
            case ACTION_KEY_HOME:
                keyToPress=KeyEvent.VK_HOME;
                break;
            case ACTION_KEY_END:
                keyToPress=KeyEvent.VK_END;
                break;
            case ACTION_CUT:
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_X);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                return;
            case ACTION_COPY:
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_C);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                return;
            case ACTION_PASTE:
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                return;
        }
        robot.keyPress(keyToPress);
    }

    @SuppressWarnings("unused")
    private void checkControls(char key) {
        switch (key) {
            case CONTROLS_ALT_PRESSED:
                robot.keyPress(KeyEvent.VK_ALT);
                break;
            case CONTROLS_ALT_RELEASED:
                robot.keyRelease(KeyEvent.VK_ALT);
                break;
            case CONTROLS_CTRL_PRESSED:
                robot.keyPress(KeyEvent.VK_CONTROL);
                break;
            case CONTROLS_CTRL_RELEASED:
                robot.keyRelease(KeyEvent.VK_CONTROL);
                break;
            case CONTROLS_SHIFT_PRESSED:
                robot.keyPress(KeyEvent.VK_SHIFT);
                break;
            case CONTROLS_SHIFT_RELEASED:
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
        }
    }

    @SuppressWarnings("unused")
    private void checkCommands(String msg) {
        String time=msg.substring(msg.indexOf(DIVIDER_FIRST)+1);

        switch (msg.charAt(1)) {
            case COMMANDS_SHUT_DOWN:
                try {
                    Runtime.getRuntime().exec("shutdown /s /t "+time+" /f");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case COMMANDS_RESTART:
                try {
                    Runtime.getRuntime().exec("shutdown /r /t "+time+" /f");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case COMMANDS_QUICK_RESTART:
                try {
                    Runtime.getRuntime().exec("shutdown /f");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case COMMANDS_ABORT_ALL_ACTION:
                try {
                    Runtime.getRuntime().exec("shutdown /a");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case COMMANDS_HIBERNATE:
                try {
                    Runtime.getRuntime().exec("shutdown /h /t "+time+" /f");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @SuppressWarnings("unused")
    private void checkSetters(String message) {
        String text=message.substring(message.indexOf(DIVIDER_FIRST)+1);
        StringSelection selec= new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selec, selec);

        switch (message.charAt(1)) {
            case SETTERS_SET_TEXT:
                checkActions(ACTION_PASTE);
                break;
            case SETTERS_SET_TEXT_TO_WIN_CLIP_BOARD:
                break;
        }
    }


    @SuppressWarnings("unused")
    private void checkFunction(char key) {
        int keyToPress=KeyEvent.VK_SHIFT;
        switch (key) {
            case F_ONE:
                keyToPress=KeyEvent.VK_F1;
                break;
            case F_TWO:
                keyToPress=KeyEvent.VK_F2;
                break;
            case F_THREE:
                keyToPress=KeyEvent.VK_F3;
                break;
            case F_FOUR:
                keyToPress=KeyEvent.VK_F4;
                break;
            case F_FIVE:
                keyToPress=KeyEvent.VK_F5;
                break;
            case F_SIX:
                keyToPress=KeyEvent.VK_F6;
                break;
            case F_SEVEN:
                keyToPress=KeyEvent.VK_F7;
                break;
            case F_EIGHT:
                keyToPress=KeyEvent.VK_F8;
                break;
            case F_NINE:
                keyToPress=KeyEvent.VK_F9;
                break;
            case F_TEN:
                keyToPress=KeyEvent.VK_F10;
                break;
            case F_ELEVEN:
                keyToPress=KeyEvent.VK_F11;
                break;
            case F_TWELVE:
                keyToPress=KeyEvent.VK_F12;
                break;
        }

        robot.keyPress(keyToPress);
    }

    /*Robots*/

    private void mouseMoving(String message) {
        System.out.println(message);
        int div1=message.indexOf(DIVIDER_FIRST);
        int div2=message.indexOf(DIVIDER_SECOND);
        int x=Integer.parseInt(message.substring(div1+1,div2));
        int y=Integer.parseInt(message.substring(div2+1));

        Point point=MouseInfo.getPointerInfo().getLocation();
        point.x+=x;
        point.y+=y;
        robot.mouseMove(point.x,point.y);
    }

    private class TaskReader extends Thread {
        private Vector<String> messages;

        TaskReader(Vector<String> messages) {
            this.messages = messages;
            setPriority(MAX_PRIORITY);
        }

        @Override
        public void run() {
            for(String message:messages)
                listen(message);
        }
    }

    private class Listener implements MessageListener {

        @Override
        public void onListen(String message) {
            if(!isBusy) {
                try {
                    listen(message);
                    if(!taskList.isEmpty()){
                        new TaskReader(new Vector<>(taskList)).start();
                        taskList.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else taskList.add(message);
        }

        @Override
        public void setStatus(String status) {
            statusLabel.setText(status);
        }
    }
}
