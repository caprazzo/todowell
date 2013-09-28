package net.caprazzi.giddone.worker;

import com.google.inject.Inject;
import net.caprazzi.giddone.cloning.Clone;
import net.caprazzi.giddone.cloning.CloneService;
import net.caprazzi.giddone.parsing.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryParser {

    private static final Logger Log = LoggerFactory.getLogger(RepositoryParser.class);

    private final CloneService cloneService;
    private final SourceCodeParser parser;

    @Inject
    public RepositoryParser(CloneService cloneService, SourceCodeParser parser) {
        this.cloneService = cloneService;
        this.parser = parser;
    }

    public Iterable<Todo> parse(String cloneUrl, String branch) throws Exception {
        Clone result = cloneService.clone(cloneUrl, branch);

        if (result.isSuccess()) {
            Iterable<Todo> parsed = parser.parse(result.getCloneDir());
            cloneService.cleanUp(result);
            return parsed;
        }

        Log.error("Parsing aborted because cloning failed with: {}", result);
        throw new RuntimeException("Parsing aborted because cloning failed", result.getError());
    }

}
