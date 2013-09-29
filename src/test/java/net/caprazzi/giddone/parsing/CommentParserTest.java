package net.caprazzi.giddone.parsing;

import com.google.common.collect.Lists;

import net.caprazzi.giddone.TestValues;
import net.caprazzi.giddone.model.CommentLine;
import net.caprazzi.giddone.model.CommentStyle;
import net.caprazzi.giddone.model.Language;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CommentParserTest {

    @Test
    public void should_find_java_style_comments() {
        SourceFile sourceFile = new SourceFile(null, null, new Language("Java", "java", CommentStyle.Java));
        CommentParser parser = new CommentParser();

        List<CommentLine> comments = Lists.newArrayList(parser.parse(sourceFile, TestValues.javaStyleLines.iterator()));

        for (int i=0; i<comments.size(); i++) {
            Assert.assertEquals(i + 1, comments.get(i).getLineNumber());
            Assert.assertEquals(TestValues.javaStyleLines.get(i), comments.get(i).getLine());
            Assert.assertEquals(sourceFile, comments.get(1).getSource());
        }

        Assert.assertEquals(5, comments.size());
    }

    @Test
    public void should_find_shell_style_comments() {
        SourceFile sourceFile = new SourceFile(null, null, new Language("Shell", "shell", CommentStyle.Shell));
        CommentParser parser = new CommentParser();

        List<CommentLine> comments = Lists.newArrayList(parser.parse(sourceFile, TestValues.shellStyleLines.iterator()));

        for (int i=0; i<comments.size(); i++) {
            Assert.assertEquals(i + 1, comments.get(i).getLineNumber());
            Assert.assertEquals(TestValues.shellStyleLines.get(i), comments.get(i).getLine());
            Assert.assertEquals(sourceFile, comments.get(1).getSource());
        }

        Assert.assertEquals(5, comments.size());
    }
}
