// Import required modules
const fs = require('fs');
const readline = require('readline');

// Start Program
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

let filePath;

// Prompt user for dataset file path (must happen before any processing)
function askFilePath() {
    rl.question("Enter dataset file path: ", (path) => {
        if (fs.existsSync(path) && fs.statSync(path).isFile()) {
            filePath = path;
            processFile();
        } else {
            console.log("Invalid file path. Please try again.");
            askFilePath();
        }
    });
}

// Read CSV using fs and store records in an ArrayList equivalent
function processFile() {
    let records = [];
    let headers = null; // keep header columns for later reporting

    try {
        const data = fs.readFileSync(filePath, 'utf8');
        const lines = data.split(/\r?\n/);
        let isDataStarted = false;

        for (let line of lines) {
            // Skip header rows until we reach the actual data
            if (!isDataStarted) {
                if (line.startsWith("Candidate,")) {
                    headers = parseCSVLine(line);
                    isDataStarted = true;
                }
                continue;
            }

            // Parse CSV line and store as a record (array of fields)
            const fields = parseCSVLine(line);
            if (fields.length >= 8) {
                records.push(fields);
            }
        }

        // Process dataset and display formatted output
        displayAnalytics(records, headers);

    } catch (err) {
        // Handle errors reading the file (missing file, permissions, etc.)
        console.log("Error reading file: " + err.message);
    } finally {
        rl.close();
    }
}

// Dataset handling: simple CSV parser that supports quoted fields
function parseCSVLine(line) {
    let fields = [];
    let inQuotes = false;
    let currentField = "";

    for (let c of line) {
        if (c === '"') {
            inQuotes = !inQuotes;
        } else if (c === ',' && !inQuotes) {
            fields.push(currentField);
            currentField = "";
        } else {
            currentField += c;
        }
    }
    fields.push(currentField);

    return fields;
}

// Processing logic: compute statistics, detect duplicates, compute frequency counts
function displayAnalytics(records, headers) {
    if (records.length === 0) {
        console.log("No valid records found.");
        return;
    }

    // Dataset statistics
    console.log("\n=== Dataset Statistics ===");
    console.log("Total Records: " + records.length);
    console.log("Columns: " + (headers === null ? "unknown" : headers.length));
    if (headers !== null) {
        console.log("Column Names: " + headers.join(", "));
    }

    // Track duplicates and column value frequencies
    let seenRecords = new Set();
    let duplicates = [];
    let columnFrequencies = {};

    let passCount = 0;
    let failCount = 0;
    let totalScore = 0;
    let minScore = Number.MAX_VALUE;
    let maxScore = Number.MIN_VALUE;

    for (let record of records) {
        // Duplicate detection (full row match)
        let rowKey = record.join("|").trim();
        if (seenRecords.has(rowKey)) {
            duplicates.push(rowKey);
        } else {
            seenRecords.add(rowKey);
        }

        // Frequency count per column
        for (let col = 0; col < record.length; col++) {
            let value = record[col].trim();
            if (value === "") {
                value = "<EMPTY>";
            }
            if (!columnFrequencies[col]) {
                columnFrequencies[col] = {};
            }
            columnFrequencies[col][value] = (columnFrequencies[col][value] || 0) + 1;
        }

        // Exam score processing (column 6) and pass/fail (column 7)
        if (record.length >= 8) {
            let score = parseInt(record[6].trim());
            let result = record[7].trim();

            if (!isNaN(score)) {
                totalScore += score;
                minScore = Math.min(minScore, score);
                maxScore = Math.max(maxScore, score);

                if (result.toUpperCase() === "PASS") {
                    passCount++;
                } else if (result.toUpperCase() === "FAIL") {
                    failCount++;
                }
            }
        }
    }

    console.log("Unique candidates: " + Object.keys(columnFrequencies[0] || {}).length);
    if (minScore !== Number.MAX_VALUE) {
        console.log(`Score: min=${minScore} max=${maxScore} average=${(totalScore / records.length).toFixed(2)}`);
    }
    console.log(`Pass: ${passCount}  Fail: ${failCount}  Pass rate: ${(passCount / records.length * 100).toFixed(1)}%`);

    // Display duplicates
    console.log("\n=== Duplicate Records ===");
    if (duplicates.length === 0) {
        console.log("No duplicate records found.");
    } else {
        console.log(`Found ${duplicates.length} duplicate record(s) (showing up to 5):`);
        for (let i = 0; i < duplicates.length && i < 5; i++) {
            console.log("  " + duplicates[i]);
        }
    }

    // Display frequency counts for each column
    console.log("\n=== Frequency Count per Column (Top Values) ===");
    let columnsToShow = headers !== null ? headers.length : Object.keys(columnFrequencies).length;
    for (let col = 0; col < columnsToShow; col++) {
        let freq = columnFrequencies[col];
        if (!freq) continue;

        let colName = headers !== null && col < headers.length ? headers[col] : "Column " + col;
        console.log(`\n-- ${colName} (${Object.keys(freq).length} distinct) --`);

        // Show all unique values sorted by frequency
        let topValues = Object.entries(freq).sort((a, b) => {
            let byCount = b[1] - a[1];
            if (byCount !== 0) return byCount;
            return a[0].localeCompare(b[0]);
        });

        for (let [value, count] of topValues) {
            console.log(`  ${value}: ${count}`);
        }
    }
}

// End Program
askFilePath();
