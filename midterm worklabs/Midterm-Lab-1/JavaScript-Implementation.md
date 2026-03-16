JAVASCRIPT (NODE.JS) IMPLEMENTATION REQUIREMENT
Students must:
Use readline for user input
Use fs.existsSync() for validation
Loop until valid

const fs = require('fs');
const readline = require('readline');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function askFilePath() {
    rl.question("Enter dataset file path: ", function(path) {
        if (fs.existsSync(path)) {
            console.log("File found. Processing...");
            // proceed with reading file
        } else {
            console.log("Invalid file path. Try again.");
            askFilePath();
        }
    });
}

askFilePath();