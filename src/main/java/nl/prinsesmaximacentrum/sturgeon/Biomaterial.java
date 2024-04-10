package nl.prinsesmaximacentrum.sturgeon;

import java.util.regex.Pattern;

public class Biomaterial {

    private String bm;
    private final Pattern bmPattern = Pattern.compile("(PM(C|A|G|L|R|O)|EXT|EGA|ENA)BM[0-9]{3}[A-Z]{3}");

    public Biomaterial() {}

    public boolean isBmValid(String bm) {
        return bmPattern.matcher(bm).find();
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = (this.isBmValid(bm)) ? bm : "";
    }
}
