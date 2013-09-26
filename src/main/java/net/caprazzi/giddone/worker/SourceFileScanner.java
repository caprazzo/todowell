package net.caprazzi.giddone.worker;

import com.google.common.base.Optional;
import net.caprazzi.giddone.parsing.Language;
import net.caprazzi.giddone.parsing.Languages;
import net.caprazzi.giddone.parsing.SourceFile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceFileScanner {

    private final Languages languages;

    public SourceFileScanner(Languages languages) {
        this.languages = languages;
    }

    /**
     * Find all source files with a known extension
     * @param path base path where to look for files
     * @return an iterable of files annotated with the detected language
     * @throws IOException
     */
    public final Iterable<SourceFile> scan(final Path path) throws IOException {
        final LinkedList<SourceFile> sourceFiles = new LinkedList<SourceFile>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Optional<SourceFile> sourceFile = tryGetSourceFile(path, file);
                if (sourceFile.isPresent()) {
                    sourceFiles.add(sourceFile.get());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return sourceFiles;
    }

    public Optional<SourceFile> tryGetSourceFile(Path directory, Path file) {
        Optional<Language> language = languages.fromFile(file);
        if (language.isPresent()) {
            return Optional.of(new SourceFile(directory, file, language.get()));
        }
        return Optional.absent();
    }

}
