package net.caprazzi.giddone.worker;

import com.google.inject.Inject;
import net.caprazzi.giddone.parsing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class SourceCodeParser {

    private static final Logger Log = LoggerFactory.getLogger(SourceCodeParser.class);

    private final SourceFileScanner scanner;
    private final CommentParser commentParser;
    private final TodoParser todoParser;

    @Inject
    public SourceCodeParser(SourceFileScanner scanner, CommentParser commentParser, TodoParser todoParser) {
        this.scanner = scanner;
        this.commentParser = commentParser;
        this.todoParser = todoParser;
    }

    public Iterable<Todo> parse(Path source) throws IOException {
        Iterable<SourceFile> sourceFiles = scanner.scan(source);
        Iterable<CommentLine> comments = commentParser.parse(sourceFiles);
        return todoParser.parse(comments);
    }
}
