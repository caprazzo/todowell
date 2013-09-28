package net.caprazzi.giddone;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.*;
import com.google.inject.name.Names;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.client.HttpClientBuilder;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import net.caprazzi.giddone.cloning.CloneService;
import net.caprazzi.giddone.deploy.DeployService;
import net.caprazzi.giddone.deploy.PresentationService;
import net.caprazzi.giddone.healthchecks.AwsS3HealthCheck;
import net.caprazzi.giddone.hook.HookQueueClient;
import net.caprazzi.giddone.parsing.*;
import net.caprazzi.giddone.resources.GiddoneResource;
import net.caprazzi.giddone.worker.HookQueueExecutor;
import net.caprazzi.giddone.worker.RepositoryParser;
import net.caprazzi.giddone.worker.SourceCodeParser;
import net.caprazzi.giddone.worker.SourceFileScanner;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GiddoneWorkerService extends Service<GiddoneWorkerServiceConfiguration> {

    private static final Logger Log = LoggerFactory.getLogger(GiddoneWorkerService.class);

    private static final ScheduledExecutorService healthCheckExecutor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {
        new GiddoneWorkerService().run(args);
    }

    @Override
    public void initialize(Bootstrap<GiddoneWorkerServiceConfiguration> bootstrap) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run(GiddoneWorkerServiceConfiguration configuration, Environment environment) throws Exception {
        Injector injector = Guice.createInjector(new ServiceModule(configuration));
        final AwsS3HealthCheck instance = injector.getInstance(AwsS3HealthCheck.class);
        environment.addHealthCheck(instance);

        healthCheckExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    instance.check();
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

        environment.addResource(injector.getInstance(GiddoneResource.class));
        injector.getInstance(HookQueueExecutor.class).start();
    }

    private static class ServiceModule extends AbstractModule {

        private final GiddoneWorkerServiceConfiguration configuration;

        public ServiceModule(GiddoneWorkerServiceConfiguration configuration) {
            this.configuration = configuration;
        }

        @Override
        protected void configure() {

            Path tempDir = FileSystems.getDefault().getPath(configuration.getWorkerTempDir());
            try {
                Files.createDirectory(tempDir);
                Log.debug("Created workers dir: {}", tempDir.toAbsolutePath());
            }
            catch(FileAlreadyExistsException ex) {
                Log.debug("Using existing workers dir: {}", tempDir.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to created directory " + tempDir, e);
            }

            bind(HttpClient.class).toInstance(new HttpClientBuilder().using(configuration.getHttpClient()).build());
            bind(String.class).annotatedWith(Names.named("hook-queue-url")).toInstance(configuration.getHookQueueUrl());
            bind(Path.class).annotatedWith(Names.named("worker-temp-dir")).toInstance(tempDir);
            bind(Long.class).annotatedWith(Names.named("hook-worker-polling")).toInstance(configuration.getHookQueuePolling());
            bind(Languages.class).toInstance(new Languages(new Language("Java", "java", CommentStrategy.DoubleSlash)));

            //bind(AWSCredentials.class).toInstance();
            bind(AmazonS3.class).toInstance(new AmazonS3Client(new BasicAWSCredentials(configuration.getAws().getAWSAccessKeyId(), configuration.getAws().getAWSSecretKey())));
        }
    }
}
