import java.io.*;
import java.util.*;

public class FileOps {
    public static List<List<String>> readCSV(String filePath) {
        List<List<String>> transactions = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header row
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    List<String> productList = Arrays.asList(parts[1].split("-"));
                    transactions.add(productList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public static void writeToFile(String frequentPatterns, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(frequentPatterns);

            System.out.println("Output written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
