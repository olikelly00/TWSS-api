package tech.makers.aceplay.storage;

import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=810s
@RestController
public class StorageController {
  @Autowired private GoogleStorageService googleStorageService;

  @GetMapping("/api/storage/upload_key")
  public TrackUploadKeyDto getTrackUploadKey(
      @RequestParam String filename, @RequestParam String contentType)
      throws IOException, InvalidProposedMimeType {
    String uploadedFilename = makeUniqueFilename(filename);
    URL signedUploadUrl = googleStorageService.makeSignedUploadUrl(uploadedFilename, contentType);
    URL publicUrl = googleStorageService.getPublicUrl(uploadedFilename);
    return new TrackUploadKeyDto(publicUrl, signedUploadUrl);
  }

  private String makeUniqueFilename(String filename) {
    String extension = Files.getFileExtension(filename);
    String basename = Files.getNameWithoutExtension(filename);
    String uuid = UUID.randomUUID().toString();
    return String.format("%s-%s.%s", basename, uuid, extension);
  }
}
