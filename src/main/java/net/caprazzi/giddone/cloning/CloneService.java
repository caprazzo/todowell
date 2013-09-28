package net.caprazzi.giddone.cloning;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloneService {

    private static final Logger Log = LoggerFactory.getLogger(CloneService.class);
    private final Path tempDir;

    @Inject
    public CloneService(@Named("worker-temp-dir") Path tempDir) {
        this.tempDir = tempDir;
    }

    // TODO: add a request ID so tracing and cleaning is easier
    public Clone clone(String cloneUrl, String branch) throws Exception {
        Path workerDir = Files.createTempDirectory(tempDir, "worker-");
        File workerLog = new File(workerDir.toFile(), "worker.log");
        Log.info("Cloning started workDir={}", workerDir);

        try {


            // TODO: meter cloning execution
            ProcessBuilder builder = new ProcessBuilder()
                    .command("git", "clone", cloneUrl, "-b", branch, "repo")
                    .directory(workerDir.toAbsolutePath().toFile())
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(workerLog));

            Log.info("Cloning executing {}", Joiner.on(" ").join(builder.command()));

            Process process = builder.start();

            int exit = process.waitFor();

            if (exit != 0) {
                throw new Exception("Git process exited with " + exit);
            }

            Log.info("Cloning completed successfully");
            // TODO: return a full descriptor of the cloning operation
            return new Clone(true, null, workerLog, workerDir, Paths.get(workerDir.toString(), "repo"));
        }
        catch (Exception ex) {
            Log.error("Cloning failed: {}", ex);
            return new Clone(false, null, workerLog, workerDir, null);
        }
    }

    public void cleanUp(Clone clone) {
        Log.info("Cleaning up worker dir");
        try {
            FileUtils.deleteDirectory(clone.getWorkerDir().toFile());
        } catch (IOException e) {
            Log.error("Failed to delete worker dir {}: {}", clone.getWorkerDir());
        }
    }
}
