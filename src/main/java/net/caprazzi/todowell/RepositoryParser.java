package net.caprazzi.todowell;

import java.nio.file.Path;

public class RepositoryParser {

    private final CloneService cloneService;
    private final SourceCodeParser parser;

    public RepositoryParser(CloneService cloneService, SourceCodeParser parser, Path t) {
        this.cloneService = cloneService;
        this.parser = parser;
    }

    public Iterable<Todo> parse(String repoUrl, String branch) throws Exception {
        Path repo = cloneService.clone(repoUrl, branch);
        return parser.parse(repo);
    }

}
