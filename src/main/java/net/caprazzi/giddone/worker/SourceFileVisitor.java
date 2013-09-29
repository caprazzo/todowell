package net.caprazzi.giddone.worker;

import com.google.common.base.Optional;
import net.caprazzi.giddone.model.Language;
import net.caprazzi.giddone.model.Languages;
import net.caprazzi.giddone.parsing.SourceFile;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;

public class SourceFileVisitor extends SimpleFileVisitor<Path> {
    private final Languages languages;
    private final Path sourceDir;
    private final LinkedList<SourceFile> sourceFiles = new LinkedList<SourceFile>();

    public SourceFileVisitor(Languages languages, Path sourceDir) {
        this.languages = languages;
        this.sourceDir = sourceDir;
    }

    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        Optional<SourceFile> sourceFile = tryGetSourceFile(sourceDir, file);
        if (sourceFile.isPresent()) {
            sourceFiles.add(sourceFile.get());
        }
        return FileVisitResult.CONTINUE;
    }

    public LinkedList<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    private Optional<SourceFile> tryGetSourceFile(Path directory, Path file) {
        Optional<Language> language = languages.fromFile(file);
        if (language.isPresent()) {
            return Optional.of(new SourceFile(directory, file, language.get()));
        }
        return Optional.absent();
    }
}
