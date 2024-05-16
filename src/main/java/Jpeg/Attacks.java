package Jpeg;

import Jama.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Attacks {
    public static Matrix Mirroring(Matrix inputMatrix) {
        int rows = inputMatrix.getRowDimension();
        int cols = inputMatrix.getColumnDimension();
        Matrix mirroredMatrix = new Matrix(rows, cols);

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                mirroredMatrix.set(i, j, inputMatrix.get(i, cols - j - 1));
            }
        }
        return mirroredMatrix;
    }

    public static Matrix crop(Matrix inputMatrix) {
        int rows = inputMatrix.getRowDimension()/2;
        int cols = inputMatrix.getColumnDimension()/2;
        Matrix croppedMatrix = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                croppedMatrix.set(i,j, inputMatrix.get(i,j));
            }
        }

        return croppedMatrix;
    }
    public static Matrix rotate90(Matrix inputMatrix) {
        int rows = inputMatrix.getRowDimension();
        int cols = inputMatrix.getColumnDimension();
        Matrix rotatedMatrix = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedMatrix.set(i,j, inputMatrix.get(j,i));
            }
        }
        return rotatedMatrix;
    }
    public static BufferedImage rotate45(BufferedImage bimg, Double angle) {
        double sin = Math.abs(Math.sin(Math.toRadians(angle))), cos = Math.abs(Math.cos(Math.toRadians(angle)));
        int width = bimg.getWidth();
        int height = bimg.getHeight();
        int newidth = (int) Math.floor(width * cos + height * sin);
        int newheight = (int) Math.floor(height * cos + width * sin);

        BufferedImage rotated = new BufferedImage(newidth, newheight, bimg.getType());
        Graphics2D graphic = rotated.createGraphics();
        graphic.rotate(Math.toRadians(angle), width / 2, height / 2);
        graphic.drawRenderedImage(bimg, null);
        graphic.dispose();
        return rotated;
    }



}
