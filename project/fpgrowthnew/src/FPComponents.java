import java.util.*;

class FPNode {
    String item;
    int count;
    FPNode parent;
    Map<String, FPNode> children;
    FPNode link;

    public FPNode(String item, int count) {
        this.item = item;
        this.count = count;
        this.children = new HashMap<>();
        this.link = null;
        this.parent = null;
    }

    public void increment(int count) {
        this.count += count;
    }
}

class FPTree {
    FPNode root;
    Map<String, FPNode> headerTable;
    int minSupport;

    public FPTree(int minSupport) {
        this.root = new FPNode(null, 0);
        this.headerTable = new HashMap<>();
        this.minSupport = minSupport;
    }

    public void buildFPTree(List<List<String>> transactions) {
        Map<String, Integer> itemFrequency = new HashMap<>();

        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                itemFrequency.put(item, itemFrequency.getOrDefault(item, 0) + 1);
            }
        }

        itemFrequency.entrySet().removeIf(entry -> entry.getValue() < minSupport);

        for (List<String> transaction : transactions) {
            List<String> modifiableTransaction = new ArrayList<>(transaction);

            modifiableTransaction.sort((a, b) -> {
                int freqCompare = itemFrequency.getOrDefault(b, 0).compareTo(itemFrequency.getOrDefault(a, 0));
                return freqCompare != 0 ? freqCompare : a.compareTo(b);
            });

            modifiableTransaction.removeIf(item -> !itemFrequency.containsKey(item));

            insertTransaction(modifiableTransaction);
        }
    }

    private void insertTransaction(List<String> transaction) {
        FPNode current = root;
        for (String item : transaction) {
            current = insertNode(current, item);
        }
    }

    private FPNode insertNode(FPNode parent, String item) {
        FPNode node = parent.children.get(item);
        if (node == null) {
            node = new FPNode(item, 1);
            node.parent = parent;
            parent.children.put(item, node);

            if (!headerTable.containsKey(item)) {
                headerTable.put(item, node);
            } else {
                FPNode link = headerTable.get(item);
                while (link.link != null) {
                    link = link.link;
                }
                link.link = node;
            }
        } else {
            node.increment(1);
        }
        return node;
    }
}