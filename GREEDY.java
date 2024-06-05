import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numberOfJudges = getInput(scanner, "Enter the number of judges: ");
        int changeCost = getInput(scanner, "Enter the cost change for a problem type: ");
        scanner.nextLine();

        String file = "input3.txt";

        try {
            List<Integer> problemTypes = loadProblemTypes(file);
            List<List<Integer>> judgeAssignments = new LinkedList<>();

            for (int i = 0; i < numberOfJudges; i++) {
                judgeAssignments.add(new LinkedList<>());
            }
            //O(n)+O(n)+O(n*judgesNumber)=O(n*judgesNumber)
            int totalCost = calculateTotalCost(numberOfJudges, changeCost, problemTypes, judgeAssignments);

            System.out.println("Total cost: " + totalCost);
            displayJudgeDistribution(judgeAssignments);
        } catch (IOException e) {
            System.out.println("Error with file " + e.getMessage());
        }
    }

    private static int getInput(Scanner scanner, String input) {
        System.out.print(input);
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    public static List<Integer> loadProblemTypes(String filePath) throws IOException { //O(n)
        List<Integer> types = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Type")) {
                    String typeString = line.trim().split("\\s+")[1];
                    types.add(Integer.parseInt(typeString));
                }
            }
        }
        return types;
    }

    private static int calculateTotalCost(int judgeCount, int changeCost, List<Integer> problemTypes, List<List<Integer>> judgeAssignments) {//O(n*judgesNumber)
        int totalCost = 0;
        int currentTypeIndex = 0;

        while (currentTypeIndex < problemTypes.size()) {
            int currentType = problemTypes.get(currentTypeIndex);
            boolean assigned = false;

            for (int judgeIndex = 0; judgeIndex < judgeAssignments.size(); judgeIndex++) {
                List<Integer> currentAssignment = judgeAssignments.get(judgeIndex);
                if (!currentAssignment.isEmpty() && currentAssignment.get(currentAssignment.size() - 1).equals(currentType)) {
                    currentAssignment.add(currentType);
                    assigned = true;
                    break;
                }
            }

            if (!assigned) {
                int judgeIndex = findBestOptionJudge(problemTypes, currentType, judgeAssignments);
                judgeAssignments.get(judgeIndex).add(currentType);
                totalCost += changeCost;
            }
            currentTypeIndex++;
        }
        return totalCost;
    }

    private static int findBestOptionJudge(List<Integer> problemTypes, int currentType, List<List<Integer>> judgeAssignments) {//O(n)
        int furthestIndex = -1;
        int maxDistance = -1;

        for (int i = 0; i < judgeAssignments.size(); i++) {
            int lastType = getLastAssignedType(judgeAssignments, i);
            int nextIndex = calculateNextIndex(problemTypes, currentType, lastType);
            if (nextIndex > maxDistance) {
                maxDistance = nextIndex;
                furthestIndex = i;
            }
        }
        return furthestIndex;
    }

    private static int calculateNextIndex(List<Integer> problemTypes, int currentType, int targetType) {//O(n)
        if (targetType == -1) {
            return Integer.MAX_VALUE;
        }
        int i = problemTypes.indexOf(currentType) + 1;
        while (i < problemTypes.size()) {
            if (problemTypes.get(i) == targetType) {
                return i - problemTypes.indexOf(currentType);
            }
            i++;
        }
        return Integer.MAX_VALUE;
    }

    private static int getLastAssignedType(List<List<Integer>> judgeAssignments, int judgeIndex) {//O(n)
        List<Integer> assignments = judgeAssignments.get(judgeIndex);
        return assignments.isEmpty() ? -1 : assignments.get(assignments.size() - 1);
    }

    private static void displayJudgeDistribution(List<List<Integer>> judgeAssignments) {//O(n)
        for (int i = 0; i < judgeAssignments.size(); i++) {
            System.out.print("Judge " + (i + 1) + ": ");
            for (int j = 0; j < judgeAssignments.get(i).size(); j++) {
                System.out.print(judgeAssignments.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
}
