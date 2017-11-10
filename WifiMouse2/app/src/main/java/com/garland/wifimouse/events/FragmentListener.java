package com.garland.wifimouse.events;

import java.util.List;

/**
 * Created by lemon on 9/12/2017.
 */

public interface FragmentListener {
    void onListen();
    void onListen(String task);
    void onListen(List<String> taskList);
}
