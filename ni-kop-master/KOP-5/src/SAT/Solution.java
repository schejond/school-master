package SAT;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private final String id;
    private int weight;
    private final List<Boolean> certificate; // represents result variable states
    private boolean isValid; // if the formula for this certificate evaluates to true
    private int validClausesCnt = 0;

    public Solution(String id, int weight, List<Boolean> certificate) {
        this.id = id;
        this.weight = weight;
        this.certificate = new ArrayList<>();
        this.certificate.addAll(certificate);
        this.isValid = true;
    }

    public Solution(String id, int weight, List<Boolean> certificate, boolean isValid) {
        this.id = id;
        this.weight = weight;
        this.certificate = certificate;
        this.isValid = isValid;
    }

    public Solution(String id, int weight, List<Boolean> certificate, boolean isValid, int validClausesCnt) {
        this.id = id;
        this.weight = weight;
        this.certificate = certificate;
        this.isValid = isValid;
        this.validClausesCnt = validClausesCnt;
    }

    public Solution(final Solution solution) {
        this.id = solution.getId();
        this.weight = solution.getWeight();
        this.certificate = new ArrayList<>();
        this.certificate.addAll(solution.getCertificate());
        this.isValid = solution.isValid;
        this.validClausesCnt = solution.validClausesCnt;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "id='" + id + '\'' +
                ", weight=" + weight +
                ", certificate=" + certificate +
                ", isValid=" + isValid +
                ", validClausesCnt=" + validClausesCnt +
                '}';
    }

    public String getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Boolean> getCertificate() {
        return certificate;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getValidClausesCnt() {
        return validClausesCnt;
    }

    public void setValidClausesCnt(int validClausesCnt) {
        this.validClausesCnt = validClausesCnt;
    }

    public boolean isBetter(final Solution other) {
        if (this.validClausesCnt == other.getValidClausesCnt()) {
            return this.weight > other.getWeight();
        }
        return this.validClausesCnt > other.getValidClausesCnt();
    }
}
