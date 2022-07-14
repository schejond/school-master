package Algorithms;

import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;

import java.util.Arrays;
import java.util.List;

public class BranchAndBound extends Algorithm {
    public BranchAndBound(Knapsack knapsack) {
        super(knapsack);
        this.algoName = "BranchAndBound";
    }

    @Override
    public boolean doesSolutionExist() {
        this.startCpuTimer();

        this.getKnapsack().sortItemsByPrice();
        final List<Integer> priceSums = createPriceSumsList();

        final boolean solutionExists = this.getKnapsack().getMinPrice() == 0
                || doesSolutionExistRecursion(priceSums, 0, 0, 0);

        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(), this.getKnapsack().getItemsCount(),
                solutionExists, this.getNodesVisitedCount()
        ));
        return solutionExists;
    }

    @Override
    //todo implement tracking which items were used for the best solution variant
    public void findSolution() {
        this.startCpuTimer();

        this.getKnapsack().sortItemsByPrice();
        final List<Integer> priceSums = createPriceSumsList();

        int bestPrice = findSolutionRecursion(priceSums, 0, 0, 0, 0);

        this.stopCpuTimer();
        this.setSolution(null); //todo
    }

    private List<Integer> createPriceSumsList() {
        List<Integer> priceSums = Arrays.asList(new Integer[this.getKnapsack().getItemsCount()]);

        priceSums.set(
                this.getKnapsack().getItemsCount() - 1,
                this.getKnapsack().getItems().get(this.getKnapsack().getItemsCount() - 1).getPrice()
        );

        for (int i = this.getKnapsack().getItemsCount() - 2; i >= 0; i--) {
            priceSums.set(
                    i,
                    priceSums.get(i + 1) + this.getKnapsack().getItems().get(i).getPrice()
            );
        }

        return priceSums;
    }

    private boolean doesSolutionExistRecursion(final List<Integer> priceSums, int currentPrice, int currentWeight, int itemIndex) {
        if (currentWeight <= this.getKnapsack().getCapacity() && currentPrice >= this.getKnapsack().getMinPrice()) {
            this.incrementNodeVisitedCount();
            return true;
        }

        // don't continue if the knapsack is overweight or if all items were explored
        if (currentWeight > this.getKnapsack().getCapacity()
            || itemIndex == this.getKnapsack().getItemsCount()
                // cut branches that won't be able to fulfill the required minimum
            || currentPrice + priceSums.get(itemIndex) < this.getKnapsack().getMinPrice()) {
            this.incrementNodeVisitedCount();
            return false;
        }

        // solution added || notAdded
        return doesSolutionExistRecursion(priceSums,
                currentPrice + this.getKnapsack().getItems().get(itemIndex).getPrice(),
                currentWeight + this.getKnapsack().getItems().get(itemIndex).getWeight(),
                itemIndex + 1)
                || doesSolutionExistRecursion(priceSums,
                currentPrice, currentWeight, itemIndex + 1);
    }

    private int findSolutionRecursion(final List<Integer> priceSums, int bestPrice,
                                      int currentPrice, int currentWeight, int itemIndex) {
        // don't continue if the knapsack is overweight
        if (currentWeight > this.getKnapsack().getCapacity()) {
            return 0;
        }
        // end if all items were explored
        if (itemIndex == this.getKnapsack().getItemsCount()) {
            return currentPrice;
        }
        // cut branches that won't give better solution
        if (currentPrice + priceSums.get(itemIndex) <= bestPrice) {
            return 0;
        }

        // price if we add item on current position
        final int priceIfAdded = findSolutionRecursion(priceSums, bestPrice,
                currentPrice + this.getKnapsack().getItems().get(itemIndex).getPrice(),
                currentWeight + this.getKnapsack().getItems().get(itemIndex).getWeight(),
                itemIndex + 1);

        // price if we don't add item on current position
        final int priceIfExcluded = findSolutionRecursion(priceSums, bestPrice,
                currentPrice, currentWeight, itemIndex + 1);

        bestPrice = Math.max(bestPrice, priceIfAdded);
        bestPrice = Math.max(bestPrice, priceIfExcluded);
        return bestPrice;
    }
}
