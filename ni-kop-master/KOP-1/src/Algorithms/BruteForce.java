package Algorithms;

import KnapsackClasses.Solution;

import java.lang.Math;

public class BruteForce extends Algorithm {

    public BruteForce(KnapsackClasses.Knapsack knapsack) {
        super(knapsack);
        this.algoName = "BruteForce";
    }

    @Override
    public boolean doesSolutionExist() {
        this.startCpuTimer();

        final boolean solutionExists = this.getKnapsack().getMinPrice() == 0
                || doesSolutionExistRecursion(this.getKnapsack().getCapacity(), 0, 0);

        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(), this.getKnapsack().getItemsCount(),
                solutionExists, this.getNodesVisitedCount()
        ));
        return solutionExists;
    }

    @Override
    public void findSolution() {
        this.startCpuTimer();

        this.findSolutionRecursion(this.getKnapsack().getCapacity(), 0);

        this.stopCpuTimer();
//        this.setSolution(null);//todo
    }

    private boolean doesSolutionExistRecursion(int currentCapacity, int currentItemIndex, int currentKnapsackValue) {
        if (currentCapacity < 0 || currentItemIndex == this.getKnapsack().getItemsCount()) {
            this.incrementNodeVisitedCount();
            return false;
        }

        if (currentKnapsackValue >= this.getKnapsack().getMinPrice()
            || currentItemIndex == this.getKnapsack().getItemsCount() - 1
                && this.getKnapsack().getItems().get(currentItemIndex).getWeight() <= currentCapacity
                && currentKnapsackValue + this.getKnapsack().getItems().get(currentItemIndex).getPrice() >= this.getKnapsack().getMinPrice()) {
            this.incrementNodeVisitedCount();
            return true;
        }

        if (this.getKnapsack().getItems().get(currentItemIndex).getWeight() <= currentCapacity) {
            return doesSolutionExistRecursion(
                    currentCapacity - this.getKnapsack().getItems().get(currentItemIndex).getWeight(),
                    currentItemIndex + 1,
                    currentKnapsackValue + this.getKnapsack().getItems().get(currentItemIndex).getPrice())
                    || doesSolutionExistRecursion(
                    currentCapacity,
                    currentItemIndex + 1,
                    currentKnapsackValue);
        }

        return doesSolutionExistRecursion(
                currentCapacity,
                currentItemIndex + 1,
                currentKnapsackValue);

    }

    //todo think of a way how to mark used items
    private int findSolutionRecursion(int currentCapacity, int currentItemIndex) {
        int itemIncluded, itemExcluded;
        if (currentCapacity <= 0) {
            return 0;
        }

        if (currentItemIndex == this.getKnapsack().getItemsCount() - 1) {
            return this.getKnapsack().getItems().get(currentItemIndex).getPrice();
        }

        if (this.getKnapsack().getItems().get(currentItemIndex).getWeight() <= currentCapacity) {
            itemIncluded = findSolutionRecursion(
                    currentCapacity - this.getKnapsack().getItems().get(currentItemIndex).getWeight(),
                    currentItemIndex + 1) + this.getKnapsack().getItems().get(currentItemIndex).getPrice();

            itemExcluded = findSolutionRecursion(
                    currentCapacity,
                    currentItemIndex + 1);

            return Math.max(itemIncluded, itemExcluded);
        }
        return findSolutionRecursion(
                currentCapacity,
                currentItemIndex + 1);

    }
}
