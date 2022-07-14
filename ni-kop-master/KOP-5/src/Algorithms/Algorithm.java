package Algorithms;

import SAT.Formula;
import SAT.Solution;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public abstract class Algorithm {
    // abstract methods
    public abstract void findSolution();

    private final Formula formula;
    private Solution foundSolution = null;
    protected String algoName;
    private double bestWeightMismatch = 0;
    // cpu measurement variables
    private final ThreadMXBean threadMXBean;
    private long cpuTime = 0;
    private long startCpuTime;
    private boolean cpuTimerFinished = false;

    // public methods
    public Algorithm(Formula formula) {
        this.formula = formula;
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.threadMXBean.setThreadCpuTimeEnabled(true);
    }

    public void printCpuTime() {
        if (this.cpuTimerFinished && this.foundSolution != null) {
            // converting time from nano secs to secs
            System.out.println("CPU Time: " + (double)this.cpuTime/1_000_000_000.0 + " secs");
        } else {
            this.printNoSolutionAvailable();
        }
    }

    public void computeWeightDiff() {
        this.bestWeightMismatch = Math.abs(this.foundSolution.getWeight() - this.formula.getCorrectSolutionWeight()) / (double)this.formula.getCorrectSolutionWeight();
    }

    public void computeWeightDiff(final int correctSolution) {
        this.bestWeightMismatch = Math.abs(this.foundSolution.getWeight() - correctSolution) / (double)correctSolution;
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

    protected Formula getFormula() {
        return formula;
    }

    public Solution getFoundSolution() {
        return foundSolution;
    }

    public void setFoundSolution(Solution foundSolution) {
        this.foundSolution = foundSolution;
    }

    // private methods
    private void printNoSolutionAvailable() {
        System.out.println("No solution was found yet. Run '.findSolution()' method first");
    }

    public String getAlgoName() {
        return algoName;
    }

    public double getBestWeightMismatch() {
        return bestWeightMismatch;
    }

    public long getCpuTime() {
        return cpuTime;
    }
}