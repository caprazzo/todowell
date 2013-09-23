package net.caprazzi.gitdone.parser.parser;

import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

@RunWith(Theories.class)
public class TodoParserTest {

    private static final SourceFileScanner.Language bavaLanguage = new SourceFileScanner.Language("Bava", "bava", SourceFileScanner.CommentStrategy.DoubleSlash);
    private static final SourceFileScanner.SourceFile bavaSourceFile = new SourceFileScanner.SourceFile(null, bavaLanguage);

    private static PotentialAssignment objectAssignment(Object obejct) {
        return PotentialAssignment.forValue(obejct.getClass().getName(), obejct);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @ParametersSuppliedBy(NonTodoSupplier.class)
    public @interface ValidTodoComments { }
    public static class NonTodoSupplier extends ParameterSupplier {
        @Override
        public List getValueSources(ParameterSignature parameterSignature) {
            return Arrays.asList(new Object[]{
                objectAssignment(new CommentLine(bavaSourceFile, 10, "  // TODO: hello"))
            });
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @ParametersSuppliedBy(InvalidTodoSupplier.class)
    public @interface InvalidTodoComments { }
    public static class InvalidTodoSupplier extends ParameterSupplier {
        @Override
        public List getValueSources(ParameterSignature parameterSignature) {
            return Arrays.asList(new Object[] {
                    objectAssignment(new CommentLine(bavaSourceFile, 10, "  // XXX: hello"))
            });
        }
    }

    @Theory
    public void should_parse_valid_todo_coomments(@ValidTodoComments Object source) {
        Optional<TodoParser.Todo> parsed = new TodoParser().parse((CommentLine) source);
        Assert.assertTrue(parsed.isPresent());
    }

    @Theory
    public void should_not_parse_valid_todo_coomments(@ValidTodoComments Object source) {
        Optional<TodoParser.Todo> parsed = new TodoParser().parse((CommentLine) source);
        Assert.assertFalse(parsed.isPresent());
    }
}