package tech.makers.aceplay.storage;

import java.net.URL;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=1010s
class TrackUploadKeyDto {
  private URL signedUploadUrl;
  private URL publicUrl;

  public TrackUploadKeyDto(URL publicUrl, URL signedUploadUrl) {
    this.setSignedUploadUrl(signedUploadUrl);
    this.setPublicUrl(publicUrl);
  }

  public URL getSignedUploadUrl() {
    return signedUploadUrl;
  }

  public void setSignedUploadUrl(URL signedUploadUrl) {
    this.signedUploadUrl = signedUploadUrl;
  }

  public URL getPublicUrl() {
    return publicUrl;
  }

  public void setPublicUrl(URL publicUrl) {
    this.publicUrl = publicUrl;
  }
}
