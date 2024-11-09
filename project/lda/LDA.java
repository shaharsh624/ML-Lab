import java.io.*;
import java.util.*;
import java.util.stream.*;

public class LDA {
    private int nComponents;
    private double[][] linearDiscriminants;

    public LDA(int nComponents) {
        this.nComponents = nComponents;
    }

    public void fit(double[][] X, int[] y) {
        int nFeatures = X[0].length;
        double[] meanOverall = calculateMean(X);

        double[][] SW = new double[nFeatures][nFeatures];
        double[][] SB = new double[nFeatures][nFeatures];

        Map<Integer, double[]> classMeans = new HashMap<>();
        Map<Integer, Integer> classCounts = new HashMap<>();

        // Calculate within-class scatter (SW) and between-class scatter (SB)
        for (int label : Arrays.stream(y).distinct().toArray()) {
            double[][] Xc = IntStream.range(0, y.length)
                    .filter(i -> y[i] == label)
                    .mapToObj(i -> X[i])
                    .toArray(double[][]::new);

            double[] meanC = calculateMean(Xc);
            classMeans.put(label, meanC);
            classCounts.put(label, Xc.length);

            // Calculate SW
            for (double[] row : Xc) {
                double[] diff = new double[nFeatures];
                for (int i = 0; i < nFeatures; i++) {
                    diff[i] = row[i] - meanC[i];
                }
                double[][] diffMatrix = new double[][] { diff };
                double[][] diffProduct = multiply(transpose(diffMatrix), diffMatrix);
                for (int i = 0; i < nFeatures; i++) {
                    for (int j = 0; j < nFeatures; j++) {
                        SW[i][j] += diffProduct[i][j];
                    }
                }
            }

            // Calculate SB
            double[] meanDiff = new double[nFeatures];
            for (int i = 0; i < nFeatures; i++) {
                meanDiff[i] = meanC[i] - meanOverall[i];
            }
            double[][] meanDiffProduct = multiply(transpose(new double[][] { meanDiff }), new double[][] { meanDiff });
            for (int i = 0; i < nFeatures; i++) {
                for (int j = 0; j < nFeatures; j++) {
                    SB[i][j] += classCounts.get(label) * meanDiffProduct[i][j];
                }
            }
        }

        // Inverse of SW and then multiply by SB
        double[][] SWInv = invertMatrix(SW);
        double[][] A = multiply(SWInv, SB);

        // Calculate eigenvalues and eigenvectors
        double[][] eigenvectors = calculateEigenvectors(A);

        // Store the top n eigenvectors
        this.linearDiscriminants = Arrays.copyOfRange(eigenvectors, 0, nComponents);
    }

    public double[][] transform(double[][] X) {
        return Arrays.stream(X)
                .map(row -> {
                    double[] projectedRow = new double[nComponents];
                    for (int i = 0; i < nComponents; i++) {
                        projectedRow[i] = dotProduct(row, linearDiscriminants[i]);
                    }
                    return projectedRow;
                })
                .toArray(double[][]::new);
    }

    private double[] calculateMean(double[][] X) {
        int n = X[0].length;
        double[] mean = new double[n];
        for (double[] row : X) {
            for (int i = 0; i < n; i++) {
                mean[i] += row[i];
            }
        }
        for (int i = 0; i < n; i++) {
            mean[i] /= X.length;
        }
        return mean;
    }

    private double dotProduct(double[] v1, double[] v2) {
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    private double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    private double[][] multiply(double[][] matrix1, double[][] matrix2) {
        int rows = matrix1.length;
        int cols = matrix2[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < matrix1[0].length; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }

    private double[][] invertMatrix(double[][] matrix) {
        double determinant = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        return new double[][] {
                { matrix[1][1] / determinant, -matrix[0][1] / determinant },
                { -matrix[1][0] / determinant, matrix[0][0] / determinant }
        };
    }

    private double[][] calculateEigenvectors(double[][] matrix) {
        // Placeholder for eigenvalue/eigenvector computation
        // This can be done using third-party libraries or custom algorithms
        return new double[][] {
                { matrix[0][0], matrix[1][1] }, // Eigenvectors
                { matrix[0][1], matrix[1][0] }
        };
    }

    public static double[][] readCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine(); // Skip the header line

        int rowCount = 0;
        while (br.readLine() != null)
            rowCount++;
        br.close();

        double[][] data = new double[rowCount][4];
        br = new BufferedReader(new FileReader(filename));
        br.readLine(); // Skip header again

        int row = 0;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            for (int col = 1; col <= 4; col++) {
                data[row][col - 1] = Double.parseDouble(values[col]);
            }
            row++;
        }
        br.close();
        return data;
    }

    public static int[] readLabels(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        br.readLine(); // Skip the header line

        int rowCount = 0;
        while (br.readLine() != null)
            rowCount++;
        br.close();

        int[] labels = new int[rowCount];
        br = new BufferedReader(new FileReader(filename));
        br.readLine(); // Skip header again

        int row = 0;
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            labels[row] = labelToInt(values[values.length - 1]);
            row++;
        }
        br.close();
        return labels;
    }

    public static int labelToInt(String label) {
        switch (label) {
            case "Iris-setosa":
                return 0;
            case "Iris-versicolor":
                return 1;
            case "Iris-virginica":
                return 2;
            default:
                throw new IllegalArgumentException("Unknown label: " + label);
        }
    }

    public static void main(String[] args) {
        try {
            double[][] X = readCSV("iris_dataset.csv");
            int[] y = readLabels("iris_dataset.csv");

            LDA lda = new LDA(2);
            lda.fit(X, y);
            double[][] XProjected = lda.transform(X);

            System.out.println("Shape of transformed X: " + XProjected.length + "x" + XProjected[0].length);
            for (double[] row : XProjected) {
                System.out.println(Arrays.toString(row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
