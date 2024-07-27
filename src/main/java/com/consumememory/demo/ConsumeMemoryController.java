package com.consumememory.demo;



import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


@RestController
public class ConsumeMemoryController {

    private final List<double[][]> storedMatrices = new ArrayList<>();

    @GetMapping("/consume-memory")
    public String consumeMemory(@RequestParam(defaultValue = "10") int matrixSize ,@RequestParam(defaultValue = "1000") int iterationCount) {
                IntStream.range(1,iterationCount).forEach( i -> {
                double[][] matrix1 = generateRandomMatrix(matrixSize);
                double[][] matrix2 = generateRandomMatrix(matrixSize);

                double[][] result = multiplyMatrices(matrix1, matrix2);

                // Store the result to prevent garbage collection
                storedMatrices.add(result);
        });


        return "Matrix multiplication for size " + matrixSize + " iterationCount " + iterationCount;
    }

    @GetMapping("/clear")
    public String clearMemory(){
        clearStoredMatrices();
        return "cleared";
    }

    private double[][] generateRandomMatrix(int size) {
        Random random = new Random();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }
        return matrix;
    }

    private double[][] multiplyMatrices(double[][] matrix1, double[][] matrix2) {
        int size = matrix1.length;
        double[][] result = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    public void clearStoredMatrices() {
        synchronized(storedMatrices) {
            storedMatrices.clear();
        }
        System.gc(); // Request garbage collection
        System.out.println("Cleared stored matrices. Current memory usage: " +
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) + " MB");
    }

    @GetMapping("/memory-usage")
    public String getMemoryUsage() {
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return "Current memory usage: " + (usedMemory / (1024 * 1024)) + " MB";
    }
}

