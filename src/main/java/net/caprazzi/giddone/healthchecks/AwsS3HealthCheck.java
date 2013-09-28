package net.caprazzi.giddone.healthchecks;

import com.amazonaws.services.s3.AmazonS3;
import com.google.inject.Inject;
import com.yammer.metrics.core.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsS3HealthCheck extends HealthCheck {

    private static final Logger Log = LoggerFactory.getLogger(AwsS3HealthCheck.class);
    private final AmazonS3 s3client;

    @Inject
    protected AwsS3HealthCheck(AmazonS3 s3client) {
        super("aws-s3");
        this.s3client = s3client;
    }

    @Override
    public Result check() {
        try {
            s3client.getBucketLocation("giddone");
            return Result.healthy();
        }
        catch (Exception ex) {
            Log.error("Error while checking S3 client: {}", ex);
            return Result.unhealthy(ex);
        }
    }
}
