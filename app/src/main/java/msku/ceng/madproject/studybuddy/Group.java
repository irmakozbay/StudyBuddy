package msku.ceng.madproject.studybuddy;

public class Group {
    private String id; // Firestore döküman ID'si
    private String name, description;
    private int iconResId;

    // 1. Firebase için boş constructor (MUTLAKA OLMALI)
    public Group() {}

    // 2. Dolu constructor
    public Group(String id, String name, String description, int iconResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
    }

    // 3. SETTER METOTLARI (Hatanın çözümü buradadır)
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    // 4. GETTER METOTLARI
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
}