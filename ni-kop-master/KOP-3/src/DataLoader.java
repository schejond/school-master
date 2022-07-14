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

    public DataLoader(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public Map<String, Map<Integer, Knapsack>> getFileNameToKnapsacksById() {
        return fileNameToKnapsacksById;
    }

    public void readFilesInFolder(String folderName) {
        Map<String, Map<Integer, Knapsack>> taskNameToListOfKnapsacks = new HashMap<>();

        final String directoryToLoad = dataFolder + "\\" + folderName;
        File dir = new File(directoryToLoad);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                taskNameToListOfKnapsacks.put(file.getName(), readInstances(file));
            }
        } else {
            System.out.println("The directory to load: " + directoryToLoad + " was not found.");
        }

        this.fileNameToKnapsacksById = taskNameToListOfKnapsacks;
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
                capacity = values.get(2);

        if (values.size() - 3 != itemsCount * 2) {
            throw new Exception("Number of items does not equal obtained n in line: " + line);
        }

        List<Item> items = new ArrayList<>();
        int weight = -1;
        for (int i = 3; i < values.size(); i++) {
            if (weight != -1) {
                items.add(new Item(weight, values.get(i)));
                weight = -1;
                continue;
            }
            weight = values.get(i);
        }

        return new Knapsack(id, itemsCount, capacity, items);
    }
}
