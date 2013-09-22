package net.caprazzi.gitdone.parser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.util.LinkedList;

public class CommentScanner {

    public static Iterable<CommentLine> getComments(FileScanner.SourceFile source) throws IOException {
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

    public static boolean isComment(String line, FileScanner.Language language) {
        return line != null && line.trim().startsWith(language.getCommentStrategy().singleCommentStart());
    }

}
