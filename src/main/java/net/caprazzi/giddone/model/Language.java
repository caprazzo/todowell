package net.caprazzi.giddone.model;

import net.caprazzi.giddone.model.CommentStrategy;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class Language {

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
