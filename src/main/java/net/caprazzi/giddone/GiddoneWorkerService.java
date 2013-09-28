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

public class GiddoneWorkerService extends Service<GiddoneWorkerServiceConfiguration> {

    private static final Logger Log = LoggerFactory.getLogger(GiddoneWorkerService.class);

    public static void main(String[] args) throws Exception {
        new GiddoneWorkerService().run(args);
    }

    @Override
    public void initialize(Bootstrap<GiddoneWorkerServiceConfiguration> bootstrap) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run(GiddoneWorkerServiceConfiguration configuration, Environment environment) throws Exception {
        /*


        HttpClient httpClient = new DefaultHttpClient();
        // TODO: read from config
        HookQueueClient queueClient = new HookQueueClient(httpClient, "http://gitdone-receive-hook.herokuapp.com");

        // TODO: read from config
        Path tempDir = FileSystems.getDefault().getPath("./workers");
        try {
            Files.createDirectory(tempDir);
            Log.debug("Created workers dir: {}", tempDir.toAbsolutePath());
        }
        catch(FileAlreadyExistsException ex) {
            Log.debug("Using existing workers dir: {}", tempDir.toAbsolutePath());
        }



        CloneService cloneService = injector.getInstance(CloneService.class);

        // TODO: load languages from config
        Languages languages = new Languages(new Language("Java", "java", CommentStrategy.DoubleSlash));
        SourceFileScanner sourceFileScanner = new SourceFileScanner(languages);
        CommentParser commentParser = new CommentParser();
        TodoParser todoParser = new TodoParser();

        SourceCodeParser parser = new SourceCodeParser(sourceFileScanner, commentParser, todoParser);
        RepositoryParser repositoryParser = new RepositoryParser(cloneService, parser);

        // TODO: from config

        DeployService database = new DeployService(s3client);

        PresentationService presentationService = new PresentationService();

        HookQueueExecutor queueExecutor = new HookQueueExecutor(queueClient, 5000, repositoryParser, database, presentationService);
        */
        Injector injector = Guice.createInjector(new ServiceModule(configuration));
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

            bind(AWSCredentials.class).toInstance(configuration.getAws());
            bind(AmazonS3.class).to(AmazonS3Client.class);
        }
    }
}
