import Algorithms.Algorithm;
import Algorithms.BruteForce;
import Algorithms.SimulatedAnnealing;
import SAT.Formula;
import SAT.Solution;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class Main {
    private static final String DATA_PATH = System.getProperty("user.dir") + "\\InputData";

    public static void main(String[] args) {
        System.out.println("START");

        // thread bean for measuring time
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // construct data loader with the root folder name where all data sets are stored
        DataLoader loader = new DataLoader(DATA_PATH);

        final String FOLDER_NAME = "M1"; // M1, N1, Q1, R1, A1
        loader.readFilesInFolder(FOLDER_NAME);
//        loader.readFilesFromGenerator(FOLDER_NAME); // used for reading data from generator

        List<Double> errorValues = new ArrayList<>();
//        for (int i = 0 ; i < 200 ; i++) { // loop used for the pilot experiment where I was trying to find optimal values for the parameters
        for (int instanceCount : loader.getInstanceCntToFormulasByIds().keySet()) {
//        for (String instanceFileName : loader.getFileNameToFormulas().keySet()) { // used for reading data from generator
//            if (instanceCount != 78) {
//                continue;
//            }
            final String fileName = FOLDER_NAME + instanceCount;
            long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
            System.out.println("File: " + fileName);
            String algoName = "";
            for (String taskId : loader.getInstanceCntToFormulasByIds().get(instanceCount).keySet()) {
//                if (!taskId.contains("01000")) {
//                    continue;
//                }

//                final int solFoundByBF = getSolutionWeightByBruteForce(loader.getFileNameToFormulas().get(instanceFileName)); // used for reading data from generator
                Algorithm algo = new SimulatedAnnealing(
                        loader.getInstanceCntToFormulasByIds().get(instanceCount).get(taskId)
                );
                algo.findSolution();
                algoName = algo.getAlgoName();

                // solution check
                final boolean solCorrect =
                        algo.getFoundSolution().getWeight() == loader.getInstanceCntToFormulasByIds().get(instanceCount).get(taskId).getCorrectSolutionWeight();
                // compute value diff if sol is not correct
                if (!solCorrect) {
                    algo.computeWeightDiff();
                }
//                if (solFoundByBF != algo.getFoundSolution().getWeight()) {
//                    algo.computeWeightDiff(solFoundByBF);
//                }
                errorValues.add(algo.getBestWeightMismatch());
                DataSaver.createAlgoOutputInfoFile(fileName, algoName, algo.getFoundSolution(), algo.getCpuTime(), algo.getBestWeightMismatch());
            }
            OptionalDouble avg = errorValues
                    .stream()
                    .mapToDouble(a -> a)
                    .average();
            long measuredCpuTime = threadMXBean.getCurrentThreadCpuTime() - startCpuTime;
            DataSaver.appendDataToResultCsv((double)measuredCpuTime/1_000_000_000.0, fileName, algoName, avg.getAsDouble());
            System.out.println("Processing of file '" + fileName + "' took "
                    + (double) measuredCpuTime / 1_000_000_000.0 + " secs");
            errorValues.clear();
        }
//        }
//        OptionalDouble avg = errorValues
//                .stream()
//                .mapToDouble(a -> a)
//                .average();
//        System.out.println("Average best price mismatch in 200 executions of pilot test: " + String.format("%.5f", avg.getAsDouble()));

        System.out.println("END");
    }

    private static int getSolutionWeightByBruteForce(Formula instance) {
        Algorithm algo = new BruteForce(instance);
        algo.findSolution();
        return algo.getFoundSolution().getWeight();
    }
}