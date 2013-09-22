package net.caprazzi.gitdone.parser;


import org.junit.Assert;
import org.junit.Test;

public class CommentLineTest {

    private static final FileScanner.Language bavaLanguage = new FileScanner.Language("Bava", "bava", FileScanner.CommentStrategy.DoubleSlash);
    private static final FileScanner.SourceFile bavaSourceFile = new FileScanner.SourceFile(null, bavaLanguage);

    @Test
    public void should_extract_comment() {
        Assert.assertEquals("some text", new CommentLine(bavaSourceFile, 0, "  //   some text").getComment());
    }
}
