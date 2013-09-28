package net.caprazzi.giddone.parsing;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import net.caprazzi.giddone.Meters;
import net.caprazzi.giddone.model.CommentLine;
import net.caprazzi.giddone.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TodoParser {

    private static final Logger Log = LoggerFactory.getLogger(TodoParser.class);

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
        Log.info("Parsing started");
        ArrayList<Todo> todos = new ArrayList<Todo>();
        Timer.Context timer = Meters.system.todoParsing.time();
        try {
            for(CommentLine line : comments) {
                String comment = line.getComment();
                if (isLabel("TODO", comment)) {
                    todos.add(new Todo(line, "TODO", extractTodo("TODO", comment)));
                }
            }
            Log.info("Parsing completed with {} todo lines", todos.size());
            Meters.repositories.todos.update(todos.size());
        }
        catch(Exception ex) {
            Log.error("Parsing failure: {}", ex);
        }
        finally {
            timer.stop();
        }
        return todos;
    }

}
