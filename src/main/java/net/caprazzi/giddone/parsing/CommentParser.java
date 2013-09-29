package net.caprazzi.giddone.parsing;

import com.codahale.metrics.Timer;
import net.caprazzi.giddone.Meters;
import net.caprazzi.giddone.model.CommentLine;
import net.caprazzi.giddone.model.Language;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class CommentParser {

    private static final Logger Log = LoggerFactory.getLogger(CommentParser.class);

    public final Iterable<CommentLine> parse(SourceFile source, Iterator<String> lines) {
        LinkedList<CommentLine> comments = new LinkedList<CommentLine>();
        int lineNumber = 1;
        while (lines.hasNext()) {
            String line = lines.next();
            if (isComment(line, source.getLanguage())) {
                comments.add(new CommentLine(source, lineNumber, line));
            }
            lineNumber++;
        }
        return comments;
    }

    private boolean isComment(String line, Language language) {
        return line != null && line.trim().startsWith(language.getStyle().singleCommentStart());
    }

    public Iterable<CommentLine> parse(Iterable<SourceFile> sourceFiles) throws IOException {
        Log.info("Parsing started");
        ArrayList<CommentLine> commentLines = new ArrayList<CommentLine>();
        Timer.Context timer = Meters.system.commentParsing.time();
        try {
            for(SourceFile file : sourceFiles) {
                LineIterator lines = FileUtils.lineIterator(file.getFile().toFile(), "UTF-8");
                try {
                    for (CommentLine line : parse(file, lines)) {
                        commentLines.add(line);
                    }
                }
                finally {
                    LineIterator.closeQuietly(lines);
                }
            }
            Log.info("Parsing completed with {} comment lines", commentLines.size());
            Meters.repositories.comments.update(commentLines.size());
        }
        catch (IOException ex) {
            Log.error("Parsing failure: {}", ex);
        }
        finally {
            timer.stop();
        }
        return commentLines;
    }
}
