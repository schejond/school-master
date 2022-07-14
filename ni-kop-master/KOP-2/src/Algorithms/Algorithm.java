package Algorithms;

import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;

import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;

public abstract class Algorithm {
    // abstract methods
    public abstract boolean doesSolutionExist();
    public abstract void findSolution();

    private final Knapsack knapsack;
    private Solution solution;
    protected String algoName;
    private long nodesVisitedCount = 0;
    private double bestPriceMismatch = 0;
    // cpu measurement variables
    private final ThreadMXBean threadMXBean;
    private long cpuTime = 0;
    private long startCpuTime;
    private boolean cpuTimerFinished = false;

    // public methods
    public Algorithm(Knapsack knapsack) {
        this.knapsack = knapsack;
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.threadMXBean.setThreadCpuTimeEnabled(true);
    }

    public void printCpuTime() {
        if (this.cpuTimerFinished && this.solution != null) {
            // converting time from nano secs to secs
            System.out.println("CPU Time: " + (double)this.cpuTime/1_000_000_000.0 + " secs");
        } else {
            this.printNoSolutionAvailable();
        }
    }

    public void printFoundSolution() {
        if (this.solution != null) {
            System.out.println("Found solution: " + this.solution);
        } else {
            this.printNoSolutionAvailable();
        }
    }

    public void computePriceDiff(final int bestPrice) {
        this.bestPriceMismatch = Math.abs(this.solution.getSolutionPrice() - bestPrice) / (double)bestPrice;
    }

    public void createOutputInfoFile(final boolean justFindSolution, final String folderName) {
        final String fileName = System.getProperty("user.dir") + "\\Output\\" + folderName + "_" + this.algoName + "_" + this.knapsack.getItems().size() +".csv";
        try {
            if (this.solution == null) {
                this.printNoSolutionAvailable();
                return;
            }

            File file = new File(fileName);
            FileWriter csvWriter = null;

            if (!file.exists()) {
                csvWriter = new FileWriter(fileName);
                // prepare header row
                csvWriter.append("KnapsackId");
                csvWriter.append(",");
                csvWriter.append("NumberOfItems");
                csvWriter.append(",");
                csvWriter.append("CpuTime");
                csvWriter.append(",");
                csvWriter.append("NodesVisited");
                csvWriter.append(",");
                csvWriter.append("Error"); // represents diff between bestPrice and foundPrice
                csvWriter.append(",");
                if (justFindSolution) {
                    csvWriter.append("CanBeSolved");
                } else {
                    for (int i = 0 ; i < this.knapsack.getItemsCount() ; i++) {
                        csvWriter.append("Item_").append(String.valueOf(i));
                        if (i < this.knapsack.getItemsCount() - 1) {
                            csvWriter.append(",");
                        }
                    }
                }
                csvWriter.append("\n");
            } else {
                csvWriter = new FileWriter(fileName, true);
            }

            // content
            csvWriter.append(String.valueOf(this.knapsack.getId()));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(this.knapsack.getItemsCount()));
            csvWriter.append(",");
            csvWriter.append(String.valueOf((double)this.cpuTime/1_000_000_000.0));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(this.nodesVisitedCount));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(this.bestPriceMismatch));
            csvWriter.append(",");
            if (justFindSolution) {
                csvWriter.append(String.valueOf(this.solution.canBeSolved() ? 1 : 0));
            } else {
                for (int i = 0 ; i < this.solution.getItemsCount() ; i++) {
                    csvWriter.append(String.valueOf(this.solution.getItemsInserted().get(i)));
                    if (i < this.solution.getItemsCount() - 1) {
                        csvWriter.append(",");
                    }
                }
            }
            csvWriter.append("\n");

            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            System.out.println("Error in creating new file: " + e.getMessage() + " -- " + e.getClass().getName() + ". FileName: " + fileName);
        }
    }

    // protected methods
    protected void startCpuTimer() {
        this.cpuTimerFinished = false;
        this.startCpuTime = threadMXBean.getCurrentThreadCpuTime();
    }

    protected void stopCpuTimer() {
        this.cpuTime = threadMXBean.getCurrentThreadCpuTime() - this.startCpuTime;
        this.cpuTimerFinished = true;
    }

    protected Knapsack getKnapsack() {
        return knapsack;
    }

    protected void setSolution(Solution solution) {
        this.solution = solution;
    }

    protected int countSolutionWeight(final List<Boolean> items) {
        int knapsackWeight = 0;
        for (int i = 0 ; i < this.getKnapsack().getItemsCount() ; i++) {
            if (items.get(i)) {
                knapsackWeight += this.getKnapsack().getItems().get(i).getWeight();
            }
        }
        return knapsackWeight;
    }

    public Solution getSolution() {
        return solution;
    }

    // private methods
    private void printNoSolutionAvailable() {
        System.out.println("No solution was found yet. Run '.findSolution()' method first");
    }

    public String getAlgoName() {
        return algoName;
    }

    public long getNodesVisitedCount() {
        return nodesVisitedCount;
    }

    public void incrementNodeVisitedCount() {
        this.nodesVisitedCount += 1;
    }
}
