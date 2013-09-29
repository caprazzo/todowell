package net.caprazzi.giddone.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class Language {

    private final String name;
    private final String extension;
    private final CommentStyle style;

    public Language(@JsonProperty("name") String name, @JsonProperty("extension") String extension, @JsonProperty("style") CommentStyle commentStrategy) {
        this.name = name;
        this.extension = extension;
        this.style = commentStrategy;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public CommentStyle getStyle() {
        return style;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, extension, style);
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
                && Objects.equals(style, other.style);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append(name)
            .append(extension)
            .append(style)
            .toString();
    }
}
