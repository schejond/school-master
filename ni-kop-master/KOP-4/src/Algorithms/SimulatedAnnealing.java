package Algorithms;

import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;

import java.util.*;

public class SimulatedAnnealing extends Algorithm {
    private final float t_0 = 5300f; // initial temperature
    private final float alpha = 0.9998f; // temperature control param
    private final float finalTemperature = 1000f;

    public SimulatedAnnealing(Knapsack knapsack) {
        super(knapsack);
        this.algoName = "SimulatedAnnealing";
    }

    @Override
    // no need to implement
    public boolean doesSolutionExist() {
        return false;
    }

    @Override
    public void findSolution() {
        Random random = new Random();
        this.startCpuTimer();
        List<Boolean> startingSolution = new ArrayList<>();
        final int[] startingSolPrice = {0};
        final int[] startingSolWeight = {0};
        this.getKnapsack().getItems().forEach(el -> {
            if (
//                    startingSolWeight[0] <= this.getKnapsack().getCapacity() &&
                            random.nextBoolean()) {
                startingSolution.add(true);
                startingSolPrice[0] += el.getPrice();
                startingSolWeight[0] += el.getWeight();
            } else {
                startingSolution.add(false);
            }
        });
        Solution bestSolution = new Solution(
                this.getKnapsack().getId(),
                this.getKnapsack().getItemsCount(),
                startingSolPrice[0],
                startingSolWeight[0],
                startingSolution
        );
        float t = t_0;
        Solution localBestSolution = new Solution(bestSolution);

        int bestSolutionUpdateCnt = 0;
        while(t >= finalTemperature && bestSolutionUpdateCnt < this.getKnapsack().getItemsCount()*100) {
            int m = 0;
            int solutionUpdateCnt = 0;
//            List<Double> probabilityOfAcceptingWorseSolution = new ArrayList<>();
            while (m < 2 * this.getKnapsack().getItemsCount() && solutionUpdateCnt < this.getKnapsack().getItemsCount()) {
                Solution neighbour = new Solution(localBestSolution);
                final int i = random.nextInt(this.getKnapsack().getItemsCount());
                // flip bit on index i
                neighbour.getItemsInserted().set(i, !neighbour.getItemsInserted().get(i));
                // if the item was added, increase price and weight of neighbour
                if (neighbour.getItemsInserted().get(i)) {
                    neighbour.setSolutionPrice(
                            neighbour.getSolutionPrice() + this.getKnapsack().getItems().get(i).getPrice()
                    );
                    neighbour.setSolutionWeight(
                            neighbour.getSolutionWeight() + this.getKnapsack().getItems().get(i).getWeight()
                    );
                } else { // item was removed -> decrease price and weight
                    neighbour.setSolutionPrice(
                            neighbour.getSolutionPrice() - this.getKnapsack().getItems().get(i).getPrice()
                    );
                    neighbour.setSolutionWeight(
                            neighbour.getSolutionWeight() - this.getKnapsack().getItems().get(i).getWeight()
                    );
                }

                // compare fitness value of new neighbour solution
                final int delta = getSolutionFitness(neighbour) - getSolutionFitness(localBestSolution);
//                probabilityOfAcceptingWorseSolution.add(Math.exp(delta/t)/2);
                if (delta > 0) {
                    localBestSolution = neighbour;
                    solutionUpdateCnt++;
                    if (!isSolutionValid(bestSolution) && isSolutionValid(localBestSolution)
                            || isSolutionValid(localBestSolution) && localBestSolution.isBetter(bestSolution)) {
                        bestSolution = localBestSolution;
                        bestSolutionUpdateCnt++;
                    }
                } else if (random.nextFloat() < Math.exp(delta/t)/2) { // use Math.random() for double or random.nextFloat() for float
                    localBestSolution = neighbour;
                    solutionUpdateCnt++;
                }
                m++;
            }
//            OptionalDouble average = probabilityOfAcceptingWorseSolution
//                    .stream()
//                    .mapToDouble(a -> a)
//                    .average();
//            System.out.println("Sance na prijeti horsiho updatu: " + String.format("%.2f", average.getAsDouble()));
//            System.out.println("Probehlo iteraci: " + m + " probehlo updatu: " + solutionUpdateCnt);
//            System.out.println("--------------------------------------");

            // cool down temperature
            t *= alpha;
        }

        // check that found solution si valid
        if (isSolutionValid(bestSolution)) {
            this.setSolution(bestSolution);
        } else {
            this.setSolution(new Solution(
                    this.getKnapsack().getId(),
                    this.getKnapsack().getItemsCount(),
                    0,
                    new ArrayList<>(Collections.nCopies(this.getKnapsack().getItemsCount(), false))
            ));
        }
        this.stopCpuTimer();
    }

    private int getSolutionFitness(final Solution solution) {
        return solution.getSolutionPrice();
    }

    private Boolean isSolutionValid(final Solution solution) {
        return solution.getSolutionWeight() <= this.getKnapsack().getCapacity();
    }
}
