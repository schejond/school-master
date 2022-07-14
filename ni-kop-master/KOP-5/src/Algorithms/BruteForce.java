package Algorithms;

import SAT.Formula;
import SAT.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteForce extends Algorithm {

    public BruteForce(Formula formula) {
        super(formula);
        this.algoName = "BruteForce";
    }

    @Override
    public void findSolution() {
        this.startCpuTimer();
        Solution bestSolution = null;

        // iterate through all 2^n possible solutions
        for (long i = 0 ; i < Math.pow(2, this.getFormula().getVariablesCount()) ; i++) {
            List<Boolean> state = new ArrayList<>(Collections.nCopies(this.getFormula().getVariablesCount(), false));

            // generate a state based on "row" and "column"
            for (int j = 0 ; j < this.getFormula().getVariablesCount() ; j++) {
                boolean value = (i >> j & 0b1) == 0b1;
                state.set(j, value);
            }

            // if the formula is evaluated as true, calculate variables weight
            if (this.getFormula().evaluate(state) == this.getFormula().getClausesCount()) {
                int weight = 0;
                for (int variableIndex = 0 ; variableIndex < state.size() ; variableIndex++) {
                    if (state.get(variableIndex)) {
                        weight += this.getFormula().getWeights().get(variableIndex);
                    }
                }
                if (bestSolution == null || bestSolution.getWeight() < weight) {
                    bestSolution = new Solution(this.getFormula().getId(), weight, state);
                }
            }
        }

        // return the best solution (more of them may exist, but we are ok with any of them)
        this.stopCpuTimer();
        this.setFoundSolution(bestSolution);
    }
}
