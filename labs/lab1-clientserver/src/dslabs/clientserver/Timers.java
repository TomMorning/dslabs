package dslabs.clientserver;

import dslabs.framework.Timer;
import lombok.Data;

@Data
final class ClientTimer implements Timer {
  static final int CLIENT_RETRY_MILLIS = 200;
  private final Request request;
}
