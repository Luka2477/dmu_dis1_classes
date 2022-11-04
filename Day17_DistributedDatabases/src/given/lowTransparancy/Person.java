package given.lowTransparancy;

public class Person {
    private String Fornavn;
    private String Navn;
    private String Koen;
    private int Loen;
    private String Postnr;

    public String getFornavn() {
        return Fornavn;
    }
    public void setFornavn(String fornavn){
        Fornavn = fornavn;
    }
    public int getLoen() {
        return Loen;
    }
    public void setLoen(int loen) {
        Loen = loen;
    }
    public String getNavn() {
        return Navn;
    }
    public void setNavn(String navn) {
        Navn = navn;
    }
    public String getKoen() {
        return Koen;
    }
    public void setKoen(String koen) {
        Koen = koen;
    }
    public String getPostnr() {
        return Postnr;
    }
    public void setPostnr(String postnr){
        Postnr = postnr;
    }
}
