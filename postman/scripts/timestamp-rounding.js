// Pre-request Script: Round current time to the nearest 5-minute interval
// This ensures CDN cache-consistent requests for live score polling
//
// Usage: Add this script to the "Pre-request Script" tab in Postman
// Sets variable: {{timestamp}}

const now = new Date();
const minutes = now.getMinutes();
const roundedMinutes = Math.floor(minutes / 5) * 5;

now.setMinutes(roundedMinutes);
now.setSeconds(0);
now.setMilliseconds(0);

const roundedTimestamp = now.toISOString();

// Set the variable for use in the request
pm.variables.set("timestamp", roundedTimestamp);

console.log(`Original time: ${new Date().toISOString()}`);
console.log(`Rounded time:  ${roundedTimestamp}`);
