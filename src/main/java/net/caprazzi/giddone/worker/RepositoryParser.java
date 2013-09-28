package net.caprazzi.giddone.worker;

import com.google.inject.Inject;
import net.caprazzi.giddone.cloning.CloneService;
import net.caprazzi.giddone.parsing.Todo;

import java.nio.file.Path;

public class RepositoryParser {

    private final CloneService cloneService;
    private final SourceCodeParser parser;

    @Inject
    public RepositoryParser(CloneService cloneService, SourceCodeParser parser) {
        this.cloneService = cloneService;
        this.parser = parser;
    }

    public Iterable<Todo> parse(String cloneUrl, String branch) throws Exception {
        Path repo = cloneService.clone(cloneUrl, branch);
        return parser.parse(repo);
    }

}
