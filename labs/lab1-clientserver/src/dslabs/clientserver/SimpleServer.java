package dslabs.clientserver;

import dslabs.atmostonce.AMOApplication;
import dslabs.atmostonce.AMOResult;
import dslabs.framework.Address;
import dslabs.framework.Application;
import dslabs.framework.Node;
import dslabs.kvstore.KVStore;
import dslabs.kvstore.KVStore.KVStoreResult;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Simple server that receives requests and returns responses.
 *
 * <p>See the documentation of {@link Node} for important implementation notes.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class SimpleServer extends Node {
  // Your code here...
  AMOApplication<KVStore> amoApplication = new AMOApplication<>(new KVStore());

  /* -----------------------------------------------------------------------------------------------
   *  Construction and Initialization
   * ---------------------------------------------------------------------------------------------*/
  public SimpleServer(Address address, Application app) {
    super(address);

    // Your code here...
  }

  @Override
  public void init() {
    // No initialization necessary
  }

  /* -----------------------------------------------------------------------------------------------
   *  Message Handlers
   * ---------------------------------------------------------------------------------------------*/
  private void handleRequest(Request m, Address sender) {
    // Your code here...
    AMOResult result = amoApplication.execute(m.command());
    Reply reply = new Reply(result);
    send(reply, sender);
  }
}
