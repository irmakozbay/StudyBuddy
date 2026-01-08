package msku.ceng.madproject.studybuddy;

/*Bahriye Gavaz*/

public class Group {
    private String id;
    private String name, description;
    private int iconResId;

    public Group() {}

    public Group(String id, String name, String description, int iconResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
    }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }

}