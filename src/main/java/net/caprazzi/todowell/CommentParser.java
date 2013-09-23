package net.caprazzi.todowell;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class CommentParser {

    public final Iterable<CommentLine> parse(SourceFile source) throws IOException {
        LinkedList<CommentLine> comments = new LinkedList<CommentLine>();

        LineIterator it = FileUtils.lineIterator(source.getFile().toFile(), "UTF-8");
        try {
            int lineNumber = 1;
            while (it.hasNext()) {
                String line = it.nextLine();
                if (isComment(line, source.getLanguage())) {
                    comments.add(new CommentLine(source, lineNumber, line));
                }
                lineNumber++;
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        return comments;
    }

    private boolean isComment(String line, Language language) {
        return line != null && line.trim().startsWith(language.getCommentStrategy().singleCommentStart());
    }

    public Iterable<CommentLine> parse(Iterable<SourceFile> sourceFiles) throws IOException {
        ArrayList<CommentLine> commentLines = new ArrayList<CommentLine>();
        for(SourceFile file : sourceFiles) {
            for (CommentLine line : parse(file)) {
                commentLines.add(line);
            }
        }
        return commentLines;
    }
}
