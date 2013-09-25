package net.caprazzi.todowell;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class HookQueueClient {

    private static final Logger Log = LoggerFactory.getLogger(HookQueueClient.class);

    private final HttpClient client;
    private final String baseUrl;

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public HookQueueClient(HttpClient client, String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    public Optional<PostReceiveHook> headValue() throws IOException {
        Optional<Integer> head = head();
        if (!head.isPresent()) {
            return Optional.absent();
        }
        return value(head.get());
    }

    private Optional<Integer> head() throws IOException {
        HttpGet get = new HttpGet(baseUrl + "/github/post-receive-hooks/chrome/hooks/head");

        try {
            HttpResponse response = client.execute(get);
            Log.debug("Response to {}: {}", get, response);
            if (response.getStatusLine().getStatusCode() == 200) {
                return Optional.of(Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8")));
            }
            return Optional.absent();
        }
        finally {
            get.releaseConnection();
        }
    }

    private Optional<PostReceiveHook> value(int id) throws IOException {
        HttpGet get = new HttpGet(baseUrl + "/github/post-receive-hooks/chrome/hooks/" + id + "/value");
        try {
            HttpResponse response = client.execute(get);
            Log.debug("Response to {}: {}", get, response);
            if (response.getStatusLine().getStatusCode() == 200) {
                return Optional.of(mapper.readValue(response.getEntity().getContent(), PostReceiveHook.class));
            }
            return Optional.absent();
        }
        finally {
            get.releaseConnection();
        }
    }
}
