package msku.ceng.madproject.studybuddy;

/*Bahriye Gavaz*/

public class StudyLog {
    public String groupName;
    public double hours;
    public String note;
    public long timestamp;

    public StudyLog() {}

    public StudyLog(String groupName, double hours, String note) {
        this.groupName = groupName;
        this.hours = hours;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
    }
}