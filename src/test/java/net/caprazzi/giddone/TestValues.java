package net.caprazzi.giddone;

import java.util.Arrays;
import java.util.List;

public class TestValues {

    public static final List<String> javaStyleLines = Arrays.asList(new String[]{
            "// TODO: is-comment is-todo",
            "    // TODO: is-comment is-todo            ",

            "// XXX: is-comment not-todo            ",
            "// is-comment not-todo",
            "        // is-comment not-todo        ",
            "some text here // TODO: not-comment not-todo",
            "   some text here // not-comment not-todo",
            "not-comment not-todo     ",
            "    not-comment not-todo",
            "/* not-comment not-todo */",
            "/* TODO: not-comment not-todo */",
    });

    public static List<String> shellStyleLines = Arrays.asList(new String[]{
            "# TODO: is-comment is-todo",
            "    # TODO: is-comment is-todo            ",

            "# XXX: is-comment not-todo            ",
            "# is-comment not-todo",
            "        # is-comment not-todo        ",
            "some text here # TODO: not-comment not-todo",
            "   some text here # not-comment not-todo",
            "not-comment not-todo     ",
            "    not-comment not-todo",
            "''' not-comment not-todo '''",
            "''' TODO: not-comment not-todo '''",
    });
}
