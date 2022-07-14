import Algorithms.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Main {
    private static final String DATA_PATH = System.getProperty("user.dir") + "\\Data";

    public static void main(String[] args) {
        System.out.println("START");

        // thread bean
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // construct data loader with the root folder name where all data sets are stored
        DataLoader loader = new DataLoader(DATA_PATH);

        final String FOLDER_NAME = "ZKC";// NK, ZKC, ZKW
        loader.readFilesInFolder(FOLDER_NAME);
        for (String fileName : loader.getFileNameToKnapsacksById().keySet()) {
            if (fileName.contains("35") || fileName.contains("37") || fileName.contains("40")) { // values: 4,10,15,20,22,25,27,30,32,35,37,40
                continue;
            }
            long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
            System.out.println("File: " + fileName);
            String algoName = "";
            for (int taskId : loader.getFileNameToKnapsacksById().get(fileName).keySet()) {
                Algorithm algo = new BruteForce(
                        loader.getFileNameToKnapsacksById().get(fileName).get(taskId)
//                        , true
                );
                final double epsilon = 0.2; // 0.01, 0.05, 0.1, 0.2
                algo.findSolution();
//                ((DynamicProgramming)algo).PTAS(epsilon);
                algoName = algo.getAlgoName();

                // solution check
                boolean solCorrect =
                        algo.getSolution().getItemsInserted().size() == loader.getFileNameToSolutionsById().get(fileName).get(taskId).getItemsInserted().size()
                        && algo.getSolution().getSolutionPrice() == loader.getFileNameToSolutionsById().get(fileName).get(taskId).getSolutionPrice();
                // compute value diff if sol is not correct
                if (!solCorrect) {
                    algo.computePriceDiff(loader.getFileNameToSolutionsById().get(fileName).get(taskId).getSolutionPrice());
                }
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
                algo.createOutputInfoFile(false, FOLDER_NAME);
            }
            long measuredCpuTime = threadMXBean.getCurrentThreadCpuTime() - startCpuTime;
            FileManipulator.appendData((double)measuredCpuTime/1_000_000_000.0, fileName, algoName);
            System.out.println("Processing of file \'" + fileName + "\' took "
                    + (double)measuredCpuTime/1_000_000_000.0 + " secs");
        }

        System.out.println("DONE");
    }
}
