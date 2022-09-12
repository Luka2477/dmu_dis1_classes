package workshop.task2;

public class Connection {

  private boolean connected;
  private boolean waiting;

  public Connection(boolean connected, boolean waiting) {
    this.connected = connected;
    this.waiting = waiting;
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public boolean isWaiting() {
    return waiting;
  }

  public void setWaiting(boolean waiting) {
    this.waiting = waiting;
  }
}
