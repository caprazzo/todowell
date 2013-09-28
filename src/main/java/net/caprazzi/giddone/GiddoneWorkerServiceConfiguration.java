package net.caprazzi.giddone;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.HttpClientConfiguration;
import com.yammer.dropwizard.config.Configuration;
import net.caprazzi.giddone.aws.AwsCredentials;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class GiddoneWorkerServiceConfiguration extends Configuration {

    @Valid
    @JsonProperty
    private final HttpClientConfiguration httpClient = new HttpClientConfiguration();

    @Valid
    @NotNull
    @JsonProperty
    private AwsCredentials aws = new AwsCredentials();

    @Valid
    @NotNull
    @JsonProperty
    private String hookQueueUrl;

    @Valid
    @NotNull
    @JsonProperty
    private Long hookQueuePolling;

    @Valid
    @NotNull
    @JsonProperty
    private String workerTempDir;

    public HttpClientConfiguration getHttpClient() {
        return httpClient;
    }

    public AwsCredentials getAws() {
        return aws;
    }

    public String getHookQueueUrl() {
        return hookQueueUrl;
    }

    public Long getHookQueuePolling() {
        return hookQueuePolling;
    }

    public String getWorkerTempDir() {
        return workerTempDir;
    }
}
