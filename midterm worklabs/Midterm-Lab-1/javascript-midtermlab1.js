const fs = require('fs');
const readline = require('readline');

// Create readline interface
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// Function to ask for file path until valid
function askFilePath() {
    rl.question("Enter dataset file path: ", function (path) {
        if (fs.existsSync(path) && fs.statSync(path).isFile() && path.endsWith(".csv")) {
            console.log("File found. Processing...\n");
            processFile(path);
        } else {
            console.log("Invalid file path or format. Try again.\n");
            askFilePath(); // loop until valid
        }
    });
}

// Function to process dataset
function processFile(path) {
    try {
        const data = fs.readFileSync(path, 'utf8');
        const lines = data.split(/\r?\n/).filter(line => line.trim() !== "");

        let records = [];
        for (let line of lines) {
    let trimmed = line.trim();

    // Only accept lines that are purely digits (with optional decimal point)
    if (/^\d+(\.\d+)?$/.test(trimmed)) {
        let value = parseFloat(trimmed);
        records.push(value);
    } else {
        console.log(`Skipping invalid record: ${line}`);
    }
}


        // ✅ Add this check
        if (records.length === 0) {
            console.log("No valid records found in dataset.");
            rl.close();
            return;
        }

        // Compute analytics
        const totalRecords = records.length;
        const totalSales = records.reduce((sum, val) => sum + val, 0);
        const averageSales = totalSales / totalRecords;
        const highestSale = Math.max(...records);
        const lowestSale = Math.min(...records);

        // Display formatted report
        console.log("===== SALES SUMMARY REPORT =====");
        console.log(`Total Records: ${totalRecords}`);
        console.log(`Total Sales: ${totalSales.toFixed(2)}`);
        console.log(`Average Sales per Transaction: ${averageSales.toFixed(2)}`);
        console.log(`Highest Transaction: ${highestSale.toFixed(2)}`);
        console.log(`Lowest Transaction: ${lowestSale.toFixed(2)}`);
        console.log("================================");

    } catch (err) {
        console.error("Error reading file:", err.message);
    } finally {
        rl.close();
    }
}

// Start program
askFilePath();
