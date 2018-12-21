package laborai.demo;

import laborai.studijosktu.Ks;
import laborai.studijosktu.AvlSetKTUx;
import laborai.studijosktu.SortedSetADTx;
import laborai.studijosktu.SetADT;
import laborai.studijosktu.BstSetKTUx;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

public class ZaidejuTestai {
    static Zaidejas[] zaidejuBaze;
    static SortedSetADTx<Zaidejas> KSerija = new BstSetKTUx(new Zaidejas(), Zaidejas.pagalUgi);

    public static void main(String[] args) throws CloneNotSupportedException {
        Locale.setDefault(Locale.US);
        aibesTestas();
    }

    static SortedSetADTx generuotiAibe(int kiekis, int genersk){
        zaidejuBaze = new Zaidejas[genersk];
        for (int i = 0; i < genersk; i++)
            zaidejuBaze[i] = new Zaidejas.Builder().buildRandom();
        Collections.shuffle(Arrays.asList(zaidejuBaze));
        KSerija.clear();
        for (int i = 0; i < kiekis; i++)
            KSerija.add(zaidejuBaze[i]);
        return KSerija;
    }

    public static void aibesTestas() throws CloneNotSupportedException {
        /*
        To be implemented
         */



    }
}
