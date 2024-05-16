package graphics;

import core.FileBindings;
import core.Helper;
import enums.QualityType;
import enums.SamplingType;
import enums.TransformType;
import enums.YCbCrType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import Jpeg.ColorTransform;
import Jpeg.Process;
import Jpeg.Quality;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MainWindowController implements Initializable {

    @FXML
    Button buttonInverseQuantize;
    @FXML
    Button buttonInverseToRGB;
    @FXML
    Button buttonInverseSample;
    @FXML
    Button buttonInverseTransform;
    @FXML
    Button buttonQuantize;
    @FXML
    Button buttonSample;
    @FXML
    Button buttonToYCbCr;
    @FXML
    Button buttonTransform;

    @FXML
    TextField inputMSE;
    @FXML
    TextField inputPSNR;
    @FXML
    TextField inputMAE;
    @FXML
    TextField inputSAE;

    @FXML
    Slider quantizeQuality;
    @FXML
    TextField quantizeQualityField;

    @FXML
    CheckBox shadesOfGrey;
    @FXML
    CheckBox showSteps;

    @FXML
    Spinner<Integer> transformBlock;
    @FXML
    ComboBox<TransformType> transformType;
    @FXML
    ComboBox<SamplingType> sampling;
    @FXML
    ComboBox<QualityType> quality;
    @FXML
    Button countError;
    @FXML
    Button buttonWatermark;
    @FXML
    Spinner spinnerWatermark;
    @FXML
    ComboBox<YCbCrType> comboboxWatermark;
    @FXML
    Button buttonMirror;
    @FXML
    Button buttonCrop;
    @FXML
    Button removeWatermark;
    @FXML
    Button buttonRotate;
    @FXML
    Button buttonRotate45;

    private Process process;
    /**
     * Inicializace okna, nastavení výchozích hodnot. Naplnění prvků v rozhraní.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nastavení všech hodnot do combo boxů
        sampling.getItems().setAll(SamplingType.values());
        transformType.getItems().setAll(TransformType.values());
        quality.getItems().setAll(QualityType.values());
        // Nastavení výchozích hodnot
        quality.getSelectionModel().select(QualityType.RGB);
        sampling.getSelectionModel().select(SamplingType.S_4_4_4);
        transformType.getSelectionModel().select(TransformType.DCT);
        quantizeQuality.setValue(50);



        comboboxWatermark.getItems().addAll(YCbCrType.values());

        ObservableList<Integer> depths = FXCollections.observableArrayList(1,2,3,4,5,6,7,8);
        SpinnerValueFactory<Integer> spinnerValuesDepth = new SpinnerValueFactory.ListSpinnerValueFactory<>(depths);
        spinnerWatermark.setValueFactory(spinnerValuesDepth);
        // Vytvoření listu možností, které budou uvnitř spinneru
        ObservableList<Integer> blocks = FXCollections.observableArrayList(2, 4, 8, 16, 32, 64, 128, 256, 512);
        SpinnerValueFactory<Integer> spinnerValues = new SpinnerValueFactory.ListSpinnerValueFactory<>(blocks);
        spinnerValues.setValue(8);
        transformBlock.setValueFactory(spinnerValues);


        // Nastavení formátu čísel v textových polích, aby bylo možné zadávat pouze čísla. Plus metoda, která je na konci souboru.
        quantizeQualityField.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));

        // Propojení slideru s textovým polem
        quantizeQualityField.textProperty().bindBidirectional(quantizeQuality.valueProperty(), NumberFormat.getIntegerInstance());


        process = new Process(FileBindings.defaultImage);
    }



    /**
     * Jednotlivé metody pro napojené na frontend.
     * Všechny tlačítka by měly být ošetřeny try..catch, aby aplikace nespadla, když nastane vyjímka.
     */

    /**
     * Zavření okna.
     */
    public void close() {
        Stage stage = ((Stage) buttonSample.getScene().getWindow());
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
    /**
     *
     */
    public void closeWindows() {
        Dialogs.closeAllWindows();
    }

    /**
     * Zobrazení originálního obrázku
     */
    public void showOriginal() {
        File f = new File(FileBindings.defaultImage);
        try {
            Dialogs.showImageInWindow(ImageIO.read(f), "Original", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TBD změna obrázku
     */
    public void changeImage() {
    }

    /**
     * TBD zrušení změn
     */
    public void reset() {
    }

    /**
     * Zobrazení upraveného RGB obrázku
     */
    public void showRGBModified() {
        try {
            Dialogs.showImageInWindow(process.getImageFromRGB(), "Modified RGB", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Převod z grayscale do RBG
     */
    public void convertToRGB() {
        try {
            process.convertToRGB();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Převod z RGB do grayscale
     */
    public void convertToYCbCr() {
        try {
            process.convertToYCbCr();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sample() {
        try {
            process.downSample(sampling.getValue());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public void inverseSample() {
        try {
            process.upSample(sampling.getValue());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void transform() {
        process.transformImage(transformType.getValue(),transformBlock.getValue());
    }

    public void inverseTransform() {
        process.inverseTransformImage(transformType.getValue(),transformBlock.getValue());

    }

    public void quantize() {
    process.quantize(transformBlock.getValue(),quantizeQuality.getValue());
    }

    public void inverseQuantize() {
        process.inverseQuantize(transformBlock.getValue(),quantizeQuality.getValue());

    }

    public void countQuality() {
        double MSE = process.countMSE(quality.getValue());
        double PSNR = process.countPSNR(quality.getValue()) ;
        double MAE = process.countMAE(quality.getValue());
        double SAE = process.countSAE(quality.getValue());
        inputMSE.setText(MSE+ " ");
        inputPSNR.setText(PSNR + "");
        inputMAE.setText(MAE + "");
        inputSAE.setText(SAE + "");

    }

    /**
     * Zobrazení modré složky modifikovaného RGB obrázku
     */
    public void showBlueModified() {
        try {
            Dialogs.showImageInWindow(process.showModifBlue(), "Modified Blue", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení modré složky originálního RGB obrázku
     */
    public void showBlueOriginal() {
        try {
            Dialogs.showImageInWindow(process.showOrigBlue(), "Original Blue", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení Cb složky modifikovaného YCbCr obrázku
     */
    public void showCbModified() {
        try {
            Dialogs.showImageInWindow(process.showModifCb(), "Modified Cb", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení Cb složky originálního YCbCr obrázku
     */
    public void showCbOriginal() {
        try {
            Dialogs.showImageInWindow(process.showOrigCb(), "Original Cb", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení Cr složky modifikovaného YCbCr obrázku
     */
    public void showCrModified() {
        try {
            Dialogs.showImageInWindow(process.showModifCr(), "Modified Cr", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení Cr složky originálního YCbCr obrázku
     */
    public void showCrOriginal() {
        try {
            Dialogs.showImageInWindow(process.showOrigCr(), "Original Cr", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení zelené složky modifikovaného RGB obrázku
     */
    public void showGreenModified() {
        try {
            Dialogs.showImageInWindow(process.showModifGreen(), "Modified Green", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení zelené složky originálního RGB obrázku
     */
    public void showGreenOriginal() {
        try {
            Dialogs.showImageInWindow(process.showOrigGreen(), "Original Green", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení červené složky modifikovaného RGB obrázku
     */
    public void showRedModified() {
        try {
            Dialogs.showImageInWindow(process.showModifRed(), "Modified Red", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení červené složky originálního RGB obrázku
     */
    public void showRedOriginal() {
        try {
            Dialogs.showImageInWindow(process.showOrigRed(), "Original Red", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení Y složky modifikovaného YCbCr obrázku
     */
    public void showYModified() {
        try {
            Dialogs.showImageInWindow(process.showModifY(), "Modified Y", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Zobrazení Y složky originálního YCbCr obrázku
     */
    public void showYOriginal() {
        try {
            Dialogs.showImageInWindow(process.showOrigY(), "Original Y", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void YCbCrWatermark() throws IOException {
        System.out.println("watermarking");
        process.watermarkYCbCr(comboboxWatermark.getSelectionModel().getSelectedItem(), (Integer) spinnerWatermark.getValue());
    }

    public void mirrorImage(){
        process.mirrorImage();
    }
    public void cropImage(){
        process.cropImage();
    }
    public void rotateImage(){
        process.rotateImage();
    }
    public void rotateImage45(){
        process.rotateImage45();
    }
    public void extractWatermark(){
        process.extractWatermark((Integer) spinnerWatermark.getValue(), comboboxWatermark.getSelectionModel().getSelectedItem() );
    }

}

