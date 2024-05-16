package Jpeg;

import Jama.Matrix;
import enums.TransformType;

import static enums.TransformType.WHT;

public class Transform {
    // Transformace předané barevné složky
    public static Matrix transform(Matrix input, TransformType type, int blockSize){
        Matrix outputMatrix = new Matrix(input.getRowDimension(), input.getColumnDimension());
        Matrix transforMat = getTransformMatrix(type,blockSize);
        for (int i = 0; i <input.getRowDimension() ; i+=blockSize) {
            for (int j = 0; j < input.getColumnDimension(); j+=blockSize) {
                Matrix temp = input.getMatrix(i,i+blockSize-1,j,j+blockSize-1);
                outputMatrix.setMatrix(i,i+blockSize-1,j,j+blockSize-1,transforMat.times(temp).times(transforMat.transpose()));
            }
        }
        return outputMatrix;

    }

    // Inverzní transformace
    public static Matrix inverseTransform(Matrix input, TransformType type, int blockSize){
        Matrix outputMatrix = new Matrix(input.getRowDimension(), input.getColumnDimension());
        Matrix transforMat = getTransformMatrix(type,blockSize);
        for (int i = 0; i <input.getRowDimension() ; i+=blockSize) {
            for (int j = 0; j < input.getColumnDimension(); j+=blockSize) {
                Matrix temp = input.getMatrix(i,i+blockSize-1,j,j+blockSize-1);
                outputMatrix.setMatrix(i,i+blockSize-1,j,j+blockSize-1,transforMat.transpose().times(temp).times(transforMat));
            }
        }
        return outputMatrix;

    }

    // Získání transformační matice, generované podle typu a velikosti bloku
    public static Matrix getTransformMatrix(TransformType type, int blockSize) {
        Matrix outputMatrix = new Matrix(blockSize, blockSize);
        double sqrt;
        switch (type) {
            case DCT:
                for (int i = 0; i < blockSize; i++) {
                    for (int j = 0; j < blockSize; j++) {
                        if (i == 0) {
                             sqrt = Math.sqrt(1.0 / blockSize);
                        } else {
                             sqrt = Math.sqrt(2.0 / blockSize);
                        }
                        double frac = (((2.0 * j + 1.0) * i * Math.PI) / (2.0 * blockSize));
                        double temp = sqrt * Math.cos(frac);
                        outputMatrix.set(i, j, temp);


                    }
                }
                break;
            case WHT:
                Matrix hamaradMatrix = new Matrix(blockSize,blockSize);
                hamaradMatrix.set(0,0,1);
                hamaradMatrix.set(1,0,1);
                hamaradMatrix.set(0,1,1);
                hamaradMatrix.set(1,1,-1);
                for (int k = 1; k < blockSize; k += k) {
                    for (int i = 0; i < k; i++) {
                        for (int j = 0; j < k; j++) {
                            hamaradMatrix.set(i+k,j,hamaradMatrix.get(i,j));
                            hamaradMatrix.set(i,j+k, hamaradMatrix.get(i,j));
                            hamaradMatrix.set(i+k,j+k, -1*hamaradMatrix.get(i,j));
                        }
                    }
                }
                outputMatrix= hamaradMatrix.times(1/Math.sqrt(blockSize));
                break;
        }


        return outputMatrix;
    }


}
