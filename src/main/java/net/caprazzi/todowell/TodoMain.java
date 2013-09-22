package net.caprazzi.todowell;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class TodoMain {

    private static final Logger Log = LoggerFactory.getLogger(TodoMain.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        Path workers = FileSystems.getDefault().getPath("./workers");
        try {
            Files.createDirectory(workers);
            Log.debug("Created workers dir: {}", workers.toAbsolutePath());
        }
        catch(FileAlreadyExistsException ex) {
            Log.debug("Using existing workers dir: {}", workers.toAbsolutePath());
        }

        CloneService cloneService = new CloneService();

        Path workerDir = Files.createTempDirectory(workers, "worker-");
        File workerLog = new File(workerDir.toFile(), "worker.log");

        String cloneUrl =  "git@github.com:mcaprari/botto.git";
        String branch = "master";

        try {
            cloneService.clone(cloneUrl, branch, workerDir, workerLog);

        } catch (Exception e) {

        }

    }

}
