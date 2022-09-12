package workshop.task4.v2.utils;

public abstract class PackageService {

  public static String construct(Package pack) {
    return String.format("%s-><-from=%s&to=%s&status=%s", pack.message, pack.from, pack.to, pack.status);
  }

  public static String construct(String message, String from, String to, String status) {
    return String.format("%s-><-from=%s&to=%s&status=%s", message, from, to, status);
  }

  public static Package deconstruct(String data) {
    Package pack = new Package();

    String[] split = data.split("-><-");
    String[] attrs = split[1].split("&");

    pack.message = split[0];
    pack.from = attrs[0].split("=")[1];
    pack.to = attrs[1].split("=")[1];
    pack.status = attrs[2].split("=")[1];

    return pack;
  }

}
