package net.caprazzi.giddone.cloning;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.nio.file.Path;

public class Clone {
    private boolean success;
    private Throwable error;
    private final File log;
    private Path workerDir;
    private Path cloneDir;

    public Clone(boolean success, Throwable error, File log, Path workerDir, Path cloneDir) {
        this.success = success;
        this.error = error;
        this.log = log;
        this.workerDir = workerDir;
        this.cloneDir = cloneDir;
    }

    public boolean isSuccess() {
        return success;
    }

    public Throwable getError() {
        return error;
    }

    public File getLog() {
        return log;
    }

    Path getWorkerDir() {
        return workerDir;
    }

    public Path getCloneDir() {
        return cloneDir;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("success", success)
        .append("error", error != null ? error.toString() : null)
        .append("worker", workerDir)
        .append("clone", cloneDir)
        .append("log", log)
        .toString();
    }
}
