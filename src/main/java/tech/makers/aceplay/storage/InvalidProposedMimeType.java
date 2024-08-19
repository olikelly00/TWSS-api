package tech.makers.aceplay.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=1262s
@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidProposedMimeType extends Exception {
  public InvalidProposedMimeType(String mimeType, String message) {
    super(String.format("Invalid mime type \"%s\": %s", mimeType, message));
  }
}
