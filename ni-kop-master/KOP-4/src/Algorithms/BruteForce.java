package Algorithms;

import KnapsackClasses.Solution;
import KnapsackClasses.ValueContentPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        List<Boolean> knapsackContent = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));

        final ValueContentPair solution = this.findSolutionRecursion(this.getKnapsack().getCapacity(), 0, knapsackContent);

        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(),
                this.getKnapsack().getItemsCount(),
                solution.getValue(),
                solution.getContent()
        ));
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

    private ValueContentPair findSolutionRecursion(int currentCapacity, int currentItemIndex, List<Boolean> knapsackContent) {
        ValueContentPair itemIncluded, itemExcluded;
        if (currentCapacity <= 0) {
            this.incrementNodeVisitedCount();
            return new ValueContentPair(0, knapsackContent);
        }

        if (currentItemIndex == this.getKnapsack().getItemsCount() - 1) {
            List<Boolean> knapsackContentIfIncluded = new ArrayList<>(knapsackContent);
            knapsackContentIfIncluded.set(currentItemIndex, true);
            this.incrementNodeVisitedCount();
            return currentCapacity - this.getKnapsack().getItems().get(currentItemIndex).getWeight() < 0 ?
                    new ValueContentPair(0, knapsackContent)
                    :
                    new ValueContentPair(this.getKnapsack().getItems().get(currentItemIndex).getPrice(), knapsackContentIfIncluded);
        }

        if (this.getKnapsack().getItems().get(currentItemIndex).getWeight() <= currentCapacity) {
            List<Boolean> knapsackContentIfIncluded = new ArrayList<>(knapsackContent);
            knapsackContentIfIncluded.set(currentItemIndex, true);
            List<Boolean> knapsackContentIfExcluded = new ArrayList<>(knapsackContent);

            itemIncluded = findSolutionRecursion(
                    currentCapacity - this.getKnapsack().getItems().get(currentItemIndex).getWeight(),
                    currentItemIndex + 1,
                    knapsackContentIfIncluded
            );
            itemIncluded.addValue(this.getKnapsack().getItems().get(currentItemIndex).getPrice());

            itemExcluded = findSolutionRecursion(
                    currentCapacity,
                    currentItemIndex + 1,
                    knapsackContentIfExcluded);

            if (itemIncluded.getValue() > itemExcluded.getValue()) {
                return itemIncluded;
            } else if (itemIncluded.getValue() == itemExcluded.getValue()) {
                final int weightIfIncluded = this.countSolutionWeight(itemIncluded.getContent());
                final int weightIfExcluded = this.countSolutionWeight(itemExcluded.getContent());
                if (weightIfIncluded <= weightIfExcluded) {
                    return itemIncluded;
                }
            }
            return itemExcluded;
        }
        return findSolutionRecursion(
                currentCapacity,
                currentItemIndex + 1,
                knapsackContent);
    }
}
