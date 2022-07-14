package Algorithms;

import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// also contains greedy redux
public class Greedy extends Algorithm {
    private boolean doRedux;

    public Greedy(Knapsack knapsack, final boolean redux) {
        super(knapsack);
        this.algoName = "Greedy";
        this.doRedux = redux;
        if (this.doRedux) {
            this.algoName += "Redux";
        }
    }

    @Override
    // no need to implement
    public boolean doesSolutionExist() {
        return false;
    }

    @Override
    public void findSolution() {
        this.startCpuTimer();
        this.getKnapsack().setIndexPositions();
        // sort by price/weight value
        this.getKnapsack().sortItemsByPriceWeight();
        List<Boolean> knapsackContent = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));
        int currentCapacity = this.getKnapsack().getCapacity();
        int bestPrice = 0;
        for (int itemIndex = 0 ; itemIndex < this.getKnapsack().getItemsCount() ; itemIndex++) {
            if (currentCapacity >= this.getKnapsack().getItems().get(itemIndex).getWeight()) {
                knapsackContent.set(itemIndex, true);
                currentCapacity -= this.getKnapsack().getItems().get(itemIndex).getWeight();
                bestPrice += this.getKnapsack().getItems().get(itemIndex).getPrice();
            }
        }

        // redux compares solution from greedy algo with one item with the biggest value
        int bestPriceRedux = 0;
        List<Boolean> knapsackContentRedux = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));
        if (this.doRedux) {
            this.getKnapsack().sortItemsByPrice();

            for (int itemIndex = 0 ; itemIndex < this.getKnapsack().getItemsCount() ; itemIndex++) {
                if (this.getKnapsack().getItems().get(itemIndex).getWeight() <= this.getKnapsack().getCapacity()) {
                    bestPriceRedux = this.getKnapsack().getItems().get(itemIndex).getPrice();
                    knapsackContentRedux.set(itemIndex, true);
                    break;
                }
            }
        }

        // get items put into knapsack in original order
        List<Boolean> solutionItemsInOriginalOrder = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));
        for (int i = 0; i < this.getKnapsack().getItemsCount(); i++) {
            if (bestPrice > bestPriceRedux) {
                solutionItemsInOriginalOrder.set(this.getKnapsack().getItems().get(i).getIndexPosition(), knapsackContent.get(i));
            } else {
                solutionItemsInOriginalOrder.set(this.getKnapsack().getItems().get(i).getIndexPosition(), knapsackContentRedux.get(i));
            }
        }

        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(),
                this.getKnapsack().getItemsCount(),
                Math.max(bestPrice, bestPriceRedux),
                solutionItemsInOriginalOrder
        ));
    }
}
