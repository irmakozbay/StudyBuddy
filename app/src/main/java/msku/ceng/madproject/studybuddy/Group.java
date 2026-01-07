package msku.ceng.madproject.studybuddy;

public class Group {
    private String id; // Firestore döküman ID'si
    private String name, description;
    private int iconResId;

    public Group() {} // Firebase için boş constructor şart

    public Group(String id, String name, String description, int iconResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
}