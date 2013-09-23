package net.caprazzi.todowell;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
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
        Languages languages = new Languages(new Language("Java", "java", CommentStrategy.DoubleSlash));
        SourceFileScanner sourceFileScanner = new SourceFileScanner(languages);
        CommentParser commentParser = new CommentParser();
        TodoParser todoParser = new TodoParser();

        SourceCodeParser parser = new SourceCodeParser(sourceFileScanner, commentParser, todoParser);
        RepositoryParser repositoryParser = new RepositoryParser(cloneService, parser, workers);

        String cloneUrl =  "git@github.com:mcaprari/botto.git";
        String branch = "master";

        Repository repository = new Repository("mcaprari", "botto", "git@github.com:mcaprari/botto.git", "master");
        // TODO: load dynamically

        Iterable<Todo> todos = repositoryParser.parse(cloneUrl, branch);
        LinkedList<TodoRecord> records = new LinkedList<TodoRecord>();
        for(Todo todo : todos) {
            System.out.println("\t" + todo.getComment().getSource().getFile().getFileName() + ":\t" + todo.getLabel() + ": " + todo.getTodo());
            records.add(TodoRecord.from(todo));
        }


        ObjectMapper mapper = new ObjectMapper();
        // TODO: fix joda time serialization and convert all to joda
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        TodoSnapshot snapshot = new TodoSnapshot(new Date(), repository, records);

        System.out.println(mapper.writeValueAsString(snapshot));

        // TODO: store the extracted todos
        // TODO: delete the clone
    }

    public static class TodoSnapshot {
        private Date created;
        private Repository repo;
        private Collection<TodoRecord> todo;

        public TodoSnapshot(Date created, Repository repo, Collection<TodoRecord> todo) {
            this.created = created;
            this.repo = repo;
            this.todo = todo;
        }

        public Date getCreated() {
            return created;
        }

        public Repository getRepo() {
            return repo;
        }

        public Collection<TodoRecord> getTodo() {
            return todo;
        }
    }



}
