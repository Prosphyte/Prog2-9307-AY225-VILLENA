import java.io.*;
import java.util.*;

class DataRecord {
    private double sales;

    public DataRecord(double sales) {
        this.sales = sales;
    }

    public double getSales() {
        return sales;
    }
}

public class SalesSummaryDashboard {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File file;

        // Step 1: Prompt user until valid file path
        while (true) {
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine();
            file = new File(path);

            if (file.exists() && file.isFile() && path.endsWith(".csv")) {
                break;
            } else {
                System.out.println("Invalid file path or format. Please try again.");
            }
        }

        // Step 2: Process dataset
        List<DataRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    double salesValue = Double.parseDouble(line.trim());
                    records.add(new DataRecord(salesValue));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        // Step 3: Compute analytics
        if (records.isEmpty()) {
            System.out.println("No valid records found in dataset.");
            return;
        }

        int totalRecords = records.size();
        double totalSales = records.stream().mapToDouble(DataRecord::getSales).sum();
        double averageSales = totalSales / totalRecords;
        double highestSale = records.stream().mapToDouble(DataRecord::getSales).max().orElse(0);
        double lowestSale = records.stream().mapToDouble(DataRecord::getSales).min().orElse(0);

        // Step 4: Display formatted report
        System.out.println("\n===== SALES SUMMARY REPORT =====");
        System.out.printf("Total Records: %d%n", totalRecords);
        System.out.printf("Total Sales: %.2f%n", totalSales);
        System.out.printf("Average Sales per Transaction: %.2f%n", averageSales);
        System.out.printf("Highest Transaction: %.2f%n", highestSale);
        System.out.printf("Lowest Transaction: %.2f%n", lowestSale);
        System.out.println("================================");
    }
}
