package com.garland.wifimouse.utility;

/**
 * Created by lemon on 8/20/2017.
 */

public interface Report {

    String SHARED_PREF_KEY="5PC6GL26GNG6G2EM;615E44MD;=NH=41=0G";
    String IP_ADDRESS_KEY = "E8AM@P4Q5;A3AA07I1H:=05M>47:EMD3";
    String CONNECT_OR_DISCONNECT_KEY = "?706AO6G1:APE;?706AO6GGQ;615E44MD;=NH=41=0G";
    String MOUSE_SPEED_KEY = "I77;AG58AQ@GGQ;615E44MD;=NH=41=0G";
    String MOUSE_PREV_CONNECTED_KEY = "I77;AG2:A>AO160Q?<APA3AA07I1H:=05M>47:EMD3";
    /*Basic Config*/
    char BASIC_CONFIG='B';
    int TCP_PORT=8935;
    char BASIC_CONFIG_CONNECTION_CLOSE ='C';
    char BASIC_CONFIG_CONNECTED ='c';
    char BASIC_CONFIG_SERVER_CLOSE_BOTH='B';
    char BASIC_CONFIG_MESSAGE = 'M';

    /*Mouse Related*/
    char MOUSE='M';
    char MOUSE_LEFT_CLICK='L';
    char MOUSE_RIGHT_CLICK='R';
    char MOUSE_LEFT_PRESSED='p';
    char MOUSE_LEFT_RELEASE='r';
    char MOUSE_CURSOR_MOVE='m';
    char MOUSE_V_SCROLL='V';
    char MOUSE_V_SCROLL_UP = 'u';
    char MOUSE_MIDDLE_CLICK = 'Z';
    char MOUSE_V_SCROLL_DOWN = 'd';

    /*Hot Key Action's */
    char KEY_ACTION ='K';
    char ACTION_KEY_UP='U';
    char ACTION_KEY_DOWN='D';
    char ACTION_KEY_LEFT='L';
    char ACTION_KEY_RIGHT='R';
    char ACTION_KEY_ENTER='E';
    char ACTION_KEY_CUSTOM='C';
    char ACTION_DELETE ='d';
    char ACTION_KEY_ESCAPE='h';
    char ACTION_KEY_TAB='T';
    char ACTION_KEY_HOME='H';
    char ACTION_KEY_END='e';
    char ACTION_CUT='X';
    char ACTION_COPY='x';
    char ACTION_PASTE='V';

    /*Controls*/
    char CONTROLS='C';
    char CONTROLS_CTRL_PRESSED='C';
    char CONTROLS_CTRL_RELEASED='D';
    char CONTROLS_SHIFT_PRESSED='S';
    char CONTROLS_SHIFT_RELEASED='T';
    char CONTROLS_ALT_PRESSED='A';
    char CONTROLS_ALT_RELEASED='B';

    /*Dividers*/
    char DIVIDER_FIRST='~';
    char DIVIDER_SECOND='@';
    char DIVIDER_THIRD='#';

    /*Commands*/
    char COMMANDS='c';
    char COMMANDS_SHUT_DOWN='S';
    char COMMANDS_RESTART='R';
    char COMMANDS_QUICK_RESTART='Q';
    char COMMANDS_HIBERNATE='H';
    char COMMANDS_ABORT_ALL_ACTION='A';

    String[] COMMAND_LIST={"SHUT DOWN","RESTART","QUICK RESTART","HIBERNATE","ABORT ALL ACTION"};

    /*Setters*/
    char SETTERS='S';
    char SETTERS_SET_TEXT='T';//from a text box with a button post,if client click post then it send the text to current focussed text box and behind the scene it clip the text and then paste...
    char SETTERS_SET_TEXT_TO_WIN_CLIP_BOARD='C';

    /*Media Controls*/
    char MEDIA='m';
    char MEDIA_PLAY='P';
    char MEDIA_PAUSE='p';
    char MEDIA_NEXT='N';
    char MEDIA_PREV='n';
    char MEDIA_VOLUME_UP='V';
    char MEDIA_VOLUME_DOWN='v';

    /*Functional Keys*/
    char FUNCTION='F';
    char F_ONE='O';
    char F_TWO='T';
    char F_THREE='t';
    char F_FIVE='F';
    char F_FOUR='f';
    char F_SIX='S';
    char F_SEVEN='s';
    char F_EIGHT='E';
    char F_NINE='N';
    char F_TEN='x';
    char F_ELEVEN='y';
    char F_TWELVE='z';


    /*Pattern Regex..*/
    String PATTERN_IP_ADDRESS="[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}";


}
