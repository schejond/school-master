import Algorithms.Algorithm;
import Algorithms.BranchAndBound;
import Algorithms.BruteForce;

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

        final String FOLDER_NAME = "ZR";// NR, ZR
        loader.readFilesInFolder(FOLDER_NAME);
        for (String fileName : loader.getFileNameToKnapsacksById().keySet()) {
//            if (fileName.contains("30") || fileName.contains("32") || fileName.contains("35") || fileName.contains("37") || fileName.contains("40")) { // values: 4,10,15,20,22,25,27,30,32,35,37,40
//                continue;
//            }
            long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
            System.out.println("File: " + fileName);
            String algoName = "";
            for (int taskId : loader.getFileNameToKnapsacksById().get(fileName).keySet()) {
                Algorithm algo = new BranchAndBound(
                        loader.getFileNameToKnapsacksById().get(fileName).get(taskId)
                );
                algoName = algo.getAlgoName();
                final boolean solutionExists = algo.doesSolutionExist();
                algo.createOutputInfoFile(true, FOLDER_NAME);

                if (loader.getFileNameToSolutionsById().get(fileName).get(Math.abs(taskId)).getSolutionPrice()
                        >= loader.getFileNameToKnapsacksById().get(fileName).get(taskId).getMinPrice()
                    != solutionExists) {
                    System.out.println("Failed to predict good result in file: \'"
                            + fileName + "\' for knapsack id: "
                            + loader.getFileNameToKnapsacksById().get(fileName).get(taskId).getId());
                    System.out.println("Solvable = " + solutionExists);
                    System.out.println("Expected solution: " + loader.getFileNameToSolutionsById().get(fileName).get(Math.abs(taskId)).toString());
                    System.out.println(loader.getFileNameToKnapsacksById().get(fileName).get(taskId).toString());
                    break;
                }
            }
            long measuredCpuTime = threadMXBean.getCurrentThreadCpuTime() - startCpuTime;
            FileManipulator.appendData((double)measuredCpuTime/1_000_000_000.0, fileName, algoName);
            System.out.println("Processing of file \'" + fileName + "\' took "
                    + (double)measuredCpuTime/1_000_000_000.0 + " secs");
        }

        System.out.println();
        System.out.println("DONE");
    }
}
