package dslabs.atmostonce;

import dslabs.framework.Application;
import dslabs.framework.Command;
import dslabs.framework.Result;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class AMOApplication<T extends Application> implements Application {
  @Getter @NonNull private final T application;

  Map<String, AMOResult> executedCommands = new ConcurrentHashMap<>();

  @Override
  public AMOResult execute(Command command) {
    if (!(command instanceof AMOCommand amoCommand)) {
      throw new IllegalArgumentException();
    }
    String uniqueKey = amoCommand.sender();
    if (alreadyExecuted(amoCommand)) {
      return executedCommands.get(uniqueKey);
    }
    Result result = application.execute(amoCommand.command());
    AMOResult amoResult = new AMOResult(result, amoCommand.sequenceNumber());
    executedCommands.put(uniqueKey, amoResult);
    return amoResult;
  }

  public Result executeReadOnly(Command command) {
    if (!command.readOnly()) {
      throw new IllegalArgumentException();
    }

    if (command instanceof AMOCommand) {
      return execute(command);
    }

    return application.execute(command);
  }

  public boolean alreadyExecuted(AMOCommand amoCommand) {
    String uniqueKey = amoCommand.sender();
    return executedCommands.containsKey(uniqueKey) && executedCommands.get(uniqueKey).sequenceNumber() >= amoCommand.sequenceNumber();
  }
}
