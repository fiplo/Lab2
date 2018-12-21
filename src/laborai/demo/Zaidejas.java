package laborai.demo;
import java.util.Locale;
import laborai.studijosktu.KTUable;
import laborai.studijosktu.Ks;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 *   @author Paulius Ratkevičius
 */
public class Zaidejas implements KTUable<Zaidejas>{
    final static private int amziausVirsutineRiba = 2002;
    final static private int amziausApatineRiba = 1978;
    final static private int esamiMetai = LocalDate.now().getYear();

    private String Vardas = "";
    private String Pavarde = "";
    private int Ugis = -1;
    private int GimimoMetai = -1;
    private long Nasumas = -1;
    public Zaidejas() {

    }
    public Zaidejas(String vardas, String pavarde, int ugis, int gimimoMetai, long nasumas){
        this.Vardas = vardas;
        this.Pavarde = pavarde;
        this.Ugis = ugis;
        this.GimimoMetai = gimimoMetai;
        this.Nasumas = nasumas;
        validate();
    }

    public Zaidejas(Builder builder){
        this.Vardas = builder.Vardas;
        this.Pavarde = builder.Pavarde;
        this.Ugis = builder.Ugis;
        this.GimimoMetai = builder.GimimoMetai;
        this.Nasumas = builder.Nasumas;
        validate();
    }

    public Zaidejas(String dataString) {this.parse(dataString);}

    @Override
    public Zaidejas create(String dataString) { return new Zaidejas(dataString);}

    @Override
    public final void parse(String dataString){
        try{
            Scanner duomenys = new Scanner(dataString);
            Vardas = duomenys.next();
            Pavarde = duomenys.next();
            Ugis = duomenys.nextInt();
            GimimoMetai = duomenys.nextInt();
            Nasumas = duomenys.nextLong();
            validate();
        }
        catch (InputMismatchException exception) {
            Ks.ern("Blogas duomenų formatas apie žaidėją -> " + dataString);
        }
        catch (NoSuchElementException exception){
            Ks.ern("Trūksta duomenų apie žaidėją -> " + dataString);
        }
    }
    @Override
    public String validate() {
        String klaidosTipas = "";
        if(GimimoMetai > amziausVirsutineRiba || GimimoMetai < amziausApatineRiba || GimimoMetai > esamiMetai)
            klaidosTipas = "Netinkami gimimo metai, turi buti [" + amziausApatineRiba + ":" + esamiMetai + "]";
        return klaidosTipas;
    }

    @Override
    public String toString(){
        return String.format("%-8s %-12s %8d %8d %8d %s", Vardas, Pavarde, Ugis, GimimoMetai, Nasumas, validate());
    }

    public String getVardas() { return Vardas;}

    public String getPavarde() { return Pavarde;}

    public int getGimimoMetai() { return GimimoMetai;}

    public int getUgis() { return Ugis; }

    public long getNasumas() { return Nasumas; }

    @Override
    public int compareTo(Zaidejas zaidejas){
        int kitasUgis = zaidejas.getUgis();
        if(Ugis < kitasUgis) return -1;
        if(Ugis > kitasUgis) return 1;
        return 0;
    }

    public static Comparator<Zaidejas> pagalVarda = (Zaidejas zaidejas1, Zaidejas zaidejas2) -> (zaidejas1.Vardas.compareTo(zaidejas2.Vardas));

    public final static Comparator<Zaidejas> pagalVardaPavarde =
            new Comparator<Zaidejas>() {
        @Override
        public int compare(Zaidejas zaidejas1, Zaidejas zaidejas2) {
            int compare = zaidejas1.getVardas().compareTo(zaidejas2.getVardas());
            if(compare != 0) return compare;
            return zaidejas1.getPavarde().compareTo(zaidejas2.getPavarde());
        }
    };

    public final static Comparator<Zaidejas> pagalUgi
            = new Comparator<Zaidejas>() {
        @Override
        public int compare(Zaidejas zaidejas1, Zaidejas zaidejas2) {
            int kr1 = zaidejas1.getUgis();
            int kr2 = zaidejas2.getUgis();
            if(kr1 < kr2) return -1;
            if(kr1 > kr2) return 1;
            return 0;
        }
    };

    public final static Comparator<Zaidejas> pagalNasuma = new Comparator<Zaidejas>() {
        @Override
        public int compare(Zaidejas zaidejas1, Zaidejas zaidejas2) {
            long kr1 = zaidejas1.getNasumas();
            long kr2 = zaidejas2.getNasumas();
            if(kr1 < kr2) return -1;
            if(kr2 > kr1) return 1;
            return 0;
        }
    };

    public final static Comparator pagalMetusUgi = new Comparator<Zaidejas>() {
        @Override
        public int compare(Zaidejas zaidejas1, Zaidejas zaidejas2) {
            if (zaidejas1.getGimimoMetai() < zaidejas2.getGimimoMetai()) return 1;
            if (zaidejas1.getGimimoMetai() > zaidejas2.getGimimoMetai()) return -1;
            if (zaidejas1.getUgis() < zaidejas2.getUgis()) return 1;
            if (zaidejas1.getUgis() > zaidejas2.getUgis()) return -1;
            return 0;
        }
    };
    public static void main(String[] args) {
        Locale.setDefault(new Locale("LT"));
        Zaidejas zaidejas1 = new Zaidejas("Jonas", "Mačiulis", 198, 1999, 150);
        Zaidejas zaidejas2 = new Zaidejas("Jonas", "Valančiūnas", 213, 2001, 200);
        Zaidejas zaidejas3 = new Zaidejas("Paulius", "Jankūnas", 205, 2000, 500);
        Zaidejas zaidejas4 = new Zaidejas();
        zaidejas4.parse("Artūras Milaknis 198 2000 150");
        Ks.oun(zaidejas1);
        Ks.oun(zaidejas2);
        Ks.oun(zaidejas3);
        Ks.oun(zaidejas4);
    }

    public static class Builder{
        private final static Random RANDOM = new Random();
        private final static String[][] ZAIDEJAI = {
                {"Tomas", "A1", "A2", "A3"},
                {"Paulius", "B1", "B2", "B3", "B4"},
                {"Gediminas", "C1", "C2"},
                {"Oskaras", "D1", "D2", "D3", "D4"},
                {"Ramūnas", "E1", "E2", "E3"},
                {"Dominykas", "F1", "F2", "F3", "F4", "F5", "F6"}
        };

        private String Vardas = "";
        private String Pavarde = "";
        private int Ugis = -1;
        private int GimimoMetai = -1;
        private long Nasumas = -1;

        public Zaidejas build() { return new Zaidejas(this);}

        public Zaidejas buildRandom() {
            int ma = RANDOM.nextInt(ZAIDEJAI.length);
            int mo = RANDOM.nextInt(ZAIDEJAI[ma].length - 1) + 1;
            return new Zaidejas(ZAIDEJAI[ma][0],
                    ZAIDEJAI[ma][mo],
                    180 + RANDOM.nextInt(30),
                    1978 + RANDOM.nextInt(24),
                    100 + RANDOM.nextInt(500));
        }
        public Builder vardas(String vardas) {
            this.Vardas = vardas;
            return this;
        }

        public Builder pavarde(String pavarde){
            this.Pavarde = pavarde;
            return this;
        }

        public Builder ugis(int ugis){
            this.Ugis = ugis;
            return this;
        }

        public Builder gimimoMetai(int gimimoMetai){
            this.GimimoMetai = gimimoMetai;
            return this;
        }

        public Builder nasumas(long nasumas){
            this.Nasumas = nasumas;
            return this;
        }
    }
}


