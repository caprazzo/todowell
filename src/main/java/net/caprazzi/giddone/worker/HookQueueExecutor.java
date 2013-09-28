package net.caprazzi.giddone.worker;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.caprazzi.giddone.GiddoneWorkerService;
import net.caprazzi.giddone.Meters;
import net.caprazzi.giddone.RandomStringGenerator;
import net.caprazzi.giddone.hook.HookQueueClient;
import net.caprazzi.giddone.model.QueueElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HookQueueExecutor {

    private static final Logger Log = LoggerFactory.getLogger(HookQueueExecutor.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final HookQueueClient client;
    private final long pollDelay;
    private final GiddoneWorkerService worker;

    private final String executorId = RandomStringGenerator.randomString();
    private long jobCount = 0;

    @Inject
    public HookQueueExecutor(HookQueueClient client, @Named("hook-worker-polling") long pollDelay, GiddoneWorkerService worker) {
        this.client = client;
        this.pollDelay = pollDelay;
        this.worker = worker;
    }

    private Optional<QueueElement> next() {
        try {
            return client.headValue();
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    public void start() {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                MDC.put("executorId", "executor=" + executorId + " ");
                while(!executor.isShutdown()) {

                    Optional<QueueElement> value = next();
                    if (!value.isPresent()) {
                        sleep(pollDelay);
                        continue;
                    }

                    MDC.put("jobId", "job=" + jobCount + " ");
                    MDC.put("elementId", "el=" + value.get().getId() + " ");
                    MDC.put("repoId", "repo=" + value.get().getValue().getRepository().getCloneUrl() + " ");

                    Log.info("Job {} started", jobCount);

                    try {
                        worker.work(value.get().getValue());
                        try {
                            client.success(value.get().getId());
                        }
                        catch (Exception ex) {
                            Log.error("Error while reporting success to queue: {}", ex);
                        }
                    }
                    catch (Exception ex) {
                        Log.error("Error while processing {}: {}", value.get(), ex);
                        ex.printStackTrace();
                        try {
                            client.error(value.get().getId(), ex);
                        } catch (IOException e) {
                            Log.error("Error while reporting error to queue: {}", ex);
                        }
                    }
                    finally {
                        Log.info("Job {} completed", jobCount);
                        jobCount++;
                        MDC.remove("jobId");
                        MDC.remove("elementId");
                        MDC.remove("repoId");
                    }
                }
                MDC.remove("executorId");
            }
        });
    }

    private void sleep(long pollDelay) {
        try {
            Thread.sleep(pollDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
