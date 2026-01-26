// A simple object for demonstration purposes
const validCredentials = {
    username: "123",
    password: "1234"
};

const loginForm = document.getElementById('login-form');
const messageArea = document.getElementById('message');

loginForm.addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent page reload

    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;

    handleLogin(usernameInput, passwordInput);
});

function playSound(soundFile) {
  // Create a new Audio object with the path to your sound file
const audio = new Audio(soundFile);
  // Play the sound
audio.play();
}
 //  handle validation
function handleLogin(username, password) {
    if (username === validCredentials.username && password === validCredentials.password) {
        messageArea.textContent = 'Login successful! Welcome, ' + username;
        messageArea.style.color = 'green';
        // delays load of next page
        setTimeout(function() {
        window.location.href = "Display timestamps.html";
        }, 1005); 
    } else {
        // error display
        messageArea.textContent = 'Invalid username or password';
        messageArea.style.color = 'red';       
        playSound('D:/Villena PrelimLabWork2/beep-warning-6387.mp3'); 
    }
}

// 1. Create a new Date object for the current time
const now = new Date();

// 2. Extract the month, day, and year
// Get the month (0-indexed, so add 1)
const month = (now.getMonth() + 1).toString().padStart(2, '0');

// Get the day
const day = now.getDate().toString().padStart(2, '0');

// Get the full year
const year = now.getFullYear().toString();

// 3. Combine them into the desired format
const formattedDate = `${month}/${day}/${year}`;

// Example output: User login time: 01/12/2026
