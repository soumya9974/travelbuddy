import { useEffect, useRef, useState, useCallback } from "react";
import { Client, StompSubscription } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { WS_BASE_URL } from "../config/api";
import { ChatMessage } from "../types";
import { useAuth } from "../context/AuthContext";

export const useWebSocket = (
  groupId: number | null,
  onMessage: (message: ChatMessage) => void
) => {
  const { token } = useAuth();

  const clientRef = useRef<Client | null>(null);
  const chatSubRef = useRef<StompSubscription | null>(null);
  const onlineSubRef = useRef<StompSubscription | null>(null);

  const [connected, setConnected] = useState(false);
  const [onlineCount, setOnlineCount] = useState(0);

  const disconnect = useCallback(() => {
    chatSubRef.current?.unsubscribe();
    onlineSubRef.current?.unsubscribe();
    chatSubRef.current = null;
    onlineSubRef.current = null;

    clientRef.current?.deactivate();
    clientRef.current = null;

    setConnected(false);
    setOnlineCount(0);
  }, []);

  const sendMessage = useCallback(
    (message: ChatMessage) => {
      if (!clientRef.current || !clientRef.current.connected || !groupId)
        return;

      clientRef.current.publish({
        destination: `/app/groups/${groupId}/chat`,
        body: JSON.stringify(message),
      });
    },
    [groupId]
  );

  useEffect(() => {
    if (!groupId || !token) return;

    disconnect();

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_BASE_URL),
      reconnectDelay: 3000,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },

      onConnect: () => {
        // CHAT
        chatSubRef.current = client.subscribe(
          `/topic/groups/${groupId}`,
          (frame) => {
            onMessage(JSON.parse(frame.body));
          }
        );

        // ONLINE
        onlineSubRef.current = client.subscribe(
          `/topic/groups/${groupId}/online`,
          (frame) => {
            const raw = frame.body;

            const num = Number(raw);
            if (!isNaN(num)) {
              setOnlineCount(num);
              return;
            }

            try {
              const parsed = JSON.parse(raw);
              if (Array.isArray(parsed)) {
                setOnlineCount(parsed.length);
                return;
              }
              if (parsed?.users?.length) {
                setOnlineCount(parsed.users.length);
                return;
              }
            } catch {}

            setOnlineCount(0);
          }
        );

        setConnected(true);
      },

      onDisconnect: () => setConnected(false),
      onWebSocketError: (e) => console.error("WS error", e),
      onStompError: () => setConnected(false),
    });

    client.activate();
    clientRef.current = client;

    return () => disconnect();
  }, [groupId, token, onMessage, disconnect]);

  return { connected, sendMessage, onlineCount, disconnect };
};
