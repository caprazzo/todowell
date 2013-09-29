package net.caprazzi.giddone;

import net.caprazzi.giddone.model.CommentLine;
import net.caprazzi.giddone.model.CommentStyle;
import net.caprazzi.giddone.model.Language;
import net.caprazzi.giddone.parsing.SourceFile;
import org.junit.Assert;
import org.junit.Test;

public class CommentLineTest {

    private static final Language bavaLanguage = new Language("Bava", "bava", CommentStyle.Java);
    private static final SourceFile bavaSourceFile = new SourceFile(null, null, bavaLanguage);

    @Test
    public void should_extract_comment() {
        Assert.assertEquals("some text", new CommentLine(bavaSourceFile, 0, "  //   some text").getComment());
    }
}
