package net.caprazzi.todowell;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class ParserService {
    public static void main(String[] args) {
        HttpClient httpClient = new DefaultHttpClient();
        HookQueueClient queueClient = new HookQueueClient(httpClient, "http://gitdone-receive-hook.herokuapp.com");
        HookQueueExecutor queueExecutor = new HookQueueExecutor(queueClient);
        queueExecutor.start();
    }
}
