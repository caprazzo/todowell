package net.caprazzi.giddone.parsing;

import com.google.common.base.Optional;

import java.util.ArrayList;

public class TodoParser {

    public final Optional<Todo> parse(CommentLine commentLine) {
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

    public Iterable<Todo> parse(Iterable<CommentLine> comments) {
        ArrayList<Todo> todos = new ArrayList<Todo>();
        for(CommentLine line : comments) {
            String comment = line.getComment();
            if (isLabel("TODO", comment)) {
                todos.add(new Todo(line, "TODO", extractTodo("TODO", comment)));
            }
        }
        return todos;
    }

}
