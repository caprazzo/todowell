package net.caprazzi.gitdone.parser.parser;


import org.junit.Assert;
import org.junit.Test;

public class CommentLineTest {

    private static final SourceFileScanner.Language bavaLanguage = new SourceFileScanner.Language("Bava", "bava", SourceFileScanner.CommentStrategy.DoubleSlash);
    private static final SourceFileScanner.SourceFile bavaSourceFile = new SourceFileScanner.SourceFile(null, bavaLanguage);

    @Test
    public void should_extract_comment() {
        Assert.assertEquals("some text", new CommentLine(bavaSourceFile, 0, "  //   some text").getComment());
    }
}
