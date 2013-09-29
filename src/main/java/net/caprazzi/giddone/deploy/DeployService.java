package net.caprazzi.giddone.deploy;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.codahale.metrics.Timer;
import com.google.inject.Inject;
import net.caprazzi.giddone.Meters;
import net.caprazzi.giddone.model.Repository;
import net.caprazzi.giddone.model.TodoSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class DeployService {

    private static final Logger Log = LoggerFactory.getLogger(DeployService.class);

    private final AmazonS3 s3Client;

    @Inject
    public DeployService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void deployHtmlPage(TodoSnapshot snapshot, String html) throws IOException {
        String bucket = "giddone";
        String keyName = createKey(snapshot.getRepo());

        Log.info("Deploy started to {}/{}", bucket, keyName);



        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/html");
        meta.setContentEncoding("UTF-8");
        meta.setContentEncoding("gzip");

        PutObjectRequest request = new PutObjectRequest(bucket, keyName, gzip(html), meta);
        request.setCannedAcl(CannedAccessControlList.PublicRead);

        Timer.Context timer = Meters.system.deploying.time();
        try {
            s3Client.putObject(request);
        }
        catch (AmazonServiceException ase) {
            Log.error("Deploy failed with AWS Service Exception: {}", ase);
            throw ase;
        }
        catch (AmazonClientException ace) {
            Log.error("Deploy failed with AWS Client Exception: {}", ace);
        }
        finally {
            timer.stop();
        }

        Log.info("Deploy success");
    }

    private InputStream gzip(String string) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(out);

        gzipOut.write(string.getBytes("UTF-8"));
        gzipOut.flush();
        gzipOut.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String createKey(Repository repo) {
        return String.format("%s/%s/%s", repo.getUser(), repo.getRepo(), repo.getBranch());
    }

}
