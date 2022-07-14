package SAT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Clause {
    Map<Integer, Boolean> idOfVariableToIsNegation = new HashMap<>();

    public Clause(List<Integer> variableIds) {
        for (Integer id : variableIds) {
            final boolean isNegation = id < 0;
            idOfVariableToIsNegation.put(Math.abs(id) - 1, isNegation);
        }
    }

    @Override
    public String toString() {
        return "Clause{" +
                "idOfVariableToIsNegation=" + idOfVariableToIsNegation +
                '}';
    }

    public boolean evaluate(List<Boolean> state) {
        for (int varId : idOfVariableToIsNegation.keySet()) {
            // if the variable is set to 1 (true) and the variable is not negated in this clause
            //  or if the variable is set to 0 (false) and the variable is negated in this clause
            if (state.get(varId) != idOfVariableToIsNegation.get(varId)) {
                return true;
            }
        }
        return false;
    }

    public Set<Integer> getVariableIds() {
        return idOfVariableToIsNegation.keySet();
    }
}
