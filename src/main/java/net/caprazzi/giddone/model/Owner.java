package net.caprazzi.giddone.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Owner {
    @JsonProperty
    private String email;

    @JsonProperty
    private String name;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
