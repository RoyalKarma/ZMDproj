package Jpeg;

import Jama.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Watermark {
    public static Matrix WatermarkYCbCr(Matrix inputMatrix, int depth, int[][] watermark) throws IOException {
        Matrix outputMatrix = new Matrix(inputMatrix.getRowDimension(), inputMatrix.getColumnDimension());
        int watermarkHeight = watermark.length;
        int watermarkWidth = watermark[0].length;
        int width= 0;
        for (int i = 0; i < inputMatrix.getRowDimension() && i < watermarkHeight; i++) {
            for (int j = 0; j < inputMatrix.getColumnDimension() && j < watermarkWidth; j++) {
                double original = inputMatrix.get(i, j);
                int orgInt = (int) original;
                if(watermark[i][j]>127) {
                     width = 1;
                }
                else{
                     width =  0;
                }
                int dif = (width << (depth - 1)) - (orgInt & (1 << (depth - 1)));

                outputMatrix.set(i, j, original + dif);
            }
        }
        return outputMatrix;
    }
    public static BufferedImage extractWatermark(Matrix input, int[][] watermark, int bitLevel) {
        int watermarkHeight = watermark.length;
        int watermarkWidth = watermark[0].length;

        BufferedImage output = new BufferedImage(watermarkWidth, watermarkHeight, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < input.getRowDimension() && i < watermarkHeight; i++) {
            for (int j = 0; j < input.getColumnDimension() && j < watermarkWidth; j++) {
                int original = (int )input.get(i, j);
                int value = (original & (1 << (bitLevel-1))) > 0 ? 255 : 0;
                output.setRGB(j , i , new Color(value, value, value).getRGB());
            }
        }
        return output;
    }
}
