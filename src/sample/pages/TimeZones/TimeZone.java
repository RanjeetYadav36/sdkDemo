package sample.pages.TimeZones;

public class TimeZone {

    public TimeZone(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public TimeZone() {
    }

    public String id;
    public String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
