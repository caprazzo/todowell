package net.caprazzi.giddone.worker;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import net.caprazzi.giddone.Meters;
import net.caprazzi.giddone.model.Language;
import net.caprazzi.giddone.model.Languages;
import net.caprazzi.giddone.parsing.SourceFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

public class SourceFileScanner {

    private static final Logger Log = LoggerFactory.getLogger(SourceFileScanner.class);
    private final Languages languages;

    @Inject
    public SourceFileScanner(Languages languages) {
        this.languages = languages;
    }

    /**
     * Find all source files with a known extension
     *
     * @param path base path where to look for files
     * @return an iterable of files annotated with the detected language
     * @throws IOException
     */
    public final Iterable<SourceFile> scan(final Path path) throws IOException {
        Log.info("Scanning started in {}", path);

        Timer.Context timer = Meters.system.scanning.time();
        SourceFileVisitor visitor = new SourceFileVisitor(languages, path);
        try {
            Files.walkFileTree(path, visitor);
            LinkedList<SourceFile> sourceFiles = visitor.getSourceFiles();
            Log.info("Scanning completed with {} source files", sourceFiles.size());
            Meters.repositories.files.update(sourceFiles.size());
            return sourceFiles;
        } catch (IOException ex) {
            Log.error("Scanning failure: {}", ex);
            throw ex;
        } finally {
            timer.stop();
        }
    }
}
