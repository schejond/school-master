import SAT.Solution;

import java.io.File;
import java.io.FileWriter;

public class DataSaver {
    private static final String RESULTS_CSV_FILE_NAME = "results.csv";

    public static void createAlgoOutputInfoFile(final String folderName, final String algoName, final Solution solution,
                                                final long cpuTime, final double bestWeightMismatch) {
        final String directoryName = System.getProperty("user.dir") + "\\Output\\" + algoName + "\\";
        final String fileName = directoryName + folderName + ".csv";
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try {
            if (solution == null) {
                System.out.println("Solution is null.");
                return;
            }

            File file = new File(fileName);
            FileWriter csvWriter;
            if (!file.exists()) {
                csvWriter = new FileWriter(fileName);
                // prepare header row
                csvWriter.append("FormulaId");
                csvWriter.append(",");
                csvWriter.append("NumberOfClauses");
                csvWriter.append(",");
                csvWriter.append("CpuTime");
                csvWriter.append(",");
                csvWriter.append("RelativeError");
                csvWriter.append("\n");
            } else {
                csvWriter = new FileWriter(fileName, true);
            }

            // content
            csvWriter.append(String.valueOf(solution.getId()));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(solution.getCertificate().size()));
            csvWriter.append(",");
            csvWriter.append(String.valueOf((double) cpuTime / 1_000_000_000.0));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(bestWeightMismatch));
            csvWriter.append("\n");

            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            System.out.println("Error in creating new file: " + e.getMessage() + " -- " + e.getClass().getName() + ". FileName: " + fileName);
        }
    }

    public static void appendDataToResultCsv(final double cpuTime, final String fileName,
                                  final String algoName, final Double avgRelativeError) {
        try {
            File file = new File(RESULTS_CSV_FILE_NAME);
            FileWriter csvWriter;
            if (!file.exists()) {
                csvWriter = new FileWriter(RESULTS_CSV_FILE_NAME);
                // prepare header row
                csvWriter.append("FileName");
                csvWriter.append(",");
                csvWriter.append("AlgoName");
                csvWriter.append(",");
                csvWriter.append("CpuTime");
                csvWriter.append(",");
                csvWriter.append("AvgRelativeError");
                csvWriter.append("\n");
            } else {
                csvWriter = new FileWriter(RESULTS_CSV_FILE_NAME, true);
            }

            // content
            csvWriter.append(fileName);
            csvWriter.append(",");
            csvWriter.append(algoName);
            csvWriter.append(",");
            csvWriter.append(String.valueOf(cpuTime));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(avgRelativeError));
            csvWriter.append("\n");

            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            System.out.println("Error while appending data to results.csv file: "
                    + e.getMessage() + " -- " + e.getClass().getName());
        }
    }
}
