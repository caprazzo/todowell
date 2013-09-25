package net.caprazzi.todowell;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PostReceiveHook {

    @JsonProperty
    private String ref;

    public String getRef() {
        return ref;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("ref", ref)
            .toString();
    }
}
