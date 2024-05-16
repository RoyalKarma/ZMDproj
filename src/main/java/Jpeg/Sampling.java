package Jpeg;

import Jama.Matrix;
import enums.SamplingType;

public class Sampling {

    public static Matrix sampleUp(Matrix mat, SamplingType samplingType) {
        Matrix outputMatrix = new Matrix(mat.getRowDimension(), mat.getRowDimension());
        switch (samplingType) {
            case S_4_4_4:
                outputMatrix = mat;
                break;
            case S_4_2_2:
                outputMatrix = upSample(mat);
                break;
            case S_4_2_0:
                mat = upSample(mat);
                mat=mat.transpose();

                outputMatrix = upSample(mat).transpose();
                break;
            case S_4_1_1:
//                mat = mat.transpose();
                outputMatrix = upSample(upSample(mat));
                break;
        }
        return outputMatrix;

    }

    public static Matrix sampleDown(Matrix mat, SamplingType samplingType) {
        Matrix outputMatrix = new Matrix(mat.getRowDimension(), mat.getRowDimension());
        switch (samplingType) {
            case S_4_4_4:
                outputMatrix = mat;
                break;
            case S_4_2_2:
                outputMatrix = downSample(mat);
                break;
            case S_4_2_0:
                mat = downSample(mat);
                mat=mat.transpose();

                outputMatrix = downSample(mat).transpose();
                break;
            case S_4_1_1:
//                mat = mat.transpose();
                outputMatrix = downSample(downSample(mat));
                break;
        }
        return outputMatrix;
    }

    public static Matrix downSample(Matrix mat) {
        int matWidth = mat.getColumnDimension();
        int matHeight = mat.getRowDimension();
        Matrix newMatrix = new Matrix(matHeight, matWidth / 2);
        for (int i = 0; i < matHeight; i++) {
            for (int j = 0; j < matWidth; j += 2) {
                newMatrix.set(i, j / 2, mat.get(i, j));
            }
        }
        return newMatrix;
    }

    public static Matrix upSample(Matrix mat) {
        int matHeight = mat.getRowDimension();
        int matWidth = mat.getColumnDimension();
        Matrix newMatrix = new Matrix(matHeight,matWidth*2);
        for (int i = 0; i <  matHeight; i++){
            for (int j = 0; j < matWidth; j++){
                newMatrix.set(i, j*2,mat.get(i,j));
                newMatrix.set(i, j*2+1,mat.get(i,j));
            }
        }
        return newMatrix;
    }
}
