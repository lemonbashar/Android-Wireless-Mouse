package assistant.events;

/**
 * Created by lemon on 9/3/2017.
 */
public interface MessageListener {
    void onListen(String message);
    void setStatus(String status);
}
