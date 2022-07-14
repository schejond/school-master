import SAT.Clause;
import SAT.Formula;
import SAT.Solution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// class which loads data from files
public class DataLoader {
    private final String dataFolder;
    private final Map<Integer, Map<String, Formula>> instanceCntToFormulasByIds = new HashMap<>();
//    private final Map<String, Formula> fileNameToFormulas = new HashMap<>(); // used for my own generated data

    public DataLoader(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void readFilesInFolder(String folderName) {
        // explore solution files
        final String directoryToLoad = dataFolder + "\\" + folderName;
        File dir = new File(directoryToLoad);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                if (file.isFile()) {
                    List<Integer> instanceParams = parseAllIntsFromLine(file.getName());
                    if (instanceParams.size() != 2) {
                        System.out.println("SAT.Solution file " + file.getName() + " has unexpected format! int params cnt");
                        return;
                    }
                    Map<String, Solution> idToSolution = parseSolutions(file);

                    // for the found solutions open relevant folder and collect relevant instances
                    File relevantFolder = Arrays.stream(directoryListing).filter(f ->
                            f.getName().contains(String.valueOf(instanceParams.get(0)))
                                    && f.getName().contains(String.valueOf(instanceParams.get(1)))
                                    && f.isDirectory()
                    ).findFirst().orElse(null);

                    File[] instances = relevantFolder != null ? relevantFolder.listFiles() : null;
                    if (instances != null) {
                        for (File instance : instances) {
                            if (idToSolution.containsKey(instance.getName().substring(6, instance.getName().length() - 6))) {
                                Formula f = parseInstance(instance);
                                f.setCorrectSolution(idToSolution.get(f.getId()).getCertificate());
                                f.setCorrectSolutionWeight(idToSolution.get(f.getId()).getWeight());
                                if (!instanceCntToFormulasByIds.containsKey(f.getClausesCount())) {
                                    instanceCntToFormulasByIds.put(f.getClausesCount(), new HashMap<>());
                                }
                                instanceCntToFormulasByIds.get(f.getClausesCount()).put(f.getId(), f);
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("The directory: " + directoryToLoad + " was not found.");
        }
    }

//    // used for my own generated data
//    public void readFilesFromGenerator(String folderName) {
//        // explore solution files
//        final String directoryToLoad = dataFolder + "\\" + folderName;
//        File dir = new File(directoryToLoad);
//        File[] directoryListing = dir.listFiles();
//        if (directoryListing != null) {
//            for (File instance : directoryListing) {
//                Formula f = parseInstance(instance);
//                fileNameToFormulas.put(instance.getName(), f);
//            }
//        }
//    }

    private Map<String, Solution> parseSolutions(final File file) {
        Map<String, Solution> idToSolution = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                String id;
                int weight;
                List<Boolean> solutions = new ArrayList<>();

                sb.append(line);
                String[] splitLine = sb.toString().split(" +");
                id = splitLine[0].substring(5);
                weight = Integer.parseInt(splitLine[1]);

                for (int i = 2 ; i < splitLine.length - 1 ; i++) {
                    solutions.add(Integer.parseInt(splitLine[i]) >= 0);
                }
                idToSolution.put(id, new Solution(id, weight, solutions, true));

                sb.setLength(0);
                line = br.readLine();
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found. Sol");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return idToSolution;
    }

    private boolean isNumeric(final String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    // parses all ints from line even numbers within mixed text
    private List<Integer> parseAllIntsFromLine(final String line) {
        String str = line.replaceAll("[^0-9]+", " ");
        return Arrays.stream(
                str.trim().split(" ")
        ).map(
                Integer::parseInt
        ).collect(
                Collectors.toList()
        );
    }

    // splits values in line by white spaces and then returns all parts which can be converted to int
    private List<Integer> parseIntsFromLine(final String line) {

        return Arrays.stream(
                line.split(" +")
        ).filter(
                this::isNumeric
        ).map(
                Integer::parseInt
        ).collect(
                Collectors.toList()
        );
    }

    private Formula parseInstance(final File file) {
        String id;
        int variablesCount = 0, clausesCount = 0;
        id = file.getName().substring(6, file.getName().length() - 6);
        List<Clause> clauses = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                String lineText = sb.toString();
                if (lineText.startsWith("c")) {// || lineText.contains("%")) {
                    sb.setLength(0);
                    line = br.readLine();
                    continue;
                }

                if (lineText.startsWith("p")) {
                    List<Integer> parsedInts = parseIntsFromLine(lineText);
                    if (parsedInts.size() != 2) {
                        throw new Exception("File " + file.getName() + " has unexpected format! p int size");
                    }
                    variablesCount = parsedInts.get(0);
                    clausesCount = parsedInts.get(1) - 1; // i have found out that the number is always +1, so it needs to be decreased
                } else if (lineText.startsWith("w")) {
                    weights = parseIntsFromLine(lineText.substring(0, lineText.length() - 1));
                    if (weights.size() != variablesCount) {
                        throw new Exception("File " + file.getName() + " has unexpected format! weights int size");
                    }
                } else { // read clause
                    List<Integer> rawClauses = parseIntsFromLine(lineText.substring(0, lineText.length() - 1));
                    if (rawClauses.size() != 3) {
                        throw new Exception("File " + file.getName() + " has unexpected format! clauses count!");
                    }
                    clauses.add(new Clause(rawClauses));
                }

                sb.setLength(0);
                line = br.readLine();
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return new Formula(id, variablesCount, clausesCount, clauses, weights);
    }

    public Map<Integer, Map<String, Formula>> getInstanceCntToFormulasByIds() {
        return instanceCntToFormulasByIds;
    }

//    public Map<String, Formula> getFileNameToFormulas() {
//        return fileNameToFormulas;
//    }
}