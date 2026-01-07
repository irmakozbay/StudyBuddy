package msku.ceng.madproject.studybuddy;

public class Note {
    private String noteId;
    private String title;
    private String content;
    private String userId;
    private String userName;
    private String postType;

    public Note() { }

    public Note(String noteId, String title, String content, String userId) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }
}