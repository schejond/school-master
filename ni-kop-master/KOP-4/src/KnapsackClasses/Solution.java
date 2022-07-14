package KnapsackClasses;

import java.util.ArrayList;
import java.util.List;

// represents found solution
public class Solution {
    private int id;
    private int itemsCount;
    private int solutionPrice;
    private int solutionWeight;
    private boolean canBeSolved = false;
    private long nodesVisitedCount = 0;
    List<Boolean> itemsInserted; // values are 0 or 1

    public Solution(final Solution solution) {
        this.id = solution.getId();
        this.itemsCount = solution.getItemsCount();
        this.solutionPrice = solution.getSolutionPrice();
        this.solutionWeight = solution.getSolutionWeight();
        this.canBeSolved = solution.canBeSolved();
        this.nodesVisitedCount = solution.getNodesVisitedCount();
        this.itemsInserted = new ArrayList<>();
        this.itemsInserted.addAll(solution.getItemsInserted());
//        this.itemsInserted = new ArrayList<>(solution.getItemsInserted());
    }

    public Solution(int id, int itemsCount, int solutionPrice, List<Boolean> itemsInserted) {
        this.id = id;
        this.itemsCount = itemsCount;
        this.solutionPrice = solutionPrice;
        this.itemsInserted = itemsInserted;
    }

    public Solution(int id, int itemsCount, boolean canBeSolved) {
        this.id = id;
        this.itemsCount = itemsCount;
        this.canBeSolved = canBeSolved;
    }

    public Solution(int id, int itemsCount, boolean canBeSolved, long nodesVisitedCount) {
        this.id = id;
        this.itemsCount = itemsCount;
        this.canBeSolved = canBeSolved;
        this.nodesVisitedCount = nodesVisitedCount;
    }

    public Solution(int id, int itemsCount, int solutionPrice, int solutionWeight, List<Boolean> itemsInserted) {
        this.id = id;
        this.itemsCount = itemsCount;
        this.solutionPrice = solutionPrice;
        this.solutionWeight = solutionWeight;
        this.itemsInserted = itemsInserted;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", itemsCount=" + itemsCount +
                ", solutionPrice=" + solutionPrice +
                ", solutionWeight=" + solutionWeight +
                ", canBeSolved=" + canBeSolved +
                ", nodesVisitedCount=" + nodesVisitedCount +
                ", itemsInserted=" + itemsInserted +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public int getSolutionPrice() {
        return solutionPrice;
    }

    public void setSolutionPrice(int solutionPrice) {
        this.solutionPrice = solutionPrice;
    }

    public int getSolutionWeight() {
        return solutionWeight;
    }

    public void setSolutionWeight(int solutionWeight) {
        this.solutionWeight = solutionWeight;
    }

    public boolean canBeSolved() {
        return canBeSolved;
    }

    public long getNodesVisitedCount() {
        return nodesVisitedCount;
    }

    public List<Boolean> getItemsInserted() {
        return itemsInserted;
    }

    public void setItemsInserted(List<Boolean> itemsInserted) {
        this.itemsInserted = itemsInserted;
    }

    public boolean isBetter(final Solution other) {
        if (this.solutionPrice > other.getSolutionPrice()) {
            return true;
        } else if (this.solutionPrice < other.getSolutionPrice()) {
            return false;
        } else {
            return this.solutionWeight <= other.getSolutionWeight();
        }
    }
}
