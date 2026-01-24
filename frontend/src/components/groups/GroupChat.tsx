import { useState, useEffect, useRef, useCallback } from "react";
import { useAuth } from "../../context/AuthContext";
import { ChatMessage, Message } from "../../types";
import { apiRequest } from "../../utils/api";
import { useWebSocket } from "../../hooks/useWebSocket";
import { Button } from "../ui/Button";
import { Spinner } from "../ui/Spinner";
import { Send, MoreVertical } from "lucide-react";

interface GroupChatProps {
  groupId: number;
  isMember: boolean;
  isAdmin: boolean;
}

export const GroupChat = ({ groupId, isMember, isAdmin }: GroupChatProps) => {
  const { token, user } = useAuth();

  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [messageInput, setMessageInput] = useState("");
  const [loading, setLoading] = useState(true);
  const [typingUser, setTypingUser] = useState<string | null>(null);

  const [openMenuForId, setOpenMenuForId] = useState<number | null>(null);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const typingTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const lastTypingSentAtRef = useRef(0);

  // ✅ safe string helper (prevents crash when content becomes number/null)
  const toSafeString = (v: unknown) =>
    v === null || v === undefined ? "" : String(v);

  /**
   * 🔥 Handle incoming WS events
   */
  const handleNewMessage = useCallback(
    (msg: ChatMessage) => {
      // ---- DELETE SINGLE ----
      if (msg.type === "DELETE" && msg.id) {
        setMessages((prev) => prev.filter((m) => m.id !== msg.id));
        return;
      }

      // ---- DELETE ALL ----
      if (msg.type === "DELETE_ALL") {
        setMessages([]);
        return;
      }

      // ---- TYPING ----
      if (msg.type === "TYPING") {
        if (Number(msg.senderId) === Number(user?.id)) return;

        setTypingUser(msg.senderName || "Someone");
        if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);

        typingTimeoutRef.current = setTimeout(() => setTypingUser(null), 2000);
        return;
      }

      // ---- CHAT ----
      const safeContent = toSafeString((msg as any).content);
      if (!safeContent.trim()) return;

      const normalized: ChatMessage = {
        ...msg,
        content: safeContent,
      };

      setMessages((prev) => {
        const exists = prev.some(
          (m) =>
            (m.id && normalized.id && m.id === normalized.id) ||
            (m.timestamp === normalized.timestamp &&
              Number(m.senderId) === Number(normalized.senderId) &&
              toSafeString(m.content) === toSafeString(normalized.content)),
        );
        return exists ? prev : [...prev, normalized];
      });
    },
    [user?.id],
  );

  const { connected, sendMessage, onlineCount } = useWebSocket(
    groupId,
    handleNewMessage,
  );

  /**
   * Fetch history
   */
  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const data: Message[] = await apiRequest(
          `/groups/${groupId}/messages`,
          {
            token: token!,
          },
        );

        setMessages(
          data.map((m) => ({
            id: m.id,
            groupId: m.groupId,
            senderId: Number(m.senderId),
            senderName: m.senderName,
            content: toSafeString((m as any).content), // ✅ important
            timestamp: m.timestamp,
            type: "CHAT",
          })),
        );
      } finally {
        setLoading(false);
      }
    };

    fetchMessages();
  }, [groupId, token]);

  /**
   * Auto-scroll
   */
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, typingUser]);

  /**
   * Close menu on outside click / escape
   * ✅ remove capture so stopPropagation works reliably
   */
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") setOpenMenuForId(null);
    };
    const onClick = () => setOpenMenuForId(null);

    if (openMenuForId !== null) {
      window.addEventListener("keydown", onKey);
      window.addEventListener("click", onClick); // ✅ no capture
    }

    return () => {
      window.removeEventListener("keydown", onKey);
      window.removeEventListener("click", onClick);
    };
  }, [openMenuForId]);

  /**
   * Send chat
   */
  const handleSendMessage = () => {
    const trimmed = messageInput.trim();
    if (!connected || !isMember || !trimmed) return;

    sendMessage({
      groupId,
      senderId: user!.id,
      senderName: user!.username,
      content: trimmed,
      timestamp: new Date().toISOString(),
      type: "CHAT",
    });

    setMessageInput("");
  };

  /**
   * Typing (throttled)
   */
  const sendTyping = () => {
    if (!connected || !isMember) return;

    const now = Date.now();
    if (now - lastTypingSentAtRef.current < 800) return;
    lastTypingSentAtRef.current = now;

    sendMessage({
      groupId,
      senderId: user!.id,
      senderName: user!.username,
      content: "...",
      timestamp: new Date().toISOString(),
      type: "TYPING",
    });
  };

  /**
   * Admin deletes
   */
  const deleteAllMessages = async () => {
    if (!confirm("Delete ALL messages in this group?")) return;

    await apiRequest(`/groups/${groupId}/messages`, {
      method: "DELETE",
      token: token!,
    });

    // ✅ optimistic UI (even if WS doesn't arrive)
    setMessages([]);
  };

  const deleteSingleMessage = async (messageId: number) => {
    if (!confirm("Delete this message?")) return;

    await apiRequest(`/groups/${groupId}/messages/${messageId}`, {
      method: "DELETE",
      token: token!,
    });

    // ✅ optimistic UI (even if WS doesn't arrive)
    setMessages((prev) => prev.filter((m) => m.id !== messageId));
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Spinner />
      </div>
    );
  }

  return (
    <div className="flex flex-col bg-white rounded-lg overflow-hidden border border-gray-200 h-[calc(100vh-220px)] min-h-[420px] max-h-[700px]">
      {isAdmin && messages.length > 0 && (
        <div className="p-2 border-b bg-white">
          <Button
            variant="outline"
            className="text-red-600 border-red-300"
            onClick={deleteAllMessages}
          >
            Delete all messages
          </Button>
        </div>
      )}

      <div className="px-4 py-2 text-sm border-b bg-gray-50">
        Online:{" "}
        {onlineCount === 0
          ? "No one online"
          : `${onlineCount} user${onlineCount > 1 ? "s" : ""}`}
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {messages.map((msg, idx) => {
          const isOwn = Number(msg.senderId) === Number(user?.id);
          const showName =
            !isOwn &&
            (idx === 0 ||
              Number(messages[idx - 1].senderId) !== Number(msg.senderId));

          const safeContent = toSafeString((msg as any).content);
          const key = msg.id ?? `${msg.timestamp}-${idx}`;

          return (
            <div
              key={key}
              className={`flex ${isOwn ? "justify-end" : "justify-start"}`}
            >
              <div className="flex items-start gap-2 max-w-[90%]">
                <div className="flex-1 min-w-0">
                  <div
                    className={`px-4 py-2 rounded-2xl ${
                      isOwn
                        ? "bg-blue-600 text-white"
                        : "bg-gray-100 text-gray-900"
                    }`}
                  >
                    {showName && (
                      <p
                        className={`text-xs font-semibold mb-1 ${
                          isOwn ? "text-blue-100" : "text-gray-600"
                        }`}
                      >
                        {msg.senderName}
                      </p>
                    )}

                    <p className="text-sm whitespace-pre-wrap break-words">
                      {safeContent}
                    </p>
                  </div>
                </div>

                {isAdmin && msg.id && (
                  <div
                    className="relative shrink-0"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <button
                      className="p-2 rounded-md hover:bg-gray-100 text-gray-500"
                      onClick={() =>
                        setOpenMenuForId((prev) =>
                          prev === msg.id ? null : msg.id!,
                        )
                      }
                      aria-label="Message actions"
                      type="button"
                    >
                      <MoreVertical size={16} />
                    </button>

                    {openMenuForId === msg.id && (
                      <div className="absolute right-0 mt-1 w-36 bg-white border border-gray-200 rounded-lg shadow-lg z-50">
                        <button
                          type="button"
                          className="w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg"
                          onClick={() => {
                            setOpenMenuForId(null);
                            deleteSingleMessage(msg.id!);
                          }}
                        >
                          Delete
                        </button>
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>
          );
        })}

        {typingUser && (
          <div className="text-sm italic text-gray-500 px-1">
            {typingUser} is typing...
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <div className="border-t p-3 flex gap-2 bg-gray-50">
        <textarea
          className="flex-1 px-4 py-2 border rounded-lg resize-none min-h-[44px]"
          value={messageInput}
          disabled={!connected || !isMember}
          placeholder={
            isMember ? "Type a message..." : "Join the group to chat"
          }
          onChange={(e) => {
            setMessageInput(e.target.value);
            if (e.target.value.trim()) sendTyping();
          }}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              handleSendMessage();
            }
          }}
        />
        <Button
          disabled={!connected || !isMember || !messageInput.trim()}
          onClick={handleSendMessage}
        >
          <Send size={18} />
        </Button>
      </div>
    </div>
  );
};
