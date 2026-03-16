import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Programming2MachineProblemSet {
    public static void main(String[] args) {
        // Start Program
        Scanner input = new Scanner(System.in);
        File file;

        // Prompt user for dataset file path (must happen before any processing)
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine();
            file = new File(path);

            // Validate path exists and is a file
            if (file.exists() && file.isFile()) {
                break;
            } else {
                System.out.println("Invalid file path. Please try again.");
            }
        }

        // Read CSV using BufferedReader/FileReader and store records in an ArrayList
        List<String[]> records = new ArrayList<>();
        String[] headers = null; // keep header columns for later reporting

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isDataStarted = false;

            while ((line = br.readLine()) != null) {
                // Skip header rows until we reach the actual data
                if (!isDataStarted) {
                    if (line.startsWith("Candidate,")) {
                        headers = parseCSVLine(line);
                        isDataStarted = true;
                    }
                    continue;
                }

                // Parse CSV line and store as a record (array of fields)
                String[] fields = parseCSVLine(line);
                if (fields.length >= 8) {
                    records.add(fields);
                }
            }

            // Process dataset and display formatted output
            displayAnalytics(records, headers);

        } catch (IOException e) {
            // Handle errors reading the file (missing file, permissions, etc.)
            System.out.println("Error reading file: " + e.getMessage());
        }

        // End Program
        input.close();
    }

    // Dataset handling: simple CSV parser that supports quoted fields
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

        return fields.toArray(new String[0]);
    }

    // Processing logic: compute statistics, detect duplicates, compute frequency counts
    private static void displayAnalytics(List<String[]> records, String[] headers) {
        if (records.isEmpty()) {
            System.out.println("No valid records found.");
            return;
        }

        // Dataset statistics
        System.out.println("\n=== Dataset Statistics ===");
        System.out.println("Total Records: " + records.size());
        System.out.println("Columns: " + (headers == null ? "unknown" : headers.length));
        if (headers != null) {
            System.out.println("Column Names: " + String.join(", ", headers));
        }

        // Track duplicates and column value frequencies
        Set<String> seenRecords = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        Map<Integer, Map<String, Integer>> columnFrequencies = new HashMap<>();

        int passCount = 0;
        int failCount = 0;
        int totalScore = 0;
        int minScore = Integer.MAX_VALUE;
        int maxScore = Integer.MIN_VALUE;

        for (String[] record : records) {
            // Duplicate detection (full row match)
            String rowKey = String.join("|", record).trim();
            if (!seenRecords.add(rowKey)) {
                duplicates.add(rowKey);
            }

            // Frequency count per column
            for (int col = 0; col < record.length; col++) {
                String value = record[col].trim();
                if (value.isEmpty()) {
                    value = "<EMPTY>";
                }
                columnFrequencies
                        .computeIfAbsent(col, k -> new HashMap<>())
                        .merge(value, 1, Integer::sum);
            }

            // Exam score processing (column 6) and pass/fail (column 7)
            if (record.length >= 8) {
                try {
                    int score = Integer.parseInt(record[6].trim());
                    String result = record[7].trim();

                    totalScore += score;
                    minScore = Math.min(minScore, score);
                    maxScore = Math.max(maxScore, score);

                    if ("PASS".equalsIgnoreCase(result)) {
                        passCount++;
                    } else if ("FAIL".equalsIgnoreCase(result)) {
                        failCount++;
                    }
                } catch (NumberFormatException e) {
                    // Invalid score value, skip for numeric stats
                }
            }
        }

        System.out.println("Unique candidates: " + columnFrequencies.getOrDefault(0, Collections.emptyMap()).size());
        if (minScore != Integer.MAX_VALUE) {
            System.out.printf("Score: min=%d max=%d average=%.2f%n", minScore, maxScore,
                    (double) totalScore / records.size());
        }
        System.out.printf("Pass: %d  Fail: %d  Pass rate: %.1f%%%n", passCount, failCount,
                (double) passCount / records.size() * 100);

        // Display duplicates
        System.out.println("\n=== Duplicate Records ===");
        if (duplicates.isEmpty()) {
            System.out.println("No duplicate records found.");
        } else {
            System.out.println("Found " + duplicates.size() + " duplicate record(s) (showing up to 5):");
            for (int i = 0; i < duplicates.size() && i < 5; i++) {
                System.out.println("  " + duplicates.get(i));
            }
        }

        // Display frequency counts for each column
        System.out.println("\n=== Frequency Count per Column (Top Values) ===");
        int columnsToShow = headers != null ? headers.length : columnFrequencies.size();
        for (int col = 0; col < columnsToShow; col++) {
            Map<String, Integer> freq = columnFrequencies.get(col);
            if (freq == null) {
                continue;
            }

            String colName = headers != null && col < headers.length ? headers[col] : "Column " + col;
            System.out.println("\n-- " + colName + " (" + freq.size() + " distinct) --");

            // Show top 10 values by frequency
            List<Map.Entry<String, Integer>> topValues = new ArrayList<>(freq.entrySet());
            Collections.sort(topValues, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
                    int byCount = Integer.compare(b.getValue(), a.getValue());
                    if (byCount != 0) {
                        return byCount;
                    }
                    return a.getKey().compareTo(b.getKey());
                }
            });

            int limit = topValues.size();  // Show all unique values
            for (int i = 0; i < limit; i++) {
                Map.Entry<String, Integer> entry = topValues.get(i);
                System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
            }
        }
    }

}
