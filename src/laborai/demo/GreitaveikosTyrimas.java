package laborai.demo;

import laborai.gui.MyException;
import laborai.studijosktu.AvlSetKTU;
import laborai.studijosktu.BstSetKTU;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

/**
 *
 * @author Paulius Ratkevičius
 */
public class GreitaveikosTyrimas {
    public static final String FINISH_COMMAND = "finish";
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("laborai.gui.messages");

    private static final String[] TYRIMU_VARDAI = {"cntsTreeSet", "cntsHashSet", "cntsBstKtu", "cntsAvlKtu"};
    private static final int[] TIRIAMI_KIEKIAI = {10000, 20000, 40000, 1000000};

    private final BlockingQueue resultsLogger = new SynchronousQueue();
    private final Semaphore semaphore = new Semaphore(-1);
    private final Timekeeper tk;
    private final String[] errors;


    private final TreeSet<Integer> series1 = new TreeSet();
    private final HashSet<Integer> series2 = new HashSet();
    private final BstSetKTU<Integer> series3 = new BstSetKTU<>();
    private final AvlSetKTU<Integer> series4 = new AvlSetKTU<>();

    public GreitaveikosTyrimas() {
        semaphore.release();
        tk = new Timekeeper(TIRIAMI_KIEKIAI, resultsLogger, semaphore);
        errors = new String[]{
            MESSAGES.getString("error1"),
            MESSAGES.getString("error2"),
            MESSAGES.getString("error3"),
            MESSAGES.getString("error4")
        };
    }

    public void pradetiTyrima() {
        try {
            SisteminisTyrimas();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void SisteminisTyrimas() throws InterruptedException {
        try {
            for (int k : TIRIAMI_KIEKIAI) {
                Zaidejas[] zaidMas = ZaidejuGamyba.generuotiIrIsmaisyti(k, 1.0);
                series1.clear();
                series2.clear();
                tk.startAfterPause();
                tk.start();
                for (Zaidejas zaid : zaidMas) {
                    series1.add(zaid.getUgis());
                }
                tk.finish(TYRIMU_VARDAI[0]);
                for (Zaidejas zaid : zaidMas) {
                    series2.add(zaid.getUgis());
                }
                tk.finish(TYRIMU_VARDAI[1]);
                for (Zaidejas zaid : zaidMas) {
                    series3.contains(zaid.getUgis());
                }
                tk.finish(TYRIMU_VARDAI[2]);
                for (Zaidejas zaid : zaidMas) {
                    series4.contains(zaid.getUgis());
                }
                tk.finish(TYRIMU_VARDAI[3]);
                tk.seriesFinish();
            }
            tk.logResult(FINISH_COMMAND);
        } catch (MyException e) {
            if (e.getCode() >= 0 && e.getCode() <= 3) {
                tk.logResult(errors[e.getCode()] + ": " + e.getMessage());
            } else if (e.getCode() == 4) {
                tk.logResult(MESSAGES.getString("msg3"));
            } else {
                tk.logResult(e.getMessage());
            }
        }
    }

    public BlockingQueue<String> getResultsLogger() {
        return resultsLogger;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}