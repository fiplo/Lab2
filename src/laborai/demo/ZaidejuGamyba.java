package laborai.demo;

import laborai.gui.MyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;


public class ZaidejuGamyba {
    private static Zaidejas[] zaidejai;
    private static int pradzia = 0, galas = 0;
    private static boolean arPradzia = true;

    public static Zaidejas[] generuoti(int kiekis) {
        zaidejai = new Zaidejas[kiekis];
        for (int i = 0; i < kiekis; i++) {
            zaidejai[i] = new Zaidejas.Builder().buildRandom();
        }
        return zaidejai;
    }

    public static Zaidejas[] generuotiIrIsmaisyti(int aibesDydis,
                                                  double isbarstymoKoeficientas) throws MyException{
        return generuotiIrIsmaisyti(aibesDydis, aibesDydis, isbarstymoKoeficientas);
    }

    public static Zaidejas[] generuotiIrIsmaisyti(int aibesDydis, int aibesImtis, double isbarstymoKoeficientas) throws MyException{
        zaidejai = generuoti(aibesDydis);
        return ismaisyti(zaidejai, aibesImtis, isbarstymoKoeficientas);
    }

    public static Zaidejas[] ismaisyti(Zaidejas[] zaidejuBaze, int kiekis, double isbarstKoef) throws MyException {
        if (zaidejuBaze == null)
            throw new IllegalArgumentException("zaidejuBaze yra null");
        if (kiekis <= 0)
            throw new MyException(String.valueOf(kiekis), 1);
        if (zaidejuBaze.length < kiekis)
            throw new MyException(zaidejuBaze.length + " >= " + kiekis, 2);
        if ((isbarstKoef < 0) || (isbarstKoef > 1))
            throw new MyException(String.valueOf(isbarstKoef), 3);
        int likusiuKiekis = zaidejuBaze.length - kiekis;
        int pradziosIndeksas = (int) (likusiuKiekis * isbarstKoef / 2);

        Zaidejas[] pradineZaidejuImtis = Arrays.copyOfRange(zaidejuBaze, pradziosIndeksas, pradziosIndeksas + kiekis);
        Zaidejas[] likusiZaidejuImtis = Stream
                .concat(Arrays.stream(Arrays.copyOfRange(zaidejuBaze, 0, pradziosIndeksas)),
                        Arrays.stream(Arrays.copyOfRange(zaidejuBaze, pradziosIndeksas + kiekis, zaidejuBaze.length)))
                .toArray(Zaidejas[]::new);
        Collections.shuffle(Arrays.asList(pradineZaidejuImtis).subList(0, (int) (pradineZaidejuImtis.length * isbarstKoef)));
        Collections.shuffle(Arrays.asList(likusiZaidejuImtis).subList(0, (int) (likusiZaidejuImtis.length * isbarstKoef)));

        ZaidejuGamyba.pradzia = 0;
        ZaidejuGamyba.galas = likusiZaidejuImtis.length - 1;
        ZaidejuGamyba.zaidejai = likusiZaidejuImtis;
        return pradineZaidejuImtis;
    }

    public static Zaidejas gautiIsBazes() throws MyException {
        if((galas - pradzia) < 0)
            throw new MyException(String.valueOf(galas - pradzia), 4);
        arPradzia = !arPradzia;
        return zaidejai[arPradzia ? pradzia++ : galas --];
    }
}
