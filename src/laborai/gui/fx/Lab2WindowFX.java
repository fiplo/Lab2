package laborai.gui.fx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import laborai.demo.Zaidejas;
import laborai.demo.ZaidejuGamyba;
import laborai.gui.MyException;
import laborai.studijosktu.*;
import laborai.demo.GreitaveikosTyrimas;

import java.io.File;
import java.util.*;

/**
 * Lab2 langas su JavaFX
 * <pre>
 *                   Lab2WindowFX (BorderPane)
 *  |-------------------------Top-------------------------|
 *  |                        menuFX                       |
 *  |------------------------Center-----------------------|
 *  |  |-----------------------------------------------|  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                    taOutput                   |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |                                               |  |
 *  |  |-----------------------------------------------|  |
 *  |------------------------Bottom-----------------------|
 *  |  |~~~~~~~~~~~~~~~~~~~paneBottom~~~~~~~~~~~~~~~~~~|  |
 *  |  |                                               |  |
 *  |  |  |-------------||------------||------------|  |  |
 *  |  |  | paneButtons || paneParam1 || paneParam2 |  |  |
 *  |  |  |             ||            ||            |  |  |
 *  |  |  |-------------||------------||------------|  |  |
 *  |  |                                               |  |
 *  |  |~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|  |
 *  |-----------------------------------------------------|
 * </pre>
 *
 * @author darius.matulis@ktu.lt
 */
public class Lab2WindowFX extends BorderPane implements EventHandler<ActionEvent> {

    private static ResourceBundle MESSAGES = ResourceBundle.getBundle("laborai.gui.messages");

    private static final int TF_WIDTH = 120;
    private static final int TF_WIDTH_SMALLER = 70;

    private static final double SPACING = 5.0;
    private static final Insets INSETS = new Insets(SPACING);
    private static final double SPACING_SMALLER = 2.0;
    private static final Insets INSETS_SMALLER = new Insets(SPACING_SMALLER);

    private final TextArea taOutput = new TextArea();
    private final GridPane paneBottom = new GridPane();
    private final GridPane paneParam2 = new GridPane();
    private final TextField tfDelimiter = new TextField();
    private final TextField tfInput = new TextField();
    private final ComboBox cmbTreeType = new ComboBox();
    private PanelsFX paneParam1, paneButtons;
    private MenuFX menuFX;
    private final Stage stage;

    private static BstSetKTU<Zaidejas> zaidejuSet;
    private int sizeOfInitialSubSet, sizeOfGenSet, sizeOfLeftSubSet;
    private double coef;
    private final String[] errors;

    public Lab2WindowFX(Stage stage) {
        this.stage = stage;
        errors = new String[]{
            MESSAGES.getString("error1"),
            MESSAGES.getString("error2"),
            MESSAGES.getString("error3"),
            MESSAGES.getString("error4")
        };
        initComponents();
    }

    private void initComponents() {
        //======================================================================
        // Sudaromas rezultatų išvedimo VBox klasės objektas, kuriame
        // talpinamas Label ir TextArea klasės objektai
        //======================================================================
        VBox vboxTaOutput = new VBox();
        vboxTaOutput.setPadding(INSETS_SMALLER);
        VBox.setVgrow(taOutput, Priority.ALWAYS);
        vboxTaOutput.getChildren().addAll(new Label(MESSAGES.getString("border1")), taOutput);
        //======================================================================
        // Formuojamas mygtukų tinklelis (mėlynas). Naudojama klasė PanelsFX.
        //======================================================================
        paneButtons = new PanelsFX(
                new String[]{
                    MESSAGES.getString("generuoti"),
                    MESSAGES.getString("perziuretiIter"),
                    MESSAGES.getString("papildyti"),
                    MESSAGES.getString("greitaveika"),
                    MESSAGES.getString("pasalinti"),
                    MESSAGES.getString("higher"),
                    MESSAGES.getString("pollLast"),
                    MESSAGES.getString("pollFirst"),
                    MESSAGES.getString("addAll"),
                    MESSAGES.getString("height"),
                    MESSAGES.getString("subSet")
                },
                4, 3);
        disableButtons(true);
        //======================================================================
        // Formuojama pirmoji parametrų lentelė (žalia). Naudojama klasė PanelsFX.
        //======================================================================
        paneParam1 = new PanelsFX(
                new String[]{
                    MESSAGES.getString("lblParam11"),
                    MESSAGES.getString("lblParam12"),
                    MESSAGES.getString("lblParam13"),
                    MESSAGES.getString("lblParam14"),
                    MESSAGES.getString("lblParam15")},
                new String[]{
                    MESSAGES.getString("tfParam11"),
                    MESSAGES.getString("tfParam12"),
                    MESSAGES.getString("tfParam13"),
                    MESSAGES.getString("tfParam14"),
                    MESSAGES.getString("tfParam15")},
                TF_WIDTH_SMALLER);
        //======================================================================
        // Formuojama antroji parametrų lentelė (gelsva).
        //======================================================================
        paneParam2.setAlignment(Pos.CENTER);
        paneParam2.setNodeOrientation(NodeOrientation.INHERIT);
        paneParam2.setVgap(SPACING);
        paneParam2.setHgap(SPACING);
        paneParam2.setPadding(INSETS);

        paneParam2.add(new Label(MESSAGES.getString("lblParam21")),     0, 0);
        paneParam2.add(new Label(MESSAGES.getString("lblParam22")), 0, 1);
        paneParam2.add(new Label(MESSAGES.getString("lblParam23")), 0, 2);

        cmbTreeType.setItems(FXCollections.observableArrayList(
                new String[]{
                    MESSAGES.getString("cmbTreeType1"),
                    MESSAGES.getString("cmbTreeType2"),
                    MESSAGES.getString("cmbTreeType3")
                }));
        cmbTreeType.setPrefWidth(TF_WIDTH);
        cmbTreeType.getSelectionModel().select(0);
        paneParam2.add(cmbTreeType, 1, 0);

        tfDelimiter.setPrefWidth(TF_WIDTH);
        tfDelimiter.setAlignment(Pos.CENTER);
        paneParam2.add(tfDelimiter, 1, 1);

        // Vėl pirmas stulpelis, tačiau plotis - 2 celės
        tfInput.setEditable(false);
        tfInput.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam2.add(tfInput, 0, 3, 2, 1);
        //======================================================================
        // Formuojamas bendras parametrų panelis (tamsiai pilkas).
        //======================================================================
        paneBottom.setPadding(INSETS);
        paneBottom.setHgap(SPACING);
        paneBottom.setVgap(SPACING);
        paneBottom.add(paneButtons, 0, 0);
        paneBottom.add(paneParam1, 1, 0);
        paneBottom.add(paneParam2, 2, 0);
        paneBottom.alignmentProperty().bind(new SimpleObjectProperty<>(Pos.CENTER));

        menuFX = new MenuFX() {
            @Override
            public void handle(ActionEvent ae) {
                Object source = ae.getSource();
                if (source.equals(menuFX.getMenus().get(0).getItems().get(0))) {
                    fileChooseMenu();
                } else if (source.equals(menuFX.getMenus().get(0).getItems().get(1))) {
                    KsFX.ounerr(taOutput, MESSAGES.getString("msg1"));
                } else if (source.equals(menuFX.getMenus().get(0).getItems().get(3))) {
                    System.exit(0);
                } else if (source.equals(menuFX.getMenus().get(1).getItems().get(0))) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle(MESSAGES.getString("menuItem21"));
                    alert.setHeaderText(MESSAGES.getString("author"));
                    alert.showAndWait();
                }
            }
        };

        //======================================================================
        // Formuojamas Lab2 langas
        //======================================================================
        // ..viršuje, centre ir apačioje talpiname objektus..
        setTop(menuFX);
        setCenter(vboxTaOutput);

        VBox vboxPaneBottom = new VBox();
        VBox.setVgrow(paneBottom, Priority.ALWAYS);
        vboxPaneBottom.getChildren().addAll(new Label(MESSAGES.getString("border2")), paneBottom);
        setBottom(vboxPaneBottom);
        appearance();

        paneButtons.getButtons().forEach((btn) -> {
            btn.setOnAction(this);
        });
        cmbTreeType.setOnAction(this);
    }

    private void appearance() {
        paneButtons.setBackground(new Background(new BackgroundFill(Color.rgb(112, 162, 255)/* Blyškiai mėlyna */, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam1.setBackground(new Background(new BackgroundFill(Color.rgb(204, 255, 204)/* Šviesiai žalia */, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam1.getTfOfTable().get(2).setEditable(false);
        paneParam1.getTfOfTable().get(2).setStyle("-fx-text-fill: red");
        paneParam1.getTfOfTable().get(4).setEditable(false);
        paneParam1.getTfOfTable().get(4).setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        paneParam2.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 153)/* Gelsva */, CornerRadii.EMPTY, Insets.EMPTY)));
        paneBottom.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        taOutput.setFont(Font.font("Monospaced", 12));
        taOutput.setStyle("-fx-text-fill: black;");
        taOutput.setEditable(false);
    }

    @Override
    public void handle(ActionEvent ae) {
        try {
            System.gc();
            System.gc();
            System.gc();
            Region region = (Region) taOutput.lookup(".content");
            region.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

            Object source = ae.getSource();
            if (source instanceof Button) {
                handleButtons(source);
            } else if (source instanceof ComboBox && source.equals(cmbTreeType)) {
                disableButtons(true);
            }
        } catch (MyException e) {
            if (e.getCode() >= 0 && e.getCode() <= 3) {
                KsFX.ounerr(taOutput, errors[e.getCode()] + ": " + e.getMessage());
                if (e.getCode() == 2) {
                    paneParam1.getTfOfTable().get(0).setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    paneParam1.getTfOfTable().get(1).setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            } else if (e.getCode() == 4) {
                KsFX.ounerr(taOutput, MESSAGES.getString("msg3"));
            } else {
                KsFX.ounerr(taOutput, e.getMessage());
            }
        } catch (UnsupportedOperationException e) {
            KsFX.ounerr(taOutput, e.getLocalizedMessage());
        } catch (Exception e) {
            KsFX.ounerr(taOutput, MESSAGES.getString("error5"));
            e.printStackTrace(System.out);
        }
    }

    private void handleButtons(Object source) throws MyException {
        if (source.equals(paneButtons.getButtons().get(0))) {
            treeGeneration(null);
        } else if (source.equals(paneButtons.getButtons().get(1))) {
            treeIteration();
        } else if (source.equals(paneButtons.getButtons().get(2))) {
            treeAdd();
        } else if (source.equals(paneButtons.getButtons().get(3))) {
            treeEfficiency();
        } else if (source.equals(paneButtons.getButtons().get(4))) {
            treeRemove();
        } else if (source.equals(paneButtons.getButtons().get(5))) {
            higherLab();
        } else if (source.equals(paneButtons.getButtons().get(6))) {
            pollLastLab();
        } else if (source.equals(paneButtons.getButtons().get(7))){
            pollFirstLab();
        } else if (source.equals(paneButtons.getButtons().get(8))) {
            addAllLab();
        } else if (source.equals(paneButtons.getButtons().get(9))) {
            height();
        } else if (source.equals(paneButtons.getButtons().get(10))) {
            subSetLab();
        }
    }

    private void treeGeneration(String filePath) throws MyException {
        // Nuskaitomi parametrai
        readTreeParameters();
        // Sukuriamas aibės objektas, priklausomai nuo medžio pasirinkimo
        // cmbTreeType objekte
        createTree();

        Zaidejas[] zaidejuArray;
        // Jei failas nenurodytas - generuojama
        if (filePath == null) {
            zaidejuArray = ZaidejuGamyba.generuotiIrIsmaisyti(sizeOfGenSet, sizeOfInitialSubSet, coef);
            paneParam1.getTfOfTable().get(2).setText(String.valueOf(sizeOfLeftSubSet));
        } else { // Skaitoma is failo
            zaidejuSet.load(filePath);
            zaidejuArray = new Zaidejas[zaidejuSet.size()];
            int i = 0;
            for (Object o : zaidejuSet.toArray()) {
                zaidejuArray[i++] = (Zaidejas) o;
            }
            // Skaitant iš failo išmaišoma standartiniu Collections.shuffle metodu.
            Collections.shuffle(Arrays.asList(zaidejuArray), new Random());
        }

        // Išmaišyto masyvo elementai surašomi i aibę
        zaidejuSet.clear();
        for (Zaidejas zaid : zaidejuArray) {
            zaidejuSet.add(zaid);
        }
        // Išvedami rezultatai
        // Nustatoma, kad eilutės pradžioje neskaičiuotų išvedamų eilučių skaičiaus
        KsFX.setFormatStartOfLine(true);
        KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()),
                MESSAGES.getString("msg5"));
        // Nustatoma, kad eilutės pradžioje skaičiuotų išvedamų eilučių skaičių
        KsFX.setFormatStartOfLine(false);
        disableButtons(false);
    }

    private void treeAdd() throws MyException {
        KsFX.setFormatStartOfLine(true);
        Zaidejas zaid = ZaidejuGamyba.gautiIsBazes();
        zaidejuSet.add(zaid);
        paneParam1.getTfOfTable().get(2).setText(String.valueOf(--sizeOfLeftSubSet));
        KsFX.oun(taOutput, zaid, MESSAGES.getString("msg7"));
        KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        KsFX.setFormatStartOfLine(false);
    }

    private void treeRemove() {
        KsFX.setFormatStartOfLine(true);
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            int nr = new Random().nextInt(zaidejuSet.size());
            Zaidejas zaid = (Zaidejas) zaidejuSet.toArray()[nr];
            zaidejuSet.remove(zaid);
            KsFX.oun(taOutput, zaid, MESSAGES.getString("msg6"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void treeIteration() {
        KsFX.setFormatStartOfLine(true);
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
        } else {
            KsFX.oun(taOutput, zaidejuSet, MESSAGES.getString("msg8"));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void treeEfficiency() {
        KsFX.setFormatStartOfLine(true);
        KsFX.oun(taOutput, "", MESSAGES.getString("msg2"));
        paneBottom.setDisable(true);
        menuFX.setDisable(true);

        GreitaveikosTyrimas gt = new GreitaveikosTyrimas();

        // Si gija paima rezultatus is greitaveikos tyrimo gijos ir isveda
        // juos i taOutput. Gija baigia darbą kai gaunama FINISH_COMMAND
        new Thread(() -> {
            try {
                String result;
                while (!(result = gt.getResultsLogger().take())
                        .equals(GreitaveikosTyrimas.FINISH_COMMAND)) {
                    KsFX.ou(taOutput, result);
                    gt.getSemaphore().release();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            gt.getSemaphore().release();
            paneBottom.setDisable(false);
            menuFX.setDisable(false);
        }, "Greitaveikos_rezultatu_gija").start();

        //Sioje gijoje atliekamas greitaveikos tyrimas
        new Thread(() -> gt.pradetiTyrima(), "Greitaveikos_tyrimo_gija").start();
    }

    private void readTreeParameters() throws MyException {
        // Truputėlis kosmetikos..
        for (int i = 0; i < 2; i++) {
            paneParam1.getTfOfTable().get(i).
                    setStyle("-fx-control-inner-background: white; ");
            paneParam1.getTfOfTable().get(i).applyCss();
        }
        // Nuskaitomos parametrų reiksmės. Jei konvertuojant is String
        // įvyksta klaida, sugeneruojama NumberFormatException situacija. Tam, kad
        // atskirti kuriame JTextfield'e ivyko klaida, panaudojama nuosava
        // situacija MyException
        int i = 0;
        try {
            // Pakeitimas (replace) tam, kad sukelti situaciją esant
            // neigiamam skaičiui
            sizeOfGenSet = Integer.valueOf(paneParam1.getParametersOfTable().get(i).replace("-", "x"));
            sizeOfInitialSubSet = Integer.valueOf(paneParam1.getParametersOfTable().get(++i).replace("-", "x"));
            sizeOfLeftSubSet = sizeOfGenSet - sizeOfInitialSubSet;
            ++i;
            coef = Double.valueOf(paneParam1.getParametersOfTable().get(++i).replace("-", "x"));
        } catch (NumberFormatException e) {
            // Galima ir taip: pagauti exception'ą ir vėl mesti
            throw new MyException(paneParam1.getParametersOfTable().get(i), e, i);
        }
    }

    private void createTree() throws MyException {
        switch (cmbTreeType.getSelectionModel().getSelectedIndex()) {
            case 0:
                zaidejuSet = new BstSetKTUx(new Zaidejas());
                break;
            case 1:
                zaidejuSet = new AvlSetKTUx(new Zaidejas());
                break;
            default:
                disableButtons(true);
                throw new MyException(MESSAGES.getString("msg1"));
        }
    }

    private void tailSetLab() {
        KsFX.setFormatStartOfLine(true);
        BstSetKTU<Zaidejas> additionalSet = new BstSetKTU<>();
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            int nr = new Random().nextInt(zaidejuSet.size());
            Zaidejas zaid = (Zaidejas) zaidejuSet.toArray()[nr];
            additionalSet = (BstSetKTU<Zaidejas>) zaidejuSet.tailSet(zaid);
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()), MESSAGES.getString("msg18"));
            KsFX.oun(taOutput, zaid, MESSAGES.getString("msg19"));
            KsFX.oun(taOutput, additionalSet.toVisualizedString(tfDelimiter.getText()), MESSAGES.getString("msg9"));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void higherLab() {
        KsFX.setFormatStartOfLine(true);
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            int nr = new Random().nextInt(zaidejuSet.size());
            Zaidejas zaid = (Zaidejas) zaidejuSet.toArray()[nr];
            Zaidejas retZaidejas = zaidejuSet.higher(zaid);

            KsFX.oun(taOutput, zaid, MESSAGES.getString("msg10"));
            KsFX.oun(taOutput, retZaidejas, MESSAGES.getString("msg13"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void pollFirstLab(){
        KsFX.setFormatStartOfLine(true);
        BstSetKTU<Zaidejas> additionalSet = new BstSetKTU<>();
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            Zaidejas salinamasZaidejas = zaidejuSet.pollFirst();
            KsFX.oun(taOutput, MESSAGES.getString("msg11"));
            KsFX.oun(taOutput, salinamasZaidejas, MESSAGES.getString("msg16"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void pollLastLab() {
        KsFX.setFormatStartOfLine(true);
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            Zaidejas salinamasZaidejas = zaidejuSet.pollLast();
            KsFX.oun(taOutput, MESSAGES.getString("msg11"));
            KsFX.oun(taOutput, salinamasZaidejas, MESSAGES.getString("msg16"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void addAllLab() {
        KsFX.setFormatStartOfLine(true);
        BstSetKTU<Zaidejas> additionalSet = new BstSetKTU<>();
        Zaidejas.Builder builder = new Zaidejas.Builder();
        int numOfRandoms = new Random().nextInt(20);
        for (int i = 0; i < numOfRandoms; i++) {
            additionalSet.add(builder.buildRandom());
        }
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            zaidejuSet.addAll(additionalSet);
            KsFX.oun(taOutput, additionalSet.toVisualizedString(tfDelimiter.getText()), MESSAGES.getString("msg12"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()), MESSAGES.getString("msg14"));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void height() {
        KsFX.setFormatStartOfLine(true);
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            int aibesAukstis = zaidejuSet.getHeight();
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
            KsFX.oun(taOutput, aibesAukstis, MESSAGES.getString("msg15"));
        }
        KsFX.setFormatStartOfLine(false);
    }

    private void subSetLab() {
        KsFX.setFormatStartOfLine(true);
        BstSetKTU<Zaidejas> suformuotaAibe = new BstSetKTU<>();
        if (zaidejuSet.isEmpty()) {
            KsFX.ounerr(taOutput, MESSAGES.getString("msg4"));
            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()));
        } else {
            Random random = new Random();
            int nr = random.nextInt(zaidejuSet.size());
            int nr1 = random.nextInt(zaidejuSet.size());
            boolean inclusive1 = random.nextBoolean();
            boolean inclusive2 = random.nextBoolean();
            Zaidejas fromElement = (Zaidejas) zaidejuSet.toArray()[nr];
            Zaidejas toElement = (Zaidejas) zaidejuSet.toArray()[nr1];


            suformuotaAibe = (BstSetKTU<Zaidejas>) zaidejuSet.subSet(fromElement, inclusive1, toElement, inclusive2);

            KsFX.oun(taOutput, zaidejuSet.toVisualizedString(tfDelimiter.getText()), MESSAGES.getString("msg18"));
            KsFX.oun(taOutput, fromElement, "Nuo elemento, kuris yra inclusive: " + inclusive1);
            KsFX.oun(taOutput, toElement, "Iki elemento, kuris yra inclusive: " + inclusive2);
            KsFX.oun(taOutput, suformuotaAibe.toVisualizedString(tfDelimiter.getText()), MESSAGES.getString("msg17"));
        }
        KsFX.setFormatStartOfLine(false);
    }

    protected void disableButtons(boolean disable) {
        for (int i : new int[]{1, 2, 4, 5, 6, 7, 8, 9, 10}) {
            if (i < paneButtons.getButtons().size() && paneButtons.getButtons().get(i) != null) {
                paneButtons.getButtons().get(i).setDisable(disable);
            }
        }
    }

    private void fileChooseMenu() throws MyException {
        FileChooser fc = new FileChooser();
        // Papildoma mūsų sukurtais filtrais
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("txt", "*.txt")
        );
        fc.setTitle((MESSAGES.getString("menuItem11")));
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            treeGeneration(file.getAbsolutePath());
        }
    }

    public static void createAndShowFXGUI(Stage stage) {
        MESSAGES = ResourceBundle.getBundle("laborai.gui.messages");
        Locale.setDefault(Locale.US); // Suvienodiname skaičių formatus
        Lab2WindowFX window = new Lab2WindowFX(stage);
        stage.setScene(new Scene(window, 1100, 650));
        stage.setTitle(MESSAGES.getString("title"));
        stage.getIcons().add(new Image("file:" + MESSAGES.getString("icon")));
        stage.show();
    }
}