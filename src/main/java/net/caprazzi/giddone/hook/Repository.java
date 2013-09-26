package net.caprazzi.giddone.hook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
    @JsonProperty
    private String url;

    @JsonProperty
    private String name;

    @JsonProperty
    private Owner owner;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getCloneUrl() {
        return "git@github.com:" + getOwner().getName() + "/" + getName() + ".git";
    }
}
