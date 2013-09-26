package net.caprazzi.giddone.parsing;

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
