package Jpeg;

import Jama.Matrix;

public class Quality {
    // Výpočet MSE. Nutný převod pomocí Matrix.getArray() nebo převod z int[][] na double[][].
    public static double countMSE(double[][] original, double[][] modified) {

        double sum = 0;
        double width = original.length;
        double height = original[0].length;
        for (int i = 0; i < width ; i++) {
            for (int j = 0; j < height; j++) {
                sum+=Math.pow(Math.abs(original[i][j]-modified[i][j]), 2);
            }

        }
        double mse = sum/(width*height);
        return mse;
    }

    // Výpočet MAE. Nutný převod pomocí Matrix.getArray() nebo převod z int[][] na double[][].
    public static double countMAE(double[][] original, double[][] modified) {
        double sum = 0;
        double width = original.length;
        double height = original[0].length;
        for (int i = 0; i < width ; i++) {
            for (int j = 0; j < height; j++) {
                sum+=Math.abs(original[i][j]-modified[i][j]);
            }

        }
        double mae = sum/(width*height);
        return mae;
    }

    // Výpočet SAE. Nutný převod pomocí Matrix.getArray() nebo převod z int[][] na double[][].
    public static double countSAE(double[][] original, double[][] modified) {
        double sum = 0;

        double width = original.length;
        double height = original[0].length;
        for (int i = 0; i < width ; i++) {
            for (int j = 0; j < height; j++) {
                sum+=Math.abs(original[i][j]-modified[i][j]);
            }

        }
        double sae = sum;
        return sae;
    }

    // Výpočet PSNR z MSE
    public static double countPSNR(double MSE) {
        int n = 8;
        double psnr = 10*Math.log10(Math.pow(Math.pow(2,n)-1,2)/MSE);
        return psnr;
    }

    // Výpočet PSNR z MSE pro RGB obrázek (průměr z barev a použít např. countPSNR)
    public static double countPSNRforRGB(double mseRed, double mseGreen, double mseBlue) {
        double psnrRGB=0;
        Double MSE = ((mseRed+mseGreen+mseBlue)/3);
        psnrRGB = countPSNR(MSE);
        return psnrRGB;
    }
    public static double countPSNRforYCbCr(double mseRed, double mseGreen, double mseBlue) {
        double psnrRGB=0;
        Double MSE = ((mseRed+mseGreen+mseBlue)/3);
        psnrRGB = countPSNR(MSE);
        return psnrRGB;
    }

    // Výpočet SSIM, ponechte zde throw, pokud to nebudete implementovat
    public static double countSSIM(Matrix original, Matrix modified) {
        throw new RuntimeException("Not implemented yet.");
    }

    // Výpočet MSSIM, ponechte zde throw, pokud to nebudete implementovat
    public static double countMSSIM(Matrix original, Matrix modified) {
        throw new RuntimeException("Not implemented yet.");
    }



}
