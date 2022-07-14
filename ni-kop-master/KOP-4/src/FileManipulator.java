import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManipulator {
    private static final String FILE_NAME = "results.csv";

    public static void appendData(final double cpuTime, final String fileName,
                                  final String algoName, final Double avgRelativeError) {
        try {
            File file = new File(FILE_NAME);
            FileWriter csvWriter = null; //"folderName, fileName, methodName, cpuTime, date"
            if (!file.exists()) {
                csvWriter = new FileWriter(FILE_NAME);
                // prepare header row
                csvWriter.append("FileName");
                csvWriter.append(",");
                csvWriter.append("AlgoName");
                csvWriter.append(",");
                csvWriter.append("CpuTime");
                csvWriter.append(",");
                csvWriter.append("AvgRelativeError");
                csvWriter.append(",");
                csvWriter.append("Date");
                csvWriter.append("\n");
            } else {
                csvWriter = new FileWriter(FILE_NAME, true);
            }

            // content
            csvWriter.append(fileName);
            csvWriter.append(",");
            csvWriter.append(algoName);
            csvWriter.append(",");
            csvWriter.append(String.valueOf(cpuTime));
            csvWriter.append(",");
            csvWriter.append(String.valueOf(avgRelativeError));
            csvWriter.append(",");
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final String dayString = df.format(new Date());
            csvWriter.append(dayString);
            csvWriter.append("\n");

            csvWriter.flush();
            csvWriter.close();
        } catch (Exception e) {
            System.out.println("Error while appending data to results.csv file: "
                    + e.getMessage() + " -- " + e.getClass().getName());
        }
    }
}
