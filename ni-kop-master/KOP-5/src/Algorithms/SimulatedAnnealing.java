package Algorithms;

import SAT.Clause;
import SAT.Formula;
import SAT.Solution;

import java.util.*;

public class SimulatedAnnealing extends Algorithm {
    private final float t_0 = 1500f; // initial temperature
    private final float alpha = 0.96f; // temperature control param
    private final float finalTemperature = 0.1f;

//    List<Double> probabilityOfAcceptingWorseSolution; // used for debugging

    public SimulatedAnnealing(Formula formula) {
        super(formula);
        this.algoName = "SimulatedAnnealing";
    }

    @Override
    public void findSolution() {
        Random random = new Random();
        this.startCpuTimer();

        // initialize starting solution
        List<Boolean> startingSolution = new ArrayList<>();
        int startingSolWeight = 0;
        for (int i = 0 ; i < this.getFormula().getVariablesCount() ; i++) {
            if (random.nextBoolean()) {
                startingSolution.add(true);
                startingSolWeight += this.getFormula().getWeights().get(i);
            } else {
                startingSolution.add(false);
            }
        }
        // check if the solution is valid (formula evaluates to true)
        int validClauses = this.getFormula().evaluate(startingSolution);
        Solution bestSolution = new Solution(
                this.getFormula().getId(),
                startingSolWeight,
                startingSolution,
                validClauses == this.getFormula().getClausesCount(),
                validClauses
        );
        Solution localBestSolution = new Solution(bestSolution);

        int iterationCnt = 0;
        do {
            float t = t_0;
            int bestSolutionUpdateCnt = 0;
            while (t >= finalTemperature && bestSolutionUpdateCnt < this.getFormula().getVariablesCount() * 100) {
                int m = 0;
                int solutionUpdateCnt = 0;
//                probabilityOfAcceptingWorseSolution = new ArrayList<>();

                while (m < 2 * this.getFormula().getVariablesCount() && solutionUpdateCnt < this.getFormula().getVariablesCount()) {
                    Solution neighbour = flipRandomBitToFindNeighbourSolution(localBestSolution, random);

                    if (acceptNeighbour(neighbour, localBestSolution, t)) {
                        localBestSolution = neighbour;
                        solutionUpdateCnt++;

                        if (neighbour.isBetter(bestSolution)) {
                            bestSolution = neighbour;
                            bestSolutionUpdateCnt++;
                        }
                    }
                    m++;
                }
//                OptionalDouble average = probabilityOfAcceptingWorseSolution
//                        .stream()
//                        .mapToDouble(a -> a)
//                        .average();
//                System.out.println("Sance na prijeti horsiho updatu: " + String.format("%.2f", average.getAsDouble()));
//                System.out.println("Probehlo iteraci: " + m + " probehlo updatu: " + solutionUpdateCnt);
//                System.out.println("--------------------------------------");

                // cool down temperature
                t *= alpha;
            }
            iterationCnt++;
        } while (!bestSolution.isValid() && iterationCnt < 3); // advanced technique - temperature restart

        this.setFoundSolution(bestSolution);
        this.stopCpuTimer();
    }

    // returns true if the neighbour is better..if not it checks it can still accept it based on the defined condition
    private boolean acceptNeighbour(final Solution neighbour, final Solution currentState, final float t) {
        // compare satisfied clauses
        if (neighbour.getValidClausesCnt() == currentState.getValidClausesCnt()) {
            if (neighbour.getWeight() >= currentState.getWeight()) {
                return true;
            } else {
                final double pstOfAcceptingWorseSolution = Math.exp((neighbour.getWeight() - currentState.getWeight()) / t);
//                probabilityOfAcceptingWorseSolution.add(pstOfAcceptingWorseSolution);
                return pstOfAcceptingWorseSolution >= Math.random();
            }
        }

        // if the states differ in satisfied clauses count, then decide based on them
        if (neighbour.getValidClausesCnt() >= currentState.getValidClausesCnt()) {
            return true;
        } else {
            final double pstOfAcceptingWorseSolution = Math.exp((neighbour.getValidClausesCnt() - currentState.getValidClausesCnt()) / t);
//            probabilityOfAcceptingWorseSolution.add(pstOfAcceptingWorseSolution);
            return pstOfAcceptingWorseSolution >= Math.random();
        }
    }

    private Solution flipRandomBitToFindNeighbourSolution(final Solution currentSolution, Random random) {
        Solution neighbour = new Solution(currentSolution);
        final int i = random.nextInt(this.getFormula().getVariablesCount());

        // flip the bit on index i
        neighbour.getCertificate().set(i, !neighbour.getCertificate().get(i));

        // adjust weight
        if (neighbour.getCertificate().get(i)) {
            neighbour.setWeight(
                    neighbour.getWeight() + this.getFormula().getWeights().get(i)
            );
        } else { // item was removed -> decrease weight value
            neighbour.setWeight(
                    neighbour.getWeight() - this.getFormula().getWeights().get(i)
            );
        }

        // adjust validity based on the changed variable
        final boolean oldSolutionValid = currentSolution.isValid();
        boolean newSolutionValid = true;
        int validClausesCnt = this.getFormula().getClausesCount();
        for (Clause clause : this.getFormula().getClauses()) {
            if (oldSolutionValid) {
                if (clause.getVariableIds().contains(i) && !clause.evaluate(neighbour.getCertificate())) {
                    newSolutionValid = false;
                    validClausesCnt--;
                }
            } else if (!clause.evaluate(neighbour.getCertificate())) {
                newSolutionValid = false;
                validClausesCnt--;
            }
        }
        neighbour.setValid(newSolutionValid);
        neighbour.setValidClausesCnt(validClausesCnt);

        return neighbour;
    }
}