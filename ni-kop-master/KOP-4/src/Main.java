import Algorithms.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class Main {
    private static final String DATA_PATH = System.getProperty("user.dir") + "\\InputData";

    public static void main(String[] args) {
        System.out.println("START");

        // thread bean
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // construct data loader with the root folder name where all data sets are stored
        DataLoader loader = new DataLoader(DATA_PATH);

        final String FOLDER_NAME = "ZKC"; // NK and ZKC
        loader.readFilesInFolder(FOLDER_NAME);
        List<Double> errorValues = new ArrayList<>();
//        for (int i = 0 ; i < 200 ; i++) {
            for (String fileName : loader.getFileNameToKnapsacksById().keySet()) {
//                if (!fileName.contains("22")) {
//                    continue;
//                }
                long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
                System.out.println("File: " + fileName);
                String algoName = "";
                for (int taskId : loader.getFileNameToKnapsacksById().get(fileName).keySet()) {
                    Algorithm algo = new SimulatedAnnealing(
                            loader.getFileNameToKnapsacksById().get(fileName).get(taskId)
                    );
                    algo.findSolution();
                    algoName = algo.getAlgoName();

                    // solution check
                    boolean solCorrect =
                            algo.getSolution().getItemsInserted().size() == loader.getFileNameToSolutionsById().get(fileName).get(taskId).getItemsInserted().size()
                                    && algo.getSolution().getSolutionPrice() == loader.getFileNameToSolutionsById().get(fileName).get(taskId).getSolutionPrice();
                    // compute value diff if sol is not correct
                    if (!solCorrect) {
                        algo.computePriceDiff(loader.getFileNameToSolutionsById().get(fileName).get(taskId).getSolutionPrice());
//                        System.out.println("Relative error in found solution best price: " + String.format("%.3f", algo.getBestPriceMismatch()));
//                        System.out.println("Found solution: " + algo.getSolution().getSolutionPrice());
//                        System.out.println("Correct solution: " + loader.getFileNameToSolutionsById().get(fileName).get(taskId).getSolutionPrice());
                    }
                    errorValues.add(algo.getBestPriceMismatch());
//                    break;
//                for (int i = 0 ; i < algo.getSolution().getItemsInserted().size() ; i++) {
//                    if (algo.getSolution().getItemsInserted().get(i) != loader.getFileNameToSolutionsById().get(fileName).get(taskId).getItemsInserted().get(i)) {
//                        solCorrect = false;
//                        break;
//                    }
//                }
//                if (!solCorrect) {
//                    System.out.println("Solution is not correct for id: " + taskId);
//                    System.out.println("Expected: " + loader.getFileNameToSolutionsById().get(fileName).get(taskId));
//                    algo.printFoundSolution();
//                    break;
//                }
                    algo.createOutputInfoFile(false, fileName);
                }
                OptionalDouble avg = errorValues
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                long measuredCpuTime = threadMXBean.getCurrentThreadCpuTime() - startCpuTime;
                FileManipulator.appendData((double)measuredCpuTime/1_000_000_000.0, fileName, algoName, avg.getAsDouble());
                System.out.println("Processing of file \'" + fileName + "\' took "
                        + (double) measuredCpuTime / 1_000_000_000.0 + " secs");
            }
//        }
//        OptionalDouble avg = errorValues
//                .stream()
//                .mapToDouble(a -> a)
//                .average();
//        System.out.println("Average best price mismatch in 200 executions of pilot test: " + String.format("%.3f", avg.getAsDouble()));
        System.out.println("DONE");
    }
}
