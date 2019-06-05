package cn.icedsoul.cutter.algorithm.markovChain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class MCLClustering {

    private double[][] transitionMatrix;
    private int expansionParam;
    private double inflationParam;
    private static final int PRECISION = 5;
    private static final int ITERATOR_SIZE = 100;

    public MCLClustering( int expansionParm, double inflationParam, double[][] data){
        this.expansionParam = expansionParm;
        this.inflationParam = inflationParam;
        this.transitionMatrix = data;
    }

    public void runMCL() {
        calculateAffinity(transitionMatrix);
//        normalizeMatrix(transitionMatrix);
        addSelfLoops();
        normalizeMatrix(transitionMatrix);

        int iterations = 1;
        while (!checkConvergernce(transitionMatrix) && iterations < ITERATOR_SIZE) {
            System.out.println("Running MCL iteration: " + iterations);
            double[][] expandedMatrix = expandMatrix(transitionMatrix);
            double[][] inflatedMatrix = inflateMatrix(expandedMatrix);
            pruneMatrix(inflatedMatrix);
            transitionMatrix = inflatedMatrix;
            iterations++;
        }
        //printMatrix(transitionMatrix);
        System.out.println("Markov Chain Clustering converged after: " + (iterations - 1) + " iterations");
        System.out.println("Analyzing clusters and generating file...");
        printMatrix(transitionMatrix);
    }

    private void addSelfLoops() {
        for (int i = 0; i < transitionMatrix.length; i++) {
            for (int j = 0; j < transitionMatrix.length; j++) {
                if (i == j) {
                    transitionMatrix[i][j] = 1;
                }
            }
        }
    }

    private void calculateAffinity(double[][] G){
//        for (int j = 0; j < G.length; j++) {
//            double colSum = 0;
//            for (int i = 0; i < G.length; i++) {
//                colSum += G[i][j];
//            }
//            if(colSum > 0){
//                for (int i = 0; i < G.length; i++) {
//                    G[i][j] = G[i][j] / colSum;
//                }
//            }
//        }
        for(int i = 0; i < G.length; i++){
            double sum = 0;
            for(int j = 0; j < G[0].length; j++){
                sum += G[i][j];
            }
            if(sum > 0){
                for(int j = 0; j < G[0].length; j++){
                    G[i][j] = G[i][j] / sum;
                }
            }
        }
    }

    public void normalizeMatrix(double[][] inputMatrix) {
        for (int j = 0; j < inputMatrix.length; j++) {
            double colSum = 0;
            for (int i = 0; i < inputMatrix.length; i++) {
                colSum += inputMatrix[i][j];
            }
            if(colSum > 0){
                for (int i = 0; i < inputMatrix.length; i++) {
                    inputMatrix[i][j] = inputMatrix[i][j] / colSum;
                }
            }
        }
    }

    private boolean checkConvergernce(double[][] inputMatrix) {
//        printMatrix(inputMatrix);
        for (int i = 0; i < inputMatrix.length; i++) {
            for (int j = 0; j < inputMatrix.length; j++) {
                inputMatrix[i][j] = new BigDecimal(inputMatrix[i][j]).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
            }
        }
        int matLen = inputMatrix.length;
        double[][] convergedMatrix = multiplyMatrices(inputMatrix, inputMatrix);
        for (int i = 0; i < convergedMatrix.length; i++) {
            for (int j = 0; j < inputMatrix.length; j++) {
                convergedMatrix[i][j] = new BigDecimal(convergedMatrix[i][j]).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
            }
        }
        return Arrays.deepEquals(inputMatrix, convergedMatrix);
    }

    public double[][] multiplyMatrices(double[][] mat1, double[][] mat2) {
        double[][] newMat = new double[mat1.length][mat1.length];
        int matLen = newMat.length;
        for (int i = 0; i < matLen; i++) {
            for (int j = 0; j < matLen; j++) {
                for (int l = 0; l < matLen; l++) {
                    newMat[i][j] += (mat1[i][l]) * (mat2[l][j]);
                }
            }
        }
        return newMat;
    }

    public double[][] expandMatrix(double[][] inputMatrix) {
        int matLen = inputMatrix.length;
        double[][] expandedMatrix = new double[inputMatrix.length][inputMatrix.length];
        for (int i = 0; i < matLen; i++) {
            for (int j = 0; j < matLen; j++) {
                expandedMatrix[i][j] = inputMatrix[i][j];
            }
        }
        for (int i = 1; i < expansionParam; i++) {
            expandedMatrix = multiplyMatrices(expandedMatrix, inputMatrix);
        }
        return expandedMatrix;
    }

    public double[][] inflateMatrix(double[][] expandedMatrix) {
        int matLen = expandedMatrix.length;
        double[][] inflatedMatrix = new double[matLen][matLen];
        for (int i = 0; i < matLen; i++) {
            for (int j = 0; j < matLen; j++) {
                inflatedMatrix[i][j] = Math.pow(expandedMatrix[i][j], inflationParam);
            }
        }
        normalizeMatrix(inflatedMatrix);
        return inflatedMatrix;
    }

    public void pruneMatrix(double[][] inputMatrix) {
        for (int i = 0; i < inputMatrix.length; i++) {
            for (int j = 0; j < inputMatrix.length; j++) {
                if (inputMatrix[i][j] < Math.pow(10, -PRECISION)) {
                    inputMatrix[i][j] = 0.0;
                }
            }
        }
    }


    public double[][] getTransitionMatrix(){
        return this.transitionMatrix;
    }

    private void printMatrix(double[][] inMatrix) {
        System.out.println("=====input matrix:=======");
        int matLen = inMatrix.length;
        for (int i = 0; i < matLen; i++) {
            for (int j = 0; j < matLen; j++) {
                System.out.print(inMatrix[i][j] + " ");
            }
            System.out.println("\n");
        }
    }
}
