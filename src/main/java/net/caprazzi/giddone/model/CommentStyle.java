package net.caprazzi.giddone.model;

public enum CommentStyle {
    Java("//"),
    Shell("#");
    private final String singleCommentStart;

    CommentStyle(String singleCommentStart) {
        this.singleCommentStart = singleCommentStart;
    }

    public String singleCommentStart() {
        return singleCommentStart;
    }
}
