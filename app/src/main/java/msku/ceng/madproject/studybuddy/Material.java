package msku.ceng.madproject.studybuddy;

public class Material {
    private String materialId;
    private String title;
    private String description; // "Subhead" dediğin kısım
    private String userId;
    private String postType;

    public Material() { } // Firebase için boş constructor

    public Material(String materialId, String title, String description, String userId) {
        this.materialId = materialId;
        this.title = title;
        this.description = description;
        this.userId = userId;
    }

    // Getter ve Setterlar
    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

}