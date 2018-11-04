package np.com.naxa.staffattendance.common;

import java.util.HashMap;

public class MessageEvent {
    private String message;
    private HashMap<String, String> hashMap;

    public MessageEvent(String message) {
        this.hashMap = new HashMap<>();
        this.message = message;
    }

    public void addItem(String key, String value) {
        hashMap.put(key, value);
    }

    public String getItem(String key) {
        return hashMap.get(key);
    }

    public String getMessage() {
        return message;
    }


}
