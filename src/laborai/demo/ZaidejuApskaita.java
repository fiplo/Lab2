package laborai.demo;

import laborai.studijosktu.BstSetKTU;
import laborai.studijosktu.SetADT;

public class ZaidejuApskaita {
    public static SetADT<String> zaidejuVardai(Zaidejas[] zaidejai){
        SetADT<Zaidejas> uni = new BstSetKTU<>(Zaidejas.pagalVarda);
        SetADT<String> kart = new BstSetKTU<>();
        for (Zaidejas zaidejas : zaidejai){
            int sizeBefore = uni.size();
            uni.add(zaidejas);

            if (sizeBefore == uni.size()){
                kart.add(zaidejas.getVardas());
            }
        }
        return kart;
    }
}
