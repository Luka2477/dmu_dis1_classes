package workshop.task4.v2.utils;

public abstract class Logger {

  public static void info(String message, Object... varargs) {
    System.out.printf(String.format("[INFO]   %s%n", message), varargs);
  }

  public static void warn(String message, Object... varargs) {
    System.out.printf(String.format("[WARN]   %s%n", message), varargs);
  }

  public static void error(String message, Object... varargs) {
    System.out.printf(String.format("[ERROR]  %s%n", message), varargs);
  }

}
