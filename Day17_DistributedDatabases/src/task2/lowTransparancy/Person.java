package task2.lowTransparancy;

public class Person {
    private String cpr;
    private String navn;
    private String byNavn;
    private int loen;
    private int skatteprocent;

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getByNavn() {
        return byNavn;
    }

    public void setByNavn(String byNavn) {
        this.byNavn = byNavn;
    }

    public int getLoen() {
        return loen;
    }

    public void setLoen(int loen) {
        this.loen = loen;
    }

    public int getSkatteprocent() {
        return skatteprocent;
    }

    public void setSkatteprocent(int skatteprocent) {
        this.skatteprocent = skatteprocent;
    }
}
