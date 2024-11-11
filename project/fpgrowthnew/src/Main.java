import java.util.*;

public class Main {
    public static void main(String[] args) {
        String fileName = "orders";
        String inputFile = "data/" + fileName + ".csv";
        String outPutFile = "results/" + fileName + ".txt";
        int minSupport = 5;
        FPGrowth fpGrowth = new FPGrowth(minSupport);

        List<List<String>> transactions = FileOps.readCSV(inputFile);

        // 1. Create Frequency Map
        Map<String, Integer> sortedMap = new HashMap<>(fpGrowth.createFrequencyMap(transactions));

        // 2. Create Ordered-Item Set
        List<List<String>> filteredTransactions = new ArrayList<>(fpGrowth.createOrderedItemSet(sortedMap, transactions));

        // 3. Create Frequent Pattern Tree
        Map<String, Integer> frequentPatterns = fpGrowth.findFrequentPatterns(filteredTransactions);

        String output = "Transactions: \n" + transactions + "\n" +
                "\nFrequency Map: \n" + sortedMap + "\n" +
                "\nOrdered-Item Set: \n" + filteredTransactions + "\n" +
                "\nFrequent Patterns:\n" + fpGrowth.formatOutput(frequentPatterns);
        FileOps.writeToFile(output, outPutFile);
    }
}

