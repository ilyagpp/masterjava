package ru.javaops.masterjava.matrix;

import java.util.*;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        class ColumnResult {
            private final int col;
            private final int[] columnC;

            public ColumnResult(int col, int[] columnC) {
                this.col = col;
                this.columnC = columnC;
            }
        }


        final CompletionService<ColumnResult> completionService = new ExecutorCompletionService<>(executor);

        for(int j =0 ; j < matrixSize; j++){
            int[] ColB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                ColB[k] = matrixB[k][j];
            }
            int finalJ = j;
            completionService.submit(() -> {
                int[] ColC = new int[matrixSize];
                for (int rowA = 0; rowA < matrixSize; rowA++) {
                    int[] thisRow = matrixA[rowA];
                    int sum = 0;
                    for (int colA = 0; colA < matrixSize; colA++) {
                        sum += thisRow[colA] * ColB[colA];
                    }
                    ColC[rowA] = sum;
                }
                return new ColumnResult(finalJ, ColC);
            });
        }

        for (int j = 0; j < matrixSize; j++){
                ColumnResult columnResult = completionService.take().get();
            for (int k = 0; k < matrixSize; k++){
                matrixC[k][columnResult.col] = columnResult.columnC[k];
            }

        }
        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int[] BColumns = new int[matrixSize];
        for (int bCol = 0; bCol < matrixSize; bCol++) {
            for (int colT = 0; colT < matrixSize; colT++) {
                BColumns[colT] = matrixB[colT][bCol];
            }
            for (int rowA = 0; rowA < matrixSize; rowA++) {
                int[] thisRow = matrixA[rowA];
                int sum = 0;
                for (int colA = 0; colA < matrixSize; colA++) {
                    sum += thisRow[colA] * BColumns[colA];
                }
                matrixC[rowA][bCol] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
