package laborai.demo;
import java.util.Locale;
import javafx.application.Application;
import javafx.stage.Stage;
import laborai.gui.fx.Lab2WindowFX;
//import laborai.gui.swing.Lab2Window;

public class VykdymoModulisFXZaideju extends Application {
    public static void main(String[] args) {
        VykdymoModulisFXZaideju.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale.setDefault(Locale.US);
        ZaidejuTestai.aibesTestas();
        Lab2WindowFX.createAndShowFXGUI(primaryStage);
    }
}
