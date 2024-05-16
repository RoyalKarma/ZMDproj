package Jpeg;

import Jama.Matrix;

public class ColorTransform {

    /**
     * Metoda pro přepočet RGB do grayscale.
     * @param red Dvourozměrné pole s hosnotami červené.
     * @param green Dvourozměrné pole s hodnotami zelené.
     * @param blue Dvourozměrné pole s hodnotami modré.
     * @return Pole typu Matrix, ve kterém jsou předány Matrixy pro každou barvu grayscale
     */
    public static Matrix[] convertOriginalRGBtoYcBcR(int[][] red, int[][] green, int[][] blue){
        //Při práci s dvojrozměrným polem se získávájí rozměry pomocí metody .lenght na matici jako celek a poté na jeden z řádků.
        Matrix convertedY = new Matrix(red.length, red[0].length);
        Matrix convertedCb = new Matrix(red.length, red[0].length);
        Matrix convertedCr = new Matrix(red.length, red[0].length);

        for (int i = 0; i < red.length; i++) {
            for (int j = 0; j < red[0].length; j++) {
                //Vzorec vycházející ze zadaní
                convertedY.set(i,j,0.257*red[i][j] + 0.504*green[i][j] + 0.098*blue[i][j] + 16);
                convertedCb.set(i,j,-0.148*red[i][j] - 0.291*green[i][j] + 0.439*blue[i][j] + 128);
                convertedCr.set(i,j,0.439*red[i][j] - 0.368*green[i][j] - 0.071*blue[i][j] + 128);
            }
        }
        return new Matrix[]{convertedY, convertedCb, convertedCr};
    }

    /**
     * Metoda pro přepočet grayscale do RGB.
     * @param Y Matrix barvy Y
     * @param Cb Matrix barvy Cb
     * @param Cr Matrix barvy Cr
     * @return Hodnota pole dvourozměrných polí s hodnotami RGB
     */
    public static int[][][] convertModifiedYcBcRtoRGB(Matrix Y, Matrix Cb, Matrix Cr){
        int[][] convertedRed = new int[Y.getColumnDimension()][Y.getRowDimension()];
        int[][] convertedGreen = new int[Y.getColumnDimension()][Y.getRowDimension()];;
        int[][] convertedBlue = new int[Y.getColumnDimension()][Y.getRowDimension()];;

        for (int i = 0; i < Y.getColumnDimension(); i++) {
            for (int j = 0; j < Y.getRowDimension(); j++) {
                int red = Math.round((float)(1.164*(Y.get(i,j)-16) + 1.596*(Cr.get(i,j)-128)));
                red = red > 255 ? 255 : red;
                red = red < 0 ? 0 : red;

                int green = Math.round((float)((1.164*(Y.get(i,j) - 16) - 0.813*(Cr.get(i,j)-128)) - 0.391*(Cb.get(i,j) -128)));
                green = green > 255 ? 255 : green;
                green = green < 0 ? 0 : green;

                int blue = Math.round((float)(1.164*(Y.get(i,j) -16) + 2.018*(Cb.get(i,j)-128)));
                blue = blue > 255 ? 255 : blue;
                blue = blue < 0 ? 0 : blue;

                convertedRed[i][j] = red;
                convertedGreen[i][j] = green;
                convertedBlue[i][j] = blue;
            }
        }
        return new int[][][]{convertedRed, convertedGreen, convertedBlue};
    }



}
