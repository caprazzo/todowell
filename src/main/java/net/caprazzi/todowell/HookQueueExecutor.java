package net.caprazzi.todowell;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HookQueueExecutor {

    private final HookQueueClient client;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public HookQueueExecutor(HookQueueClient client) {
        this.client = client;
    }

    public void start() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Optional<PostReceiveHook> value = client.headValue();
                    if (value.isPresent()) {
                        onValue(value.get());
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void onValue(PostReceiveHook hook) {
        System.out.println("Received value " + hook);
    }

}
