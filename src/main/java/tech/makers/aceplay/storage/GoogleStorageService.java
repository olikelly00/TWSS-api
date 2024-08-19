package tech.makers.aceplay.storage;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.storage.*;
import com.google.common.net.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=1084s
@Service
public class GoogleStorageService {
  private static final String STORAGE_URL = "https://storage.googleapis.com/";

  @Autowired private CredentialsProvider credentialsProvider;

  @Autowired private GcpProjectIdProvider projectIdProvider;

  @Value("${gcp-bucket-name}")
  private String gcpBucketName;

  public URL makeSignedUploadUrl(String filename, String contentType)
      throws IOException, InvalidProposedMimeType {
    validateUploadContentType(contentType);

    Storage storage = makeStorage();
    BlobInfo blobInfo = makeBlobInfo(filename);

    Map<String, String> extensionHeaders = makeHeaders(contentType);
    return storage.signUrl(
        blobInfo,
        15,
        TimeUnit.MINUTES,
        Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
        Storage.SignUrlOption.withExtHeaders(extensionHeaders),
        Storage.SignUrlOption.withV4Signature());
  }

  public URL getPublicUrl(String filename) throws MalformedURLException {
    return new URL(STORAGE_URL + gcpBucketName + "/" + filename);
  }

  private Map<String, String> makeHeaders(String contentType) {
    Map<String, String> extensionHeaders = new HashMap<>();
    extensionHeaders.put("Content-Type", contentType);
    return extensionHeaders;
  }

  private BlobInfo makeBlobInfo(String filename) {
    return BlobInfo.newBuilder(BlobId.of(gcpBucketName, filename)).build();
  }

  private Storage makeStorage() throws IOException {
    Credentials credentials = credentialsProvider.getCredentials();
    String projectId = projectIdProvider.getProjectId();
    return StorageOptions.newBuilder()
        .setCredentials(credentials)
        .setProjectId(projectId)
        .build()
        .getService();
  }

  private void validateUploadContentType(String contentType) throws InvalidProposedMimeType {
    MediaType givenType = MediaType.parse(contentType);
    if (!givenType.is(MediaType.ANY_AUDIO_TYPE)) {
      throw new InvalidProposedMimeType(contentType, "Only audio files are supported");
    }
  }
}
