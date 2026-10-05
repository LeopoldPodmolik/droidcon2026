// Pre-request Script: Auto-refresh JWT token if expired or about to expire
// Token is stored in environment variable and reused until near expiry
//
// Usage: Add this script to the "Pre-request Script" tab in Postman
// Requires env vars: clientId, clientSecret, baseUrl
// Sets env vars: apiKey, tokenExpiry

const tokenExpiry = pm.environment.get("tokenExpiry");
const currentTime = Math.floor(Date.now() / 1000);
const bufferSeconds = 60; // Refresh 1 minute before expiry

if (!tokenExpiry || currentTime >= (parseInt(tokenExpiry) - bufferSeconds)) {
    console.log("Token expired or about to expire. Refreshing...");

    const authRequest = {
        url: pm.variables.replaceIn("{{baseUrl}}/api/v1/auth/token"),
        method: "POST",
        header: {
            "Content-Type": "application/json"
        },
        body: {
            mode: "raw",
            raw: JSON.stringify({
                clientId: pm.environment.get("clientId"),
                clientSecret: pm.environment.get("clientSecret"),
                grantType: "client_credentials"
            })
        }
    };

    pm.sendRequest(authRequest, function (err, response) {
        if (err) {
            console.error("Token refresh failed:", err);
            return;
        }

        const jsonResponse = response.json();
        const newToken = jsonResponse.accessToken;
        const expiresIn = jsonResponse.expiresIn; // seconds

        pm.environment.set("apiKey", newToken);
        pm.environment.set("tokenExpiry", String(currentTime + expiresIn));

        console.log(`Token refreshed. Expires in ${expiresIn}s`);
        console.log(`New token: ${newToken.substring(0, 20)}...`);
    });
} else {
    const remaining = parseInt(tokenExpiry) - currentTime;
    console.log(`Token still valid. Expires in ${remaining}s`);
}
