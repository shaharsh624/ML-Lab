import java.util.*;
import java.util.stream.*;

public class FPGrowth {
    private final int minSupport;
    private final Map<String, Integer> patternSupport;

    public FPGrowth(int minSupport) {
        this.minSupport = minSupport;
        this.patternSupport = new HashMap<>();
    }

    public Map<String, Integer> createFrequencyMap(List<List<String>> transactions) {
        Map<String, Integer> itemFrequencies = new HashMap<>();
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                itemFrequencies.merge(item, 1, Integer::sum);
            }
        }

        // Filter with items having frequencies greater than support and sort by values in descending order
        itemFrequencies.entrySet().removeIf(entry -> entry.getValue() < minSupport);

        return itemFrequencies.entrySet().stream().sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<List<String>> createOrderedItemSet(Map<String, Integer> sortedMap, List<List<String>> transactions) {
        Set<String> validItems = sortedMap.keySet();
        List<List<String>> filteredTransactions = new ArrayList<>();
        for (List<String> transaction : transactions) {
            Set<String> filteredTransactionSet = new HashSet<>();
            for (String item : transaction) {
                if (validItems.contains(item)) {
                    filteredTransactionSet.add(item);
                }
            }
            filteredTransactions.add(new ArrayList<>(filteredTransactionSet));
        }
        return filteredTransactions;
    }

    public Map<String, Integer> findFrequentPatterns(List<List<String>> transactions) {
        FPTree tree = new FPTree(minSupport);
        tree.buildFPTree(transactions);

        minePatterns(tree, new ArrayList<>());
        return patternSupport;
    }

    public void minePatterns(FPTree tree, List<String> prefix) {
        if (tree.root.children.isEmpty()) {
            return;
        }

        for (Map.Entry<String, FPNode> entry : tree.headerTable.entrySet()) {
            String item = entry.getKey();
            List<String> newPattern = new ArrayList<>(prefix);
            newPattern.add(item);

            int support = 0;
            FPNode node = entry.getValue();
            while (node != null) {
                support += node.count;
                node = node.link;
            }

            if (support >= minSupport) {
                // Store pattern with its support count
                if (newPattern.size() > 1) { // Only store patterns with 2 or more items
                    List<String> sortedPattern = new ArrayList<>(newPattern);
                    String patternKey = String.join(",", sortedPattern);
                    patternSupport.put(patternKey, support);
                }

                List<List<String>> conditionalPatternBase = new ArrayList<>();
                node = entry.getValue();

                while (node != null) {
                    List<String> path = new ArrayList<>();
                    FPNode current = node.parent;
                    while (current.item != null) {
                        path.addFirst(current.item);
                        current = current.parent;
                    }

                    if (!path.isEmpty()) {
                        for (int i = 0; i < node.count; i++) {
                            conditionalPatternBase.add(new ArrayList<>(path));
                        }
                    }
                    node = node.link;
                }

                if (!conditionalPatternBase.isEmpty()) {
                    FPTree conditionalTree = new FPTree(minSupport);
                    conditionalTree.buildFPTree(conditionalPatternBase);
                    minePatterns(conditionalTree, newPattern);
                }
            }
        }
    }

    public StringBuilder formatOutput(Map<String, Integer> frequentPatterns) {
        StringBuilder output = new StringBuilder();

        List<Map.Entry<String, Integer>> sortedPatterns = new ArrayList<>(frequentPatterns.entrySet());
        sortedPatterns.sort((e1, e2) -> {
            int supportCompare = e2.getValue().compareTo(e1.getValue());
            if (supportCompare != 0) return supportCompare;
            return e1.getKey().compareTo(e2.getKey());
        });

        for (Map.Entry<String, Integer> entry : sortedPatterns) {
            output.append(entry.getKey()).append(" = ").append(entry.getValue());
            output.append("\n");
        }
        return output;
    }
}