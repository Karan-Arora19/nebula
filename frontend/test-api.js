#!/usr/bin/env node

// Simple test script to verify API connectivity from Node.js environment
// This mimics what the frontend would do

const VEHICLE_SERVICE_URL = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080';

async function testApiConnection() {
    console.log(`Testing API connection to: ${VEHICLE_SERVICE_URL}`);

    try {
        // Use node-fetch or built-in fetch (Node 18+)
        const response = await fetch(`${VEHICLE_SERVICE_URL}/api/v1/vehicles?size=100`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('✅ API connection successful!');
        console.log(`Found ${data.vehicles ? data.vehicles.length : 0} vehicles`);
        console.log('Sample vehicle:', data.vehicles ? data.vehicles[0] : 'None');

        return true;
    } catch (error) {
        console.error('❌ API connection failed:', error.message);
        return false;
    }
}

testApiConnection()
    .then(success => {
        process.exit(success ? 0 : 1);
    })
    .catch(error => {
        console.error('Unexpected error:', error);
        process.exit(1);
    });
