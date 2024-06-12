package dslabs.clientserver;

import dslabs.atmostonce.AMOCommand;
import dslabs.atmostonce.AMOResult;
import dslabs.framework.Address;
import dslabs.framework.Client;
import dslabs.framework.Command;
import dslabs.framework.Node;
import dslabs.framework.Result;
import com.google.common.base.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.checkerframework.checker.units.qual.A;

/**
 * Simple client that sends requests to a single server and returns responses.
 *
 * <p>See the documentation of {@link Client} and {@link Node} for important implementation notes.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class SimpleClient extends Node implements Client {
  private final Address serverAddress;

  private Request request;
  private Reply reply;

  private static final AtomicInteger sequenceNumber = new AtomicInteger(0);

  /* -----------------------------------------------------------------------------------------------
   *  Construction and Initialization
   * ---------------------------------------------------------------------------------------------*/
  public SimpleClient(Address address, Address serverAddress) {
    super(address);
    this.serverAddress = serverAddress;
  }

  @Override
  public synchronized void init() {
    // No initialization necessary
  }

  /* -----------------------------------------------------------------------------------------------
   *  Client Methods
   * ---------------------------------------------------------------------------------------------*/
  @Override
  public synchronized void sendCommand(Command command) {
    AMOCommand amoCommand = new AMOCommand(command, sequenceNumber.addAndGet(1), address().toString());
    request = new Request(amoCommand);
    reply = null;
    send(request, serverAddress);
    set(new ClientTimer(request), ClientTimer.CLIENT_RETRY_MILLIS);
  }

  @Override
  public synchronized boolean hasResult() {
    return reply != null;
  }

  @Override
  public synchronized Result getResult() throws InterruptedException {
    while (reply == null) {
      wait();
    }
    AMOResult replyResult = reply.result();
    return replyResult.result();
  }

  /* -----------------------------------------------------------------------------------------------
   *  Message Handlers
   * ---------------------------------------------------------------------------------------------*/
  private synchronized void handleReply(Reply m, Address sender) {
    if (m.result().sequenceNumber() == request.command().sequenceNumber()) {
      reply = m;
      notifyAll();
    }
  }

  /* -----------------------------------------------------------------------------------------------
   *  Timer Handlers
   * ---------------------------------------------------------------------------------------------*/
  private synchronized void onClientTimer(ClientTimer t) {
   if (Objects.equal(t.request(), request) && reply == null) {
     send(t.request(), serverAddress);
     set(new ClientTimer(t.request()), ClientTimer.CLIENT_RETRY_MILLIS);
   }
  }
}
