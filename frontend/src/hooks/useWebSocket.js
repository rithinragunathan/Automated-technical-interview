import { useState, useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default function useWebSocket() {
  const [connected, setConnected] = useState(false);
  const clientRef = useRef(null);

  const connect = useCallback((submissionId, onMessage) => {
    const socket = new SockJS('/ws');
    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: function (str) {
        console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = (frame) => {
      setConnected(true);
      console.log('Connected: ' + frame);
      
      stompClient.subscribe(`/topic/submissions/${submissionId}`, (message) => {
        if (message.body) {
          const payload = JSON.parse(message.body);
          onMessage(payload);
        }
      });
    };

    stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };
    
    stompClient.onWebSocketClose = () => {
      setConnected(false);
      console.log('WebSocket Closed');
    };

    stompClient.activate();
    clientRef.current = stompClient;
    
    return stompClient;
  }, []);

  const disconnect = useCallback(() => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setConnected(false);
    }
  }, []);

  useEffect(() => {
    return () => {
      disconnect();
    };
  }, [disconnect]);

  return { connected, connect, disconnect };
}
