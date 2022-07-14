package KnapsackClasses;

import java.util.List;

// represents found solution
public class Solution {
    private int id;
    private int itemsCount;
    private int solutionPrice;
    private boolean canBeSolved = false;
    private long nodesVisitedCount = 0;
    List<Boolean> itemsInserted; // values are 0 or 1

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

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", itemsCount=" + itemsCount +
                ", solutionPrice=" + solutionPrice +
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

    public boolean canBeSolved() {
        return canBeSolved;
    }

    public List<Boolean> getItemsInserted() {
        return itemsInserted;
    }

    public void setItemsInserted(List<Boolean> itemsInserted) {
        this.itemsInserted = itemsInserted;
    }
}
