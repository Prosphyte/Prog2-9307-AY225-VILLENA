import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Programming2MachineProblemSet {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File file;

        // Prompt user for dataset file path and validate
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine();
            file = new File(path);

            if (file.exists() && file.isFile()) {
                break;
            } else {
                System.out.println("Invalid file path. Please try again.");
            }
        }

        // Read CSV and store records
        ArrayList<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isDataStarted = false;

            while ((line = br.readLine()) != null) {
                // Skip header rows until we reach the actual data
                if (!isDataStarted) {
                    if (line.startsWith("Candidate,")) {
                        isDataStarted = true;
                    }
                    continue;
                }

                // Parse CSV line
                String[] fields = parseCSVLine(line);
                if (fields.length >= 8) {
                    records.add(fields);
                }
            }

            // Process and display results
            displayAnalytics(records);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        input.close();
    }

    // Simple CSV parser
    private static String[] parseCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());

        return fields.toArray(new String[fields.size()]);
    }

    // Display basic analytics
    private static void displayAnalytics(ArrayList<String[]> records) {
        if (records.isEmpty()) {
            System.out.println("No valid records found.");
            return;
        }

        System.out.println("\n=== Exam Results Summary ===");
        System.out.println("Total Records: " + records.size());

        int passCount = 0;
        int failCount = 0;
        int totalScore = 0;

        for (String[] record : records) {
            if (record.length >= 8) {
                try {
                    int score = Integer.parseInt(record[6]);
                    String result = record[7];

                    totalScore += score;
                    if ("PASS".equals(result)) {
                        passCount++;
                    } else if ("FAIL".equals(result)) {
                        failCount++;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid score
                }
            }
        }

        double averageScore = (double) totalScore / records.size();

        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + failCount);
        System.out.printf("Average Score: %.2f%n", averageScore);
        System.out.printf("Pass Rate: %.1f%%%n", (double) passCount / records.size() * 100);
    }
}
