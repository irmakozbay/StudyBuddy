package msku.ceng.madproject.studybuddy;

public class Material {
    private String id;
    private String fileName;
    private String fileUrl; // PDF veya resim linki olabilir
    private String userId;

    public Material() { }

    public Material(String id, String fileName, String fileUrl, String userId) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}