import KnapsackClasses.Item;
import KnapsackClasses.Knapsack;
import KnapsackClasses.Solution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// class which loads data from files and returns obtained knapsack objects
public class DataLoader {
    private final String dataFolder;
    private Map<String, Map<Integer, Knapsack>> fileNameToKnapsacksById;
    private Map<String, Map<Integer, Solution>> fileNameToSolutionsById;

    public DataLoader(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public Map<String, Map<Integer, Knapsack>> getFileNameToKnapsacksById() {
        return fileNameToKnapsacksById;
    }

    public Map<String, Map<Integer, Solution>> getFileNameToSolutionsById() {
        return fileNameToSolutionsById;
    }

    public void readFilesInFolder(String folderName) {
        Map<String, Map<Integer, Knapsack>> taskNameToListOfKnapsacks = new HashMap<>();
        Map<String, Map<Integer, Solution>> taskNameToListOfSolutions = new HashMap<>();

        final String directoryToLoad = dataFolder + "\\" + folderName;
        File dir = new File(directoryToLoad);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
//                System.out.println("Reading file: " + file.getName());
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(file.getName());

                if (!matcher.find()) {
                    System.out.println("Bad input file name! File name should contain number: "
                            + file.getName());
                }
                String taskName = folderName + matcher.group();

                if (file.getName().contains("inst")) { // instance
                    taskNameToListOfKnapsacks.put(taskName, readInstances(file));
                } else if (file.getName().contains("sol")) { // ref solution
                    taskNameToListOfSolutions.put(taskName, readSolutions(file));
                } else {
                    System.out.println("Bad input file name! File name: " + file.getName());
                }
            }
        } else {
            System.out.println("The directory to load: " + directoryToLoad + " was not found.");
        }

        this.fileNameToKnapsacksById = taskNameToListOfKnapsacks;
        this.fileNameToSolutionsById = taskNameToListOfSolutions;
    }

    private Map<Integer, Knapsack> readInstances(final File file) {
        Map<Integer, Knapsack> knapsacks = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                Knapsack knapsack = parseKnapsackContentFromString(sb.toString());
                knapsacks.put(knapsack.getId(), knapsack);
                sb.setLength(0);
                line = br.readLine();
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return knapsacks;
    }

    // lines are in format:
    // ID itemsCount capacity minPrice weight price w p w p ...
    private Knapsack parseKnapsackContentFromString(final String line) throws Exception {
        List<String> splittedString = Arrays.asList(line.split(" "));
        List<Integer> values = new ArrayList<>();
        splittedString.forEach((val) -> values.add(Integer.valueOf(val)));

        int id = values.get(0), itemsCount = values.get(1),
                capacity = values.get(2), minPrice = values.get(3);

        if (values.size() - 4 != itemsCount * 2) {
            throw new Exception("Number of items does not equal obtained n in line: " + line);
        }

        List<Item> items = new ArrayList<>();
        int weight = -1;
        for (int i = 4; i < values.size(); i++) {
            if (weight != -1) {
                items.add(new Item(weight, values.get(i)));
                weight = -1;
                continue;
            }
            weight = values.get(i);
        }

        return new Knapsack(id, itemsCount, capacity, minPrice, items);
    }

    private Map<Integer, Solution> readSolutions(final File file) {
        Map<Integer, Solution> solutions = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                Solution solution = parseSolutionContentFromString(sb.toString());
                solutions.put(solution.getId(), solution);
                sb.setLength(0);
                line = br.readLine();
            }
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File " + file.getName() + " not found");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return solutions;
    }

    // lines are in format:
    // ID itemsCount solutionPrice 0/1 0/1 ...
    private Solution parseSolutionContentFromString(final String line) throws Exception {
        List<String> splittedString = Arrays.asList(line.split(" "));
        List<Integer> values = new ArrayList<>();
        splittedString.forEach((val) -> values.add(Integer.valueOf(val)));

        int id = values.get(0), itemsCount = values.get(1),
                solutionPrice = values.get(2);

        if (values.size() - 3 != itemsCount) {
            throw new Exception("Number of items does not equal obtained n in solution line: " + line);
        }

        List<Integer> solutions = new ArrayList<>(values.subList(3, values.size()));

        return new Solution(id, itemsCount, solutionPrice, solutions);
    }
}
