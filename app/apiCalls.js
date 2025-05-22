const fs = require('fs');

// Load configuration
const config = JSON.parse(fs.readFileSync('config.json', 'utf8'));
const apiKey = config.apiKey;

// Example API call (replace with your actual API endpoint)
const url = `https://your-api-endpoint?key=${apiKey}`;

fetch(url)
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
