package workshop.task3;

public class Connection {

  private boolean connected;

  public Connection(boolean connected) {
    this.connected = connected;
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

}
