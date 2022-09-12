package task12;

public record Person(int id, String name, String city) {

  public String toQueryString() {
    return String.format("id=%s&name=%s&city=%s", id, name, city);
  }

  @Override
  public String toString() {
    return "Person{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", city='" + city + '\'' +
            '}';
  }

}
