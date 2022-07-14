import Algorithms.*;
import KnapsackClasses.Solution;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String DATA_PATH = System.getProperty("user.dir") + "\\InputData";

    public static void main(String[] args) {
        System.out.println("START");

        // thread bean
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // construct data loader with the root folder name where all data sets are stored
        DataLoader loader = new DataLoader(DATA_PATH);

        final String FOLDER_NAME = "PilotTests";
        loader.readFilesInFolder(FOLDER_NAME);
        for (String fileName : loader.getFileNameToKnapsacksById().keySet()) {
            long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
            System.out.println("File: " + fileName);
            String algoName = "";
            Map<Integer, Solution> foundSolutions = new HashMap<>();
            for (int taskId : loader.getFileNameToKnapsacksById().get(fileName).keySet()) {
                Algorithm algo = new BranchAndBound(loader.getFileNameToKnapsacksById().get(fileName).get(taskId));
                algo.findSolution();
//                algoName = algo.getAlgoName();

                foundSolutions.put(taskId, algo.getSolution());
//                algo.createOutputInfoFile(false, fileName);
            }

            // now compare found solutions with solutions found by heuristic algorithms
            startCpuTime = threadMXBean.getCurrentThreadCpuTime(); // reset timer
            for (int taskId : loader.getFileNameToKnapsacksById().get(fileName).keySet()) {
                Algorithm greedyAlgo = new Greedy(
                        loader.getFileNameToKnapsacksById().get(fileName).get(taskId)
                        , false
                );
                greedyAlgo.findSolution();
                algoName = greedyAlgo.getAlgoName();
                // compute bestPrice mismatch
                greedyAlgo.computePriceDiff(foundSolutions.get(taskId).getSolutionPrice());
                greedyAlgo.createOutputInfoFile(false, fileName);
            }

            long measuredCpuTime = threadMXBean.getCurrentThreadCpuTime() - startCpuTime;
            FileManipulator.appendData((double)measuredCpuTime/1_000_000_000.0, fileName, algoName);
            System.out.println("Processing of file \'" + fileName + "\' took "
                    + (double)measuredCpuTime/1_000_000_000.0 + " secs");
        }

        System.out.println("DONE");
    }
}
