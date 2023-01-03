package database;

public class CommentsContext {
    public CommentsContext(int id, int ownerId, String comment) {
        this.id = id;
        this.ownerId = ownerId;
        this.comment = comment;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getComment() {
        return comment;
    }

    public int getId() {
        return id;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private final int id;
    private final int ownerId;
    private String comment;
}
