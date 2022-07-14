package Algorithms;

import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;
import KnapsackClasses.ValueContentPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public void findSolution() {
        this.startCpuTimer();

        this.getKnapsack().setIndexPositions();
        this.getKnapsack().sortItemsByPrice();
        final List<Integer> priceSums = createPriceSumsList();

        List<Boolean> knapsackContent = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));
        final ValueContentPair solution = findSolutionRecursion(priceSums, 0, 0, 0, 0, 0, knapsackContent);
        // correct item order
        List<Boolean> solutionItemsInOriginalOrder = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));
        for (int i = 0 ; i < this.getKnapsack().getItemsCount() ; i++) {
            solutionItemsInOriginalOrder.set(this.getKnapsack().getItems().get(i).getIndexPosition(), solution.getContent().get(i));
        }
        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(),
                this.getKnapsack().getItemsCount(),
                solution.getValue(),
                solutionItemsInOriginalOrder
        ));
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

    private ValueContentPair findSolutionRecursion(final List<Integer> priceSums, int bestPrice, int bestWeight,
                                                   int currentPrice, int currentWeight,
                                                   int itemIndex, List<Boolean> bestKnapsackContent) {
        // don't continue if the knapsack is overweight
        if (currentWeight > this.getKnapsack().getCapacity()) {
            return new ValueContentPair(0, bestKnapsackContent);
        }
        // end if all items were explored
        if (itemIndex == this.getKnapsack().getItemsCount()) {
            this.incrementNodeVisitedCount();
            return new ValueContentPair(currentPrice, bestKnapsackContent);
        }
        // cut branches that won't give better solution
        if (currentPrice + priceSums.get(itemIndex) < bestPrice) {
            this.incrementNodeVisitedCount();
            return new ValueContentPair(0, bestKnapsackContent);
        }

        List<Boolean> knapsackContentIfIncluded = new ArrayList<>(bestKnapsackContent);
        knapsackContentIfIncluded.set(itemIndex, true);
        List<Boolean> knapsackContentIfExcluded = new ArrayList<>(bestKnapsackContent);

        if (this.getKnapsack().getItems().get(itemIndex).getWeight() + currentWeight
                <= this.getKnapsack().getCapacity()) {
            boolean bestPriceUpdated = false;
            // add item on current position
            final ValueContentPair itemIncluded = findSolutionRecursion(priceSums, bestPrice, bestWeight,
                    currentPrice + this.getKnapsack().getItems().get(itemIndex).getPrice(),
                    currentWeight + this.getKnapsack().getItems().get(itemIndex).getWeight(),
                    itemIndex + 1, knapsackContentIfIncluded);

            if (itemIncluded.getValue() > bestPrice) {
                bestPriceUpdated = true;
                bestPrice = itemIncluded.getValue();
                bestWeight = this.countSolutionWeight(itemIncluded.getContent());
                bestKnapsackContent = itemIncluded.getContent();
            } else if (itemIncluded.getValue() == bestPrice) {
                final int weightIfIncluded = this.countSolutionWeight(itemIncluded.getContent());
                if (weightIfIncluded <= bestWeight) {
                    bestPriceUpdated = true;
                    bestPrice = itemIncluded.getValue();
                    bestWeight = weightIfIncluded;
                    bestKnapsackContent = itemIncluded.getContent();
                }
            }

            // don't add item on current position
            final ValueContentPair itemExcluded = findSolutionRecursion(priceSums, bestPrice, bestWeight,
                    currentPrice, currentWeight, itemIndex + 1, knapsackContentIfExcluded);

            if (itemExcluded.getValue() > bestPrice) {
                bestPriceUpdated = true;
                bestPrice = itemExcluded.getValue();
//                bestWeight = this.countSolutionWeight(itemExcluded.getContent());
                bestKnapsackContent = itemExcluded.getContent();
            } else if (itemExcluded.getValue() == bestPrice) {
                final int weightIfExcluded = this.countSolutionWeight(itemExcluded.getContent());
                if (weightIfExcluded < bestWeight) {
                    bestPriceUpdated = true;
                    bestPrice = itemExcluded.getValue();
//                    bestWeight = weightIfExcluded;
                    bestKnapsackContent = itemExcluded.getContent();
                }
            }

            return new ValueContentPair(bestPriceUpdated ? bestPrice : 0, bestKnapsackContent);
        }

        return findSolutionRecursion(
                priceSums,
                bestPrice,
                bestWeight,
                currentPrice,
                currentWeight,
                itemIndex + 1,
                bestKnapsackContent
        );
    }
}
