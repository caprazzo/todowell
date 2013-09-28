package net.caprazzi.giddone.aws;

import com.amazonaws.auth.AWSCredentials;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class AwsCredentials implements AWSCredentials {

    @JsonProperty
    @NotNull
    private String AWSAccessKeyId;

    @JsonProperty
    @NotNull
    private String AWSSecretKey;

    @Override
    public String getAWSAccessKeyId() {
        return AWSAccessKeyId;
    }

    @Override
    public String getAWSSecretKey() {
        return AWSSecretKey;
    }
}
