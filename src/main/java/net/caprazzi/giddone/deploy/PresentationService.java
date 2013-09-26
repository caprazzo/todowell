package net.caprazzi.giddone.deploy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.caprazzi.giddone.parsing.TodoSnapshot;

public class PresentationService {

    private final static ObjectMapper mapper = new ObjectMapper();
    static {
        // TODO: fix joda time serialization and convert all to joda
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public String asHtml(TodoSnapshot snapshot) throws Exception {
        return htmlTemplate(asJson(snapshot));
    }

    private String asJson(TodoSnapshot snapshot) throws Exception {
        return mapper.writeValueAsString(snapshot);
    }


    private String htmlTemplate(String snapshotJson) {
        // TODO: get and cache template from remote location
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <script id=\"snapshot\">function snapshot() { return "  + snapshotJson + "; }</script>\n" +
                "    <script src=\"/giddone/giddone.js\"></script>\n" +
                "    <title>Giddone</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div ng-app ng-controller=\"MainCtrl\" ng-include src=\"mainUrl\"></div>\n" +
                "</body>\n" +
                "</html>";
    }
}
