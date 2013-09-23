package net.caprazzi.todowell;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloneService {

    private static final Logger Log = LoggerFactory.getLogger(CloneService.class);
    private final Path tempDir;

    public CloneService(Path tempDir) {
        this.tempDir = tempDir;
    }

    public Path clone(String cloneUrl, String branch) throws Exception {
        Path workerDir = Files.createTempDirectory(tempDir, "worker-");
        File workerLog = new File(workerDir.toFile(), "worker.log");

        Log.info("STARTED Cloning {}/{} to {}", cloneUrl, branch, workerDir);

        try {
            ProcessBuilder builder = new ProcessBuilder()
                    .command("git", "clone", cloneUrl, "-b", branch, "repo")
                    .directory(workerDir.toAbsolutePath().toFile())
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(workerLog));

            Log.info("Executing {}", Joiner.on(" ").join(builder.command()));

            Process process = builder.start();

            int exit = process.waitFor();

            if (exit != 0) {
                throw new Exception("Git process exited with " + exit);
            }
        }
        catch (Exception ex) {
            Log.error("ERROR Cloning {}/{} to {}: {}", cloneUrl, branch, workerDir, ex);
            throw ex;
        }

        Log.info("COMPLETED Cloning {}/{} to {}", cloneUrl, branch, workerDir);
        // TODO: return a full descriptor of the cloning openration
        return Paths.get(workerDir.toString(), "repo");
    }
}
