package net.caprazzi.giddone.healthchecks;

import com.amazonaws.services.s3.AmazonS3;
import com.google.inject.Inject;
import com.yammer.metrics.core.HealthCheck;

public class AwsS3HealthCheck extends HealthCheck {

    private final AmazonS3 s3client;

    @Inject
    protected AwsS3HealthCheck(AmazonS3 s3client) {
        super("aws-s3");
        this.s3client = s3client;
    }

    @Override
    public Result check() throws Exception {
        s3client.getBucketLocation("giddone");
        return Result.healthy();
    }
}
