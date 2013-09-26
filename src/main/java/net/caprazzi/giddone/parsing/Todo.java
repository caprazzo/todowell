package net.caprazzi.giddone.parsing;

import net.caprazzi.giddone.parsing.CommentLine;

public class Todo {
    private final CommentLine comment;
    private final String label;
    private final String todo;

    public Todo(CommentLine comment, String label, String todo) {
        this.comment = comment;
        this.label = label;
        this.todo = todo;
    }

    public CommentLine getComment() {
        return comment;
    }

    public String getLabel() {
        return label;
    }

    public String getTodo() {
        return todo;
    }
}
