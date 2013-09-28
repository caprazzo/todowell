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
import java.util.LinkedList;

public class CommentParser {

    private static final Logger Log = LoggerFactory.getLogger(CommentParser.class);

    private final Iterable<CommentLine> parse(SourceFile source) throws IOException {


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
        Log.info("Parsing started");
        ArrayList<CommentLine> commentLines = new ArrayList<CommentLine>();
        Timer.Context timer = Meters.system.commentParsing.time();
        try {
            for(SourceFile file : sourceFiles) {
                // TODO: meter file parsing time
                for (CommentLine line : parse(file)) {
                    commentLines.add(line);
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
