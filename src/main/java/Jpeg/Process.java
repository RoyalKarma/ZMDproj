package Jpeg;
import Jama.Matrix;
import enums.*;
import graphics.Dialogs;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static enums.ColorType.*;

/**
 * Třída, která bude obsahovat všechnu práci s obrázkem.
 */
public class Process {

    private BufferedImage originalImage;
    private int imageHeight;
    private int imageWidth;
    private int [][] originalRed, modifiedRed;
    private int [][] originalGreen, modifiedGreen;
    private int [][] originalBlue, modifiedBlue;

    private Matrix originalY, modifiedY;
    private Matrix originalCb, modifiedCb;
    private Matrix originalCr, modifiedCr;

    public int waterHeight, waterWidth;
    public int [][] extractMark;


    /**
     * Kontruktor
     * Musím předat adresu originálního obrázku.
     * Zároveň z něj musím inicializovat i jednotlivé matice s barvami.
     * @param path Cesta k obrázku
     */
    public Process(String path) {

        this.originalImage = Dialogs.loadImageFromPath(path);

        imageWidth = originalImage.getWidth();
        imageHeight = originalImage.getHeight();

        originalRed = new int[imageHeight][imageWidth];
        originalGreen = new int[imageHeight][imageWidth];
        originalBlue = new int[imageHeight][imageWidth];

        originalY = new Matrix(imageHeight, imageWidth);
        originalCb = new Matrix(imageHeight, imageWidth);
        originalCr = new Matrix(imageHeight, imageWidth);

        modifiedY = new Matrix(imageHeight, imageWidth);
        modifiedCb = new Matrix(imageHeight, imageWidth);
        modifiedCr = new Matrix(imageHeight, imageWidth);
        setOriginalRGB();
    }
    /**
     * Naplnění matic originálními barvami
     */
    private void setOriginalRGB(){
        for(int h = 0; h < imageHeight; h++){
            for (int w = 0; w < imageWidth; w++){
                Color color = new Color(originalImage.getRGB(w,h));
                originalRed[h][w] = color.getRed();
                originalGreen[h][w] = color.getGreen();
                originalBlue[h][w] = color.getBlue();
            }
        }
    }
    private int [][] loadWatermark() {
        BufferedImage watermarkImage = Dialogs.loadImageFromPath("Images/watermark.png");

        int waterHeight = watermarkImage.getHeight();
        int waterWidth = watermarkImage.getWidth();

        int[][] watermark = new int[waterHeight][waterWidth];

        for (int i = 0; i < waterHeight; i++) {
            for (int j = 0; j < waterWidth; j++) {
                Color color = new Color(watermarkImage.getRGB(j, i));
                watermark[i][j] = color.getRed();
            }
        }
        return watermark;
    }

    public void watermarkYCbCr(YCbCrType yCbCrType, int depth) throws IOException {
        switch (yCbCrType){
            case Y:
                modifiedY = Watermark.WatermarkYCbCr(originalY,depth, loadWatermark());
                break;
            case Cb:
                modifiedCb =Watermark.WatermarkYCbCr(originalCb, depth, loadWatermark());
                break;
            case Cr:
                modifiedCr = Watermark.WatermarkYCbCr(originalCr, depth, loadWatermark());
                break;
        }

    }
    public void mirrorImage(){
     modifiedY = Attacks.Mirroring(modifiedY);
     modifiedCb = Attacks.Mirroring(modifiedCb);
     modifiedCr = Attacks.Mirroring(modifiedCr);
    }

    public void cropImage(){
        modifiedY = Attacks.crop(modifiedY);
        modifiedCb = Attacks.crop(modifiedCb);
        modifiedCr = Attacks.crop((modifiedCr));
    }
    public void rotateImage(){
        modifiedY = Attacks.rotate90(modifiedY);
        modifiedCb = Attacks.rotate90(modifiedCb);
        modifiedCr = Attacks.rotate90(modifiedCr);
    }
    public void rotateImage45() {
        convertToRGB();
        BufferedImage newImageFromBuffer = Attacks.rotate45(getImageFromRGB(), 45.0);


        for(int h = 0; h < imageHeight; h++){
            for (int w = 0; w < imageWidth; w++){
                Color color = new Color(newImageFromBuffer.getRGB(w,h));
                modifiedRed [h][w]= color.getRed();
                modifiedGreen[h][w] = color.getGreen();
                modifiedBlue[h][w] = color.getBlue();

            }
        }
        Matrix[] pom = ColorTransform.convertOriginalRGBtoYcBcR(modifiedRed, modifiedGreen, modifiedBlue);
        modifiedY = pom[0];
        modifiedCb = pom[1];
        modifiedCr = pom[2];
    }

    /**
     * Zobrazení obrázku v upravených RGB barvách.
     * @return Vrácení výsledného obrázku
     */
    public BufferedImage getImageFromRGB(){
        BufferedImage bfImage = new BufferedImage(
                imageWidth, imageHeight,
                BufferedImage.TYPE_INT_RGB);
        for(int h = 0; h < imageHeight; h++){
            for (int w = 0; w < imageWidth; w++) {
                bfImage.setRGB(w,h,
                        (new Color(modifiedRed[h][w],
                                modifiedGreen[h][w],
                                modifiedBlue[h][w])).getRGB());
            }
        }
        return bfImage;
    }
    public BufferedImage showOneColorImageFromRGB(int[][] color, enums.ColorType type)
    {
        BufferedImage bfImage = new BufferedImage(
                color[0].length, color.length, BufferedImage.TYPE_INT_RGB);
        for (int h = 0; h < bfImage.getHeight(); h++) {
            for (int w = 0; w < bfImage.getWidth(); w++) {
                switch (type) {
                    case RED:
                        bfImage.setRGB(w, h, (new Color(color[h][w], 0, 0)).getRGB());
                        break;
                    case GREEN:
                        bfImage.setRGB(w, h, (new Color(0, color[h][w], 0)).getRGB());
                        break;
                    case BLUE:
                        bfImage.setRGB(w, h, (new Color(0, 0, color[h][w])).getRGB());
                        break;
                }
            }
        }

        return bfImage;
    }

    public BufferedImage showOneColorImageFromYCbCr(Matrix color)
    {
        BufferedImage bfImage = new BufferedImage(
                color.getColumnDimension(), color.getRowDimension(),
                BufferedImage.TYPE_INT_RGB);
        for(int h = 0; h < bfImage.getHeight(); h++){
            for (int w = 0; w < bfImage.getWidth(); w++) {
                int pixel =(int)color.get(h,w);
                if(pixel>255)
                    pixel=255;
                if(pixel<0)
                    pixel=0;
                int rgb =(new Color(pixel,pixel, pixel).getRGB());

                bfImage.setRGB(w,h,
                        rgb);
            }
        }
        return bfImage;
    }
    /**
     * Přepočet z originální grayscale do upraveného RGB
     * Jakmile budeme pracovat s grayscale obrzem, je nutné změnit paramaetry z orig grayscale na modified grayscale.
     */
    public void convertToRGB()
    {
        int[][][] pom = ColorTransform.convertModifiedYcBcRtoRGB(modifiedY, modifiedCb, modifiedCr);
        modifiedRed = pom[0];
        modifiedGreen = pom[1];
        modifiedBlue = pom[2];
    }

    public void convertToYCbCr()
    {
        Matrix[] pom = ColorTransform.convertOriginalRGBtoYcBcR(originalRed, originalGreen, originalBlue);
        originalY = pom[0];
        originalCb = pom[1];
        originalCr = pom[2];
        modifiedY =originalY.copy();
        modifiedCb = originalCb.copy();
        modifiedCr = originalCr.copy();
    }
    public void downSample(SamplingType samplingType){
        modifiedY = modifiedY.copy();
        modifiedCb = Sampling.sampleDown(modifiedCb, samplingType);
        modifiedCr = Sampling.sampleDown(modifiedCr, samplingType);

    }

    public void upSample(SamplingType samplingType){
        modifiedY = modifiedY.copy();
        modifiedCb = Sampling.sampleUp(modifiedCb, samplingType);
        modifiedCr = Sampling.sampleUp(modifiedCr, samplingType);

    }
    public void quantize(int blockSize,double quality ){
      modifiedY=  Quantization.quantize(modifiedY,blockSize,quality,true);
      modifiedCb=  Quantization.quantize(modifiedCb,blockSize,quality,false);
      modifiedCr=  Quantization.quantize(modifiedCr,blockSize,quality,false);
        }
    public void inverseQuantize(int blockSize,double quality ){
        modifiedY=  Quantization.inverseQuantize(modifiedY,blockSize,quality,true);
        modifiedCb=  Quantization.inverseQuantize(modifiedCb,blockSize,quality,false);
        modifiedCr=  Quantization.inverseQuantize(modifiedCr,blockSize,quality,false);
    }
// COUNT MSE
    public double countMSE(QualityType qualityType){
        double MSE = 0;
        switch (qualityType) {
            case Red:
                MSE =Quality.countMSE(convertIntToDouble(originalRed),convertIntToDouble(modifiedRed));
                break;
            case Green:
                MSE =Quality.countMSE(convertIntToDouble(originalGreen),convertIntToDouble(modifiedGreen));
                break;
            case Blue:
                MSE =Quality.countMSE(convertIntToDouble(originalBlue),convertIntToDouble(modifiedBlue));
                break;
            case Y:
                MSE =Quality.countMSE(originalY.getArray(),modifiedY.getArray());
                break;
            case Cb:
                MSE =Quality.countMSE(originalCb.getArray(),modifiedCb.getArray());
                break;
            case Cr:
                MSE =Quality.countMSE(originalCr.getArray(),modifiedCr.getArray());
                break;
            case RGB:
                break;
            case YCbCr:
                MSE = (Quality.countMSE(originalCb.getArray(),modifiedCb.getArray()) +
                        Quality.countMSE(originalY.getArray(),modifiedY.getArray()) +
                         Quality.countMSE(originalCr.getArray(), modifiedCr.getArray())
                        ) /3;
                break;
        }
        return MSE;
    }
    public double countMAE(QualityType qualityType){
        double MAE = 0;
        switch (qualityType) {
            case Red:
                MAE =Quality.countMAE(convertIntToDouble(originalRed),convertIntToDouble(modifiedRed));
                break;
            case Green:
                MAE =Quality.countMAE(convertIntToDouble(originalGreen),convertIntToDouble(modifiedGreen));
                break;
            case Blue:
                MAE =Quality.countMAE(convertIntToDouble(originalBlue),convertIntToDouble(modifiedBlue));
                break;
            case Y:
                MAE =Quality.countMAE(originalY.getArray(),modifiedY.getArray());
                break;
            case Cb:
                MAE =Quality.countMAE(originalCb.getArray(),modifiedCb.getArray());
                break;
            case Cr:
                MAE =Quality.countMAE(originalCr.getArray(),modifiedCr.getArray());
                break;
            case RGB:
                break;
            case YCbCr:
                MAE = (Quality.countMAE(originalCb.getArray(),modifiedCb.getArray()) +
                        Quality.countMAE(originalY.getArray(),modifiedY.getArray()) +
                        Quality.countMAE(originalCr.getArray(), modifiedCr.getArray())
                ) /3;
                break;
        }
        return MAE;
    }
    public double countSAE(QualityType qualityType){
        double SAE = 0;
        switch (qualityType) {
            case Red:
                SAE =Quality.countSAE(convertIntToDouble(originalRed),convertIntToDouble(modifiedRed));
                break;
            case Green:
                SAE =Quality.countSAE(convertIntToDouble(originalGreen),convertIntToDouble(modifiedGreen));
                break;
            case Blue:
                SAE =Quality.countSAE(convertIntToDouble(originalBlue),convertIntToDouble(modifiedBlue));
                break;
            case Y:
                SAE =Quality.countSAE(originalY.getArray(),modifiedY.getArray());
                break;
            case Cb:
                SAE =Quality.countSAE(originalCb.getArray(),modifiedCb.getArray());
                break;
            case Cr:
                SAE =Quality.countSAE(originalCr.getArray(),modifiedCr.getArray());
                break;
            case RGB:
                break;
            case YCbCr:
                SAE = (Quality.countSAE(originalCb.getArray(),modifiedCb.getArray()) +
                        Quality.countSAE(originalY.getArray(),modifiedY.getArray()) +
                        Quality.countSAE(originalCr.getArray(), modifiedCr.getArray())
                ) /3;
                break;
        }
        return SAE;
    }
    public double countPSNR(QualityType qualityType){
        double PSNR = 0;
        switch (qualityType) {
            case Red:
                PSNR =Quality.countPSNR(Quality.countMSE(convertIntToDouble(originalRed),convertIntToDouble(modifiedRed)));
                break;
            case Green:
                PSNR =Quality.countPSNR(Quality.countMSE(convertIntToDouble(originalGreen),convertIntToDouble(modifiedGreen)));
                break;
            case Blue:
                PSNR =Quality.countPSNR(Quality.countMSE(convertIntToDouble(originalBlue),convertIntToDouble(modifiedBlue)));
                break;
            case Y:
                PSNR =Quality.countPSNR(Quality.countMSE(originalY.getArray(),modifiedY.getArray()));
                break;
            case Cb:
                PSNR =Quality.countPSNR(Quality.countMSE(originalCb.getArray(),modifiedCb.getArray()));
                break;
            case Cr:
                PSNR =Quality.countPSNR(Quality.countMSE(originalCr.getArray(),modifiedCr.getArray()));
                break;
            case RGB:
                PSNR= Quality.countPSNRforRGB(countMSE(QualityType.Red),countMSE(QualityType.Green), countMSE(QualityType.Blue));
                break;
            case YCbCr:
                PSNR= Quality.countPSNRforRGB(countMSE(QualityType.Y),countMSE(QualityType.Cb), countMSE(QualityType.Cr));
                break;
        }
        return PSNR;
    }
    public void transformImage(TransformType type, int blockSize){
        modifiedY =Transform.transform(modifiedY, type,blockSize );
        modifiedCb =Transform.transform(modifiedCb, type,blockSize );
        modifiedCr =Transform.transform(modifiedCr, type,blockSize );

    }
    public void inverseTransformImage(TransformType type, int blockSize){
        modifiedY =Transform.inverseTransform(modifiedY, type,blockSize );
        modifiedCb =Transform.inverseTransform(modifiedCb, type,blockSize );
        modifiedCr =Transform.inverseTransform(modifiedCr, type,blockSize );

    }
    public double countPSNRforYCbCr(QualityType qualityType) {
        double PSNR = 0;
        return PSNR;
    }
    //Volání na základě stisku tlačítek pro zobrazení konkrétní barevné složky.
    public BufferedImage showOrigBlue()
    {
        return showOneColorImageFromRGB(originalBlue, BLUE);
    }
    public BufferedImage showOrigGreen()
    {
        return showOneColorImageFromRGB(originalGreen, GREEN);
    }
    public BufferedImage showOrigRed()
    {
        return showOneColorImageFromRGB(originalRed,  RED);
    }
    public BufferedImage showModifBlue()
    {
        return showOneColorImageFromRGB(modifiedBlue, BLUE);
    }
    public BufferedImage showModifGreen()
    {
        return showOneColorImageFromRGB(modifiedGreen, GREEN);
    }
    public BufferedImage showModifRed()
    {
        return showOneColorImageFromRGB(modifiedRed,  RED);
    }
    public BufferedImage showOrigY()
    {
        return  showOneColorImageFromYCbCr(originalY);
    }
    public BufferedImage showModifY() {return  showOneColorImageFromYCbCr(modifiedY);}
    public BufferedImage showOrigCb()
    {
        return  showOneColorImageFromYCbCr(originalCb);
    }
    public BufferedImage showModifCb() {return  showOneColorImageFromYCbCr(modifiedCb);}
    public BufferedImage showOrigCr()
    {
        return  showOneColorImageFromYCbCr(originalCr);
    }
    public BufferedImage showModifCr()
    {
        return  showOneColorImageFromYCbCr(modifiedCr);
    }

    public static double[][] convertIntToDouble(int[][] intArray) {
        double[][] doubleArray = new double[intArray.length][intArray[0].length];
        for (int i = 0; i < intArray.length; i++) {
            for (int j = 0; j < intArray[0].length; j++) {
                doubleArray[i][j] = (double) intArray[i][j];
            }
        }
        return doubleArray;
    }

    public void extractWatermark(int bitLevel, YCbCrType target) {

        int[][] watermark = loadWatermark();

        BufferedImage extracted = null;
        switch (target) {
            case Y:
                extracted = Watermark.extractWatermark(modifiedY, watermark, bitLevel);
                break;
            case Cb:
                extracted = Watermark.extractWatermark(modifiedCb, watermark, bitLevel);
                break;
            case Cr:
                extracted = Watermark.extractWatermark(modifiedCr, watermark, bitLevel);
                break;
        }
        int extractHeight = extracted.getHeight();
        int extractWidth = extracted.getWidth();
        System.out.println(extracted);

        extractMark = new int[512][512];

        for (int h = 0; h < extractHeight; h++){
            for (int w = 0; w < extractWidth; w++){
                Color color = new Color(extracted.getRGB(w, h));
                extractMark[h][w] = color.getRed();
            }
        }

        Dialogs.showImageInWindow(extracted, "Extracted Watermark");
    }


}
