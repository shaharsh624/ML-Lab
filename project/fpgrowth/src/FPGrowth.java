import java.util.*;

public class FPGrowth {
    private final int minSupport;
    private final List<List<String>> frequentPatterns;

    public FPGrowth(int minSupport) {
        this.minSupport = minSupport;
        this.frequentPatterns = new ArrayList<>();
    }

    public List<List<String>> findFrequentPatterns(List<List<String>> transactions) {
        FPTree tree = new FPTree(minSupport);
        tree.buildFPTree(transactions);

        minePatterns(tree, new ArrayList<>());
        return frequentPatterns;
    }

    private void minePatterns(FPTree tree, List<String> prefix) {
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
                frequentPatterns.add(newPattern);

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

    public static void main(String[] args) {
        String fileName = "test2";
        String inputFile = "data/" + fileName + ".csv";
        String outPutFile = "results/" + fileName + ".txt";

        List<List<String>> transactions = FileOps.readCSV(inputFile);

        FPGrowth fpGrowth = new FPGrowth(3);
        List<List<String>> frequentPatterns = fpGrowth.findFrequentPatterns(transactions);

        System.out.printf("Frequent patterns found: %d\n", frequentPatterns.size());
        FileOps.writeCSV(frequentPatterns, outPutFile);
    }
}
