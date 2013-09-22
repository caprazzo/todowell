package net.caprazzi.todowell;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class CloneService {

    private static final Logger Log = LoggerFactory.getLogger(CloneService.class);

    public void clone(String cloneUrl, String branch, Path dest, File log) throws Exception {
        Log.info("STARTED Cloning {}/{} to {}", cloneUrl, branch, dest);

        try {
        ProcessBuilder builder = new ProcessBuilder()
                .command("git", "clone", cloneUrl, "-b", branch , "repo")
                .directory(dest.toAbsolutePath().toFile())
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.appendTo(log));

        Log.info("Executing {}", Joiner.on(" ").join(builder.command()));

        Process process = builder.start();

        int exit = process.waitFor();

        if (exit != 0) {
            throw new Exception("Git process exited with " + exit);
        }
        }
        catch (Exception ex) {
            Log.error("ERROR Cloning {}/{} to {}: {}", cloneUrl, branch, dest, ex);
            throw ex;
        }

        Log.info("COMPLETED Cloning {}/{} to {}", cloneUrl, branch, dest);
    }
}
