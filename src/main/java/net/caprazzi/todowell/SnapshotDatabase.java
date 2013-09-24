package net.caprazzi.todowell;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SnapshotDatabase {

    private final AmazonS3 s3Client;

    private final static ObjectMapper mapper = new ObjectMapper();
    static {
        // TODO: fix joda time serialization and convert all to joda
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public SnapshotDatabase(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String htmlTemplate(String snapshotJson) {
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

    public void savePage(TodoSnapshot snapshot) throws IOException {
        String bucket = "giddone";
        String keyName = createKey(snapshot.getRepo());
        String json = mapper.writeValueAsString(snapshot);
        String html = htmlTemplate(json);
        System.out.println("storing: " + html);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/html");
        meta.setContentEncoding("UTF-8");
        meta.setContentEncoding("gzip");

        PutObjectRequest put = new PutObjectRequest(bucket, keyName, gzip(html), meta);
        put.setCannedAcl(CannedAccessControlList.PublicRead);

        System.out.println("put request: " + put);

        s3put(put);
    }

    private InputStream gzip(String string) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(out);

        gzipOut.write(string.getBytes("UTF-8"));
        gzipOut.flush();
        gzipOut.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    public void save(TodoSnapshot snapshot) throws IOException {
        String bucket = "giddone";
        String keyName = createKey(snapshot.getRepo());
        String json = mapper.writeValueAsString(snapshot);

        System.out.println("storing: " + json);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("application/json");
        meta.setContentEncoding("UTF-8");
        meta.setContentEncoding("gzip");

        PutObjectRequest put = new PutObjectRequest(bucket, keyName, gzip(json), meta);
        put.setCannedAcl(CannedAccessControlList.PublicRead);

        System.out.println("put request: " + put);

        s3put(put);
    }

    private void s3put(PutObjectRequest request) {
        try {
            s3Client.putObject(request);
        }
        catch (AmazonServiceException ase) {
            // TODO: actually propagate exceptions
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    private String createKey(Repository repo) {
        return String.format("%s/%s/%s", repo.getUser(), repo.getRepo(), repo.getBranch());
    }

}
