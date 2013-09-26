package net.caprazzi.giddone.deploy;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.caprazzi.giddone.parsing.Repository;
import net.caprazzi.giddone.parsing.TodoSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class DeployService {

    private static final Logger Log = LoggerFactory.getLogger(DeployService.class);

    private final AmazonS3 s3Client;

    public DeployService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void deployHtmlPage(TodoSnapshot snapshot, String html) throws IOException {
        String bucket = "giddone";
        String keyName = createKey(snapshot.getRepo());
        Log.debug("Storing html: " + html);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/html");
        meta.setContentEncoding("UTF-8");
        meta.setContentEncoding("gzip");

        PutObjectRequest put = new PutObjectRequest(bucket, keyName, gzip(html), meta);
        put.setCannedAcl(CannedAccessControlList.PublicRead);

        Log.info("Saving to {}/{}", bucket, keyName);

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

    private void s3put(PutObjectRequest request) {
        try {
            s3Client.putObject(request);
        }
        catch (AmazonServiceException ase) {
            // TODO: actually propagate exceptions
            Log.error("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            Log.error("Error Message:    " + ase.getMessage());
            Log.error("HTTP Status Code: " + ase.getStatusCode());
            Log.error("AWS Error Code:   " + ase.getErrorCode());
            Log.error("Error Type:       " + ase.getErrorType());
            Log.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            Log.error("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            Log.error("Error Message: " + ace.getMessage());
        }
    }

    private String createKey(Repository repo) {
        return String.format("%s/%s/%s", repo.getUser(), repo.getRepo(), repo.getBranch());
    }

}
