import { useState, useEffect } from 'react';
import mqtt from 'mqtt';

let client = null;
let subscribers = new Map();

export function useMQTT(topic, defaultValue = null) {
  const [value, setValue] = useState(defaultValue);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Initialize MQTT client if not already connected
    if (!client) {
      const mqttUrl = process.env.NEXT_PUBLIC_MQTT_URL || 'ws://localhost:15675/ws';
      const username = process.env.NEXT_PUBLIC_MQTT_USERNAME || 'nebula';
      const password = process.env.NEXT_PUBLIC_MQTT_PASSWORD || 'password';

      client = mqtt.connect(mqttUrl, {
        username,
        password,
        reconnectPeriod: 5000,
        connectTimeout: 30000,
      });

      client.on('connect', () => {
        console.log('MQTT connected');
        setConnected(true);
      });

      client.on('error', (err) => {
        console.error('MQTT error:', err);
        setConnected(false);
      });

      client.on('close', () => {
        console.log('MQTT disconnected');
        setConnected(false);
      });

      client.on('message', (receivedTopic, message) => {
        const callbacks = subscribers.get(receivedTopic);
        if (callbacks) {
          const data = message.toString();
          callbacks.forEach(callback => callback(data));
        }
      });
    }

    // Subscribe to topic
    if (topic && client) {
      if (!subscribers.has(topic)) {
        subscribers.set(topic, new Set());
        client.subscribe(topic, (err) => {
          if (err) {
            console.error(`Failed to subscribe to ${topic}:`, err);
          }
        });
      }

      const callback = (data) => {
        try {
          // Try to parse as JSON, otherwise use as string
          const parsed = JSON.parse(data);
          setValue(parsed);
        } catch {
          setValue(data);
        }
      };

      subscribers.get(topic).add(callback);

      // Cleanup
      return () => {
        const callbacks = subscribers.get(topic);
        if (callbacks) {
          callbacks.delete(callback);
          if (callbacks.size === 0) {
            subscribers.delete(topic);
            if (client) {
              client.unsubscribe(topic);
            }
          }
        }
      };
    }
  }, [topic]);

  return { value, connected };
}

export function publishMQTT(topic, message) {
  if (client && client.connected) {
    const payload = typeof message === 'string' ? message : JSON.stringify(message);
    client.publish(topic, payload);
    return true;
  }
  return false;
}
