package net.caprazzi.todowell;

public enum CommentStrategy {
    DoubleSlash("//"),
    Hash("#");
    private final String singleCommentStart;

    CommentStrategy(String singleCommentStart) {
        this.singleCommentStart = singleCommentStart;
    }

    public String singleCommentStart() {
        return singleCommentStart;
    }
}
