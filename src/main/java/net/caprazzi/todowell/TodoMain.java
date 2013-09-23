package net.caprazzi.todowell;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;

public class TodoMain {

    private static final Logger Log = LoggerFactory.getLogger(TodoMain.class);

    public static void main(String[] args) throws Exception {

        Path workers = FileSystems.getDefault().getPath("./workers");
        try {
            Files.createDirectory(workers);
            Log.debug("Created workers dir: {}", workers.toAbsolutePath());
        }
        catch(FileAlreadyExistsException ex) {
            Log.debug("Using existing workers dir: {}", workers.toAbsolutePath());
        }

        CloneService cloneService = new CloneService(workers);

        Path workerDir = Files.createTempDirectory(workers, "worker-");

        String cloneUrl =  "git@github.com:mcaprari/botto.git";
        String branch = "master";

        // TODO: load dynamically
        Languages languages = new Languages(new Language("Java", "java", CommentStrategy.DoubleSlash));

        SourceFileScanner sourceFileScanner = new SourceFileScanner(languages);
        CommentParser commentParser = new CommentParser();
        TodoParser todoParser = new TodoParser();

        SourceCodeParser parser = new SourceCodeParser(sourceFileScanner, commentParser, todoParser);
        RepositoryParser repositoryParser = new RepositoryParser(cloneService, parser, workers);

        Iterable<Todo> todos = repositoryParser.parse(cloneUrl, branch);
        LinkedList<TodoRecord> records = new LinkedList<TodoRecord>();
        for(Todo todo : todos) {
            System.out.println("\t" + todo.getComment().getSource().getFile().getFileName() + ":\t" + todo.getLabel() + ": " + todo.getTodo());
            records.add(TodoRecord.from(todo));
        }

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(records));

        // TODO: convert to json
        // TODO: store the extracted todos
        // TODO: delete the clone
    }

    public static class TodoRecord {
        private String file;
        private int line;
        private String label;
        private String body;

        public TodoRecord(String file, int line, String label, String body) {
            this.file = file;
            this.line = line;
            this.label = label;
            this.body = body;
        }

        public static TodoRecord from(Todo todo) {
            return new TodoRecord(
                todo.getComment().getSource().getRelativeFile().toString(),
                todo.getComment().getLineNumber(),
                todo.getLabel(),
                todo.getTodo());
        }

        public String getFile() {
            return file;
        }

        public int getLine() {
            return line;
        }

        public String getLabel() {
            return label;
        }

        public String getBody() {
            return body;
        }

    }
}
