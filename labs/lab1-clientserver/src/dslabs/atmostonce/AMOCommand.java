package dslabs.atmostonce;

import dslabs.framework.Command;
import lombok.Data;

@Data
public final class AMOCommand implements Command {
  private final Command command;
  private final int sequenceNumber;
  private final String sender;
}
