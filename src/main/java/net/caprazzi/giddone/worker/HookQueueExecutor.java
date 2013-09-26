package net.caprazzi.giddone.worker;

import com.google.common.base.Optional;
import net.caprazzi.giddone.deploy.DeployService;
import net.caprazzi.giddone.hook.HookQueueClient;
import net.caprazzi.giddone.hook.PostReceiveHook;
import net.caprazzi.giddone.hook.QueueElement;
import net.caprazzi.giddone.parsing.Todo;
import net.caprazzi.giddone.parsing.TodoRecord;
import net.caprazzi.giddone.parsing.TodoSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HookQueueExecutor {

    private static final Logger Log = LoggerFactory.getLogger(HookQueueExecutor.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final HookQueueClient client;
    private final long pollDelay;
    private final RepositoryParser repositoryParser;
    private final DeployService deployService;

    public HookQueueExecutor(HookQueueClient client, long pollDelay, RepositoryParser repositoryParser, DeployService deployService) {
        this.client = client;
        this.pollDelay = pollDelay;
        this.repositoryParser = repositoryParser;
        this.deployService = deployService;
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
                while(!executor.isShutdown()) {

                    Optional<QueueElement> value = next();
                    if (!value.isPresent()) {
                        sleep(pollDelay);
                        continue;
                    }

                    try {
                        process(value.get().getValue());
                        client.success(value.get().getId());
                    }
                    catch (Exception ex) {
                        Log.error("Error while processing {}: {}", value.get(), ex);
                        ex.printStackTrace();
                        client.error(value.get().getId(), ex);
                    }
                }
            }
        });
    }

    // TODO: move the processing part to a Worker class
    private void process(PostReceiveHook hook) throws Exception {
        Iterable<Todo> todos = repositoryParser.parse(hook.getRepository().getCloneUrl(), hook.getBranch());

        // TODO: would be interesting to try and keep the iterable abstraction
        LinkedList<TodoRecord> records = new LinkedList<TodoRecord>();
        for(Todo todo : todos) {
            System.out.println("\t" + todo.getComment().getSource().getFile().getFileName() + ":\t" + todo.getLabel() + ": " + todo.getTodo());
            records.add(TodoRecord.from(todo));
        }

        TodoSnapshot snapshot = new TodoSnapshot(new Date(), hook, records);

        deployService.deployPage(snapshot);

        // TODO: cleanup old repos if there are no errors
    }

    private void sleep(long pollDelay) {
        try {
            Thread.sleep(pollDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
