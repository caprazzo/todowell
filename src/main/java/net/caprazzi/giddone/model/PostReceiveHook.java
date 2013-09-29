package net.caprazzi.giddone.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PostReceiveHook {

    @JsonProperty
    private String ref;

    @JsonProperty
    private PostReceiveHookRepository repository;

    public String getRef() {
        return ref;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("ref", ref)
            .toString();
    }

    public PostReceiveHookRepository getRepository() {
        return repository;
    }

    public String getBranch() {
        return parseBranch(ref);
    }

    private String parseBranch(String ref) {
        String[] parts = ref.split("/");
        return parts[parts.length - 1];
    }

}
