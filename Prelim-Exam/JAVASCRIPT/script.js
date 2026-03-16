/*
 * Villena, john Kenneth M. - 23-0380-161
 */

// Hardcoded CSV data
const csvData = `
StudentID,first_name,last_name,LAB WORK 1,LAB WORK 2,LAB WORK 3,PRELIM EXAM,ATTENDANCE GRADE
073900438,Osbourne,Wakenshaw,69,5,52,12,78
114924014,Albie,Gierardi,58,92,16,57,97
`;

// Parse CSV into array of objects
function parseCSV(csv) {
const lines = csv.trim().split("\n");
const headers = lines[0].split(",");
return lines.slice(1).map(line => {
    const values = line.split(",");
    const obj = {};
    headers.forEach((h, i) => obj[h] = values[i]);
    return obj;
});
}

let records = parseCSV(csvData);

// Render table
function render() {
const tbody = document.querySelector("#recordsTable tbody");
tbody.innerHTML = "";
records.forEach((record, index) => {
    const row = document.createElement("tr");
    Object.values(record).forEach(val => {
    const cell = document.createElement("td");
    cell.textContent = val;
    row.appendChild(cell);
    });

    // Delete button
    const actionCell = document.createElement("td");
    const delBtn = document.createElement("button");
    delBtn.textContent = "Delete";
    delBtn.onclick = () => {
records.splice(index, 1);
render();
    };
    actionCell.appendChild(delBtn);
    row.appendChild(actionCell);

    tbody.appendChild(row);
});
}

// Add record
document.getElementById("recordForm").addEventListener("submit", e => {
e.preventDefault();
const newRecord = {
    "StudentID": document.getElementById("studentId").value,
    "first_name": document.getElementById("firstName").value,
    "last_name": document.getElementById("lastName").value,
    "LAB WORK 1": document.getElementById("lab1").value,
    "LAB WORK 2": document.getElementById("lab2").value,
    "LAB WORK 3": document.getElementById("lab3").value,
    "PRELIM EXAM": document.getElementById("prelim").value,
    "ATTENDANCE GRADE": document.getElementById("attendance").value
};
records.push(newRecord);
render();
e.target.reset();
});

// Initial render
render();
