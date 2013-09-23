package net.caprazzi.todowell;

import com.google.common.base.Optional;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.Path;

public class SourceFile {
    private final Path directory;
    private final Path file;
    private final Language language;

    public SourceFile(Path directory, Path file, Language language) {
        this.directory = directory;
        this.file = file;
        this.language = language;
    }

    public Path getFile() {
        return file;
    }

    public Path getRelativeFile() {
        return directory.relativize(file);
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
