package Algorithms;

import KnapsackClasses.Item;
import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;
import KnapsackClasses.ValueContentPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DynamicProgramming extends Algorithm {

    public DynamicProgramming(Knapsack knapsack) {
        super(knapsack);
        this.algoName = "DP_PD";
    }

    @Override
    // no need to implement
    public boolean doesSolutionExist() {
        return false;
    }

    private ValueContentPair priceDecompositionDP(final Knapsack knapsack) {
        final int sumOfPrices = knapsack.getSumOfPrices();
        int[][] table = new int[knapsack.getItemsCount() + 1][sumOfPrices + 1];

        for (int itemIdx = 0 ; itemIdx <= knapsack.getItemsCount() ; itemIdx++) {
            for (int priceIdx = 1 ; priceIdx <= sumOfPrices ; priceIdx++) {
                if (itemIdx == 0) {
                    table[itemIdx][priceIdx] = Integer.MAX_VALUE;
                    continue;
                }
                if (priceIdx == 1) {
                    table[itemIdx][priceIdx] = priceIdx == knapsack.getItems().get(itemIdx - 1).getPrice() ?
                            knapsack.getItems().get(itemIdx - 1).getWeight() :
                            Integer.MAX_VALUE;
                    continue;
                }

                final int prevWeight = table[itemIdx - 1][priceIdx];
                final int prevPriceIdx = priceIdx - knapsack.getItems().get(itemIdx - 1).getPrice();

                if (prevPriceIdx < 0) {
                    table[itemIdx][priceIdx] = prevWeight;
                } else {
                    if (table[itemIdx - 1][prevPriceIdx] == Integer.MAX_VALUE) {
                        table[itemIdx][priceIdx] = Math.min(table[itemIdx - 1][prevPriceIdx], prevWeight);
                    } else {
                        table[itemIdx][priceIdx] = Math.min(
                                table[itemIdx - 1][prevPriceIdx] + knapsack.getItems().get(itemIdx - 1).getWeight(),
                                prevWeight
                        );
                    }
                }
            }
        }

        // find best price
        int bestPrice = 0;
        int bestPriceIdx = 0;
        for (int priceIdx = sumOfPrices ; priceIdx > 0 ; priceIdx--) {
            bestPriceIdx = priceIdx;
            if (table[knapsack.getItemsCount()][priceIdx] <= knapsack.getCapacity()) {
                bestPrice = priceIdx;
                break;
            }
        }

        List<Boolean> itemsInBag = new ArrayList<>(Collections.nCopies(knapsack.getItemsCount(), false));
        for (int currN = knapsack.getItemsCount() ; currN > 0 && bestPriceIdx > 0 ; currN--) {
            if (table[currN][bestPriceIdx] == table[currN-1][bestPriceIdx]) {
                continue;
            }
            bestPriceIdx -= knapsack.getItems().get(currN - 1).getPrice();
            itemsInBag.set(currN - 1, true);
        }

        return new ValueContentPair(bestPrice, itemsInBag);
    }

    @Override
    public void findSolution() {
        this.startCpuTimer();

        final ValueContentPair solution = this.priceDecompositionDP(this.getKnapsack());

        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(),
                this.getKnapsack().getItemsCount(),
                solution.getValue(),
                solution.getContent()
        ));
    }

    public void PTAS(final double epsilon) {
        this.algoName = "FPTAS_E" + epsilon;
        this.startCpuTimer();

        // copy the original knapsack - exclude large items + modify prices
        List<Item> itemsCopyWithoutLargeItems = new ArrayList<>();
        for (int i = 0 ; i < this.getKnapsack().getItemsCount() ; i++) {
            if (this.getKnapsack().getItems().get(i).getWeight() > this.getKnapsack().getCapacity()) {
                continue;
            }
            Item itemToAdd = new Item(
                    this.getKnapsack().getItems().get(i).getWeight(),
                    this.getKnapsack().getItems().get(i).getPrice()
            );
            itemToAdd.setIndexPosition(i);
            itemsCopyWithoutLargeItems.add(itemToAdd);
        }
        Knapsack ptasKnapsack = new Knapsack(
                this.getKnapsack().getId(),
                itemsCopyWithoutLargeItems.size(),
                this.getKnapsack().getCapacity(),
                itemsCopyWithoutLargeItems
        );

        // if there are no items after removing the large ones -> end
        if (ptasKnapsack.getItemsCount() == 0) {
            this.stopCpuTimer();
            this.setSolution(new Solution(
                    this.getKnapsack().getId(),
                    this.getKnapsack().getItemsCount(),
                    0,
                    new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false))
            ));
            return;
        }

        final int Cm = ptasKnapsack.getItems().stream().max(
                Comparator.comparingInt(Item::getPrice)
        ).get().getPrice();
        final double K = (epsilon * Cm)/ptasKnapsack.getItemsCount();
        ptasKnapsack.getItems().forEach((it) -> {it.setPrice((int)Math.floor((it.getPrice())/K));});
        final ValueContentPair solution = this.priceDecompositionDP(ptasKnapsack);

        // correct item order + obtain price after decoding
        List<Boolean> solutionItemsInOriginalOrder = new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false));
        int knapsackValue = 0;
        for (int i = 0 ; i < ptasKnapsack.getItemsCount() ; i++) {
            if (solution.getContent().get(i)) {
                knapsackValue += this.getKnapsack().getItems().get(ptasKnapsack.getItems().get(i).getIndexPosition()).getPrice();
            }
            solutionItemsInOriginalOrder.set(ptasKnapsack.getItems().get(i).getIndexPosition(), solution.getContent().get(i));
        }

        this.stopCpuTimer();
        this.setSolution(new Solution(
                this.getKnapsack().getId(),
                this.getKnapsack().getItemsCount(),
                knapsackValue,
                solutionItemsInOriginalOrder
        ));
    }
}
