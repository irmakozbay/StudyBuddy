package msku.ceng.madproject.studybuddy;

/*Bahriye Gavaz*/

public class Notification {
    private String title;
    private String message;
    private long timestamp;

    // Firebase için boş constructor şart
    public Notification() {}

    public Notification(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getter ve Setterlar
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}