package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

public class Material {
    private String materialId;
    private String title;
    private String content;
    private String userId;
    private String postType;

    public Material() { }

    public Material(String materialId, String title, String description, String userId) {
        this.materialId = materialId;
        this.title = title;
        this. content = content;
        this.userId = userId;
    }

    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String description) { this.content = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

}