package net.caprazzi.gitdone.parser;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class FileScanner {

    private final static Languages languages = new Languages(new Language("Java", "java", CommentStrategy.DoubleSlash));

    public static void main(String[] args) throws IOException {
        Path workers = FileSystems.getDefault().getPath("./workers");
        for(SourceFile file : scan(workers)) {
            System.out.println(file);
            for(CommentLine comment : CommentScanner.getComments(file)) {
                Optional<TodoParser.Todo> todo = TodoParser.parse(comment);
                if (todo.isPresent()) {
                    System.out.println("\t" + comment.getLineNumber() + ":\t" + comment.getLine());
                }
            }
        }
    }

    /**
     * Find all source files with a known extension
     * @param path base path where to look for files
     * @return an iterable of files annotated with the detected language
     * @throws IOException
     */
    public static Iterable<SourceFile> scan(Path path) throws IOException {
        final LinkedList<SourceFile> sourceFiles = new LinkedList<SourceFile>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Optional<SourceFile> sourceFile = SourceFile.from(file);
                if (sourceFile.isPresent()) {
                    sourceFiles.add(sourceFile.get());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return sourceFiles;
    }

    public static class SourceFile {
        private final Path file;
        private final Language language;

        public SourceFile(Path file, Language language) {
            this.file = file;
            this.language = language;
        }

        public static Optional<SourceFile> from(Path file) {
            Optional<Language> language = languages.fromFile(file);
            if (language.isPresent()) {
                return Optional.of(new SourceFile(file, language.get()));
            }
            return Optional.absent();
        }

        public Path getFile() {
            return file;
        }

        public Language getLanguage() {
            return language;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(file)
                .append(language)
                .toString();
        }
    }

    public static class Languages {

        private final BiMap<String, Language> map = HashBiMap.create();

        public Languages(Language... languages) {
            add(Arrays.asList(languages));
        }

        public final void add(Language language) {
            map.put(language.getExtension(), language);
        }

        public final void add(Collection<Language> languages) {
            for(Language language : languages) {
                add(language);
            }
        }

        public Optional<Language> fromFile(Path file) {
            String extension = FilenameUtils.getExtension(file.toString());
            return Optional.fromNullable(map.get(extension));
        }
    }

    public static class Language {

        private final String name;
        private final String extension;
        private final CommentStrategy commentStrategy;

        public Language(String name, String extension, CommentStrategy commentStrategy) {
            this.name = name;
            this.extension = extension;
            this.commentStrategy = commentStrategy;
        }

        public String getName() {
            return name;
        }

        public String getExtension() {
            return extension;
        }

        public CommentStrategy getCommentStrategy() {
            return commentStrategy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, extension, commentStrategy);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (getClass() != obj.getClass())
                return false;

            final Language other = (Language) obj;
            return Objects.equals(name, other.name)
                    && Objects.equals(extension, other.extension)
                    && Objects.equals(commentStrategy, other.commentStrategy);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(name)
                .append(extension)
                .append(commentStrategy)
                .toString();
        }
    }

    public enum CommentStrategy {
        DoubleSlash("//"),
        Hash("#");
        private final String singleCommentStart;

        CommentStrategy(String singleCommentStart) {
            this.singleCommentStart = singleCommentStart;
        }

        public String singleCommentStart() {
            return singleCommentStart;
        }
    }

}
