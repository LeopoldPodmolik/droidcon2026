// Pre-request Script: Generate a unique correlation ID for request tracing
// Format: platform-uuid-timestamp
// Example: android-a1b2c3d4-e5f6-7890-abcd-ef1234567890-1712073600
//
// Usage: Add this script to the "Pre-request Script" tab in Postman
// Sets variable: {{correlationId}}

function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const platform = pm.variables.get("platform") || "android";
const uuid = generateUUID();
const timestamp = Math.floor(Date.now() / 1000);

const correlationId = `${platform}-${uuid}-${timestamp}`;

// Set as header variable
pm.variables.set("correlationId", correlationId);

console.log(`Generated Correlation ID: ${correlationId}`);
