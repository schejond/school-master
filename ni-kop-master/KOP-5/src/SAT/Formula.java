package SAT;

import java.util.List;

public class Formula {
    private final String id;
    private final int variablesCount;
    private final int clausesCount;
    private final List<Clause> clauses;
    private final List<Integer> weights;

    private List<Boolean> correctSolution = null;
    private int correctSolutionWeight = 0;

    public Formula(String id, int variablesCount, int clausesCount, List<Clause> clauses, List<Integer> weights) {
        this.id = id;
        this.variablesCount = variablesCount;
        this.clausesCount = clausesCount;
        this.clauses = clauses;
        this.weights = weights;
    }

    // returns number of valid clauses
    public int evaluate(final List<Boolean> solutionState) {
        int validClausesCnt = 0;
        for (Clause clause : clauses) {
            if (clause.evaluate(solutionState)) {
                validClausesCnt++;
            }
        }
        return validClausesCnt;
    }

    @Override
    public String toString() {
        return "Formula{" +
                "id='" + id + '\'' +
                ", variablesCount=" + variablesCount +
                ", clausesCount=" + clausesCount +
//                ", clauses=" + clauses +
                ", weights=" + weights +
//                ", correctSolution=" + correctSolution +
                ", correctSolutionWeight=" + correctSolutionWeight +
                '}';
    }

    public String getId() {
        return id;
    }

    public int getVariablesCount() {
        return variablesCount;
    }

    public int getClausesCount() {
        return clausesCount;
    }

    public List<Clause> getClauses() {
        return clauses;
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public List<Boolean> getCorrectSolution() {
        return correctSolution;
    }

    public void setCorrectSolution(List<Boolean> correctSolution) {
        this.correctSolution = correctSolution;
    }

    public int getCorrectSolutionWeight() {
        return correctSolutionWeight;
    }

    public void setCorrectSolutionWeight(int correctSolutionWeight) {
        this.correctSolutionWeight = correctSolutionWeight;
    }
}
