package net.caprazzi.gitdone.parser;

import com.google.common.base.Optional;

public class TodoParser {

    public static Optional<Todo> parse(CommentLine commentLine) {
        String comment = commentLine.getComment();

        if (isLabel("TODO", comment)) {
            return Optional.of(new Todo(commentLine, "TODO", extractTodo("TODO", comment)));
        }

        return Optional.absent();
    }

    private static boolean isLabel(String label, String comment) {
        return comment.trim().toUpperCase().startsWith(label.toUpperCase() + ":");
    }

    private static String extractTodo(String label, String comment) {
        return comment.trim().substring(label.length()+1).trim();
    }

    private static String removeCommentStart(String comment, String start) {
        return comment.trim().substring(start.length()).trim();
    }

    public static class Todo {
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
}
