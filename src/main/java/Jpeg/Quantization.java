package Jpeg;

import Jama.Matrix;

public class Quantization {
    // Kvantizacni matice 8x8 pro jasovou slozku
    private static final double[][] quantizationMatrix8Y = {
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};

    // Kvantizacni matice 8x8 pro chromaticke slozky
    private static final double[][] quantizationMatrix8C = {
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}};

    // Metoda pro kvantizaci
    public static Matrix quantize(Matrix input, int blockSize, double quality, boolean matrixY) {
        Matrix outputMatrix = new Matrix(input.getRowDimension(), input.getRowDimension());
        Matrix quantMatrix = getQuantizationMatrix(blockSize,quality,matrixY);

        for (int row = 0; row < input.getRowDimension(); row += blockSize) {
            for (int col = 0; col < input.getColumnDimension(); col += blockSize) {
                Matrix block = input.getMatrix(row,row + blockSize - 1, col, col + blockSize -1);

                for (int rowBlock = 0; rowBlock < block.getRowDimension(); rowBlock ++) {
                    for (int columnBlock =0; columnBlock < block.getColumnDimension(); columnBlock ++) {
                        double value = block.get(rowBlock, columnBlock) / quantMatrix.get(rowBlock, columnBlock);
                        block.set(rowBlock,columnBlock, value >= -0.2 && value <= 0.2 ? (double) Math.round(value*100)/100 : (double) Math.round(value*10)/10);
                    }
                }
                outputMatrix.setMatrix(row,row + blockSize - 1, col, col + blockSize -1,block);
            }
        }
        return outputMatrix;
    }

    // Metoda pro inverzni kvantizaci
    public static Matrix inverseQuantize(Matrix input, int blockSize, double quality, boolean matrixY) {
        Matrix outputMatrix = new Matrix(input.getRowDimension(), input.getRowDimension());
        Matrix quantMatrix = getQuantizationMatrix(blockSize,quality,matrixY);

        for (int row = 0; row < input.getRowDimension(); row += blockSize) {
            for (int col = 0; col < input.getColumnDimension(); col += blockSize) {
                Matrix block = input.getMatrix(row,row + blockSize - 1, col, col + blockSize -1);

                for (int rowBlock = 0; rowBlock < block.getRowDimension(); rowBlock ++) {
                    for (int columnBlock =0; columnBlock < block.getColumnDimension(); columnBlock ++) {
                        double value = block.get(rowBlock, columnBlock) * quantMatrix.get(rowBlock, columnBlock);
                        block.set(rowBlock,columnBlock,value);
                    }
                }
                outputMatrix.setMatrix(row,row + blockSize - 1, col, col + blockSize -1,block);
            }
        }
        return outputMatrix;
    }

    // Metoda pro ziskani kvantizacni matice
    public static Matrix getQuantizationMatrix(int blockSize, double quality, boolean matrixY) {
        Matrix outputMatrix;
        if(matrixY)
            outputMatrix= new Matrix(quantizationMatrix8Y);
        else
            outputMatrix= new Matrix(quantizationMatrix8C);
        if(blockSize>8){
            for (int i = 8; i <blockSize ; i*=2) {
                outputMatrix= Sampling.upSample(outputMatrix).transpose();
                outputMatrix= Sampling.upSample(outputMatrix).transpose();
            }
        }if(blockSize<8){
            for (int i = 8; i >blockSize ; i/=2) {
                outputMatrix= Sampling.downSample(outputMatrix).transpose();
                outputMatrix= Sampling.downSample(outputMatrix).transpose();
            }
        }
        double alpha;
        if (quality<=50) {
            alpha = 50 / quality;
            outputMatrix=outputMatrix.times(alpha);
        }
        else if (quality<100) {
            alpha=2-((2*quality)/100);
            outputMatrix=outputMatrix.times(alpha);

        }
        else
            outputMatrix= new Matrix(blockSize,blockSize,1);
        return outputMatrix;
    }

}
