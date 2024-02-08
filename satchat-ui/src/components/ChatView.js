import React, { useContext, useRef, useState, useEffect } from "react";
import TextInput from "./TextInput";
import Button from "./Button";
import UserContext from "../context/UserContext";
import SocketClientContext from "../context/SocketClientContext";

let subscription;
const ChatView = ({ friend }) => {
  const { connectionId, connectionUsername, convId } = friend;
  const { username: currentUserUsername, id: currentUserId } =
    useContext(UserContext);
  const obj = useContext(SocketClientContext);
  const { socketClient: client } = obj;
  const [messages, setMessages] = useState([]);
  const [userMessage, setUserMessage] = useState("");

  useEffect(() => {
    if (client && client.connected) {
      if (subscription) {
        subscription.unsubscribe();
      }
      subscription = client
        .getClientInstance()
        .subscribe(`/topic/${convId}`, (message) => {
          setMessages((prev) => {
            const newMessages = [...prev, JSON.parse(message.body)];
            return newMessages;
          });
        });
      console.log("subscribed to " + convId);
    }
  }, []);

  const onInputChange = (e) => {
    setUserMessage(e.target.value);
  };

  const sendUserMessage = (message) => {
    client.publish({
      destination: `/app/chat/sendMessage/${convId}`,
      body: {
        messageType: "CHAT",
        content: message,
        receiverId: connectionId,
        receiverUsername: connectionUsername,
        senderId: currentUserId,
        senderUsername: currentUserUsername,
      },
    });
    setUserMessage("");
  };

  return (
    <div>
      ChatView
      {messages.length > 0 &&
        messages
          .filter((message) => message.messageType == "CHAT")
          .map((message, idx) => {
            return (
              <div key={idx}>
                {message.senderUsername}: {message.content}
              </div>
            );
          })}
      <TextInput
        id="userMessage"
        labelText="User Message"
        onChange={onInputChange}
        value={userMessage}
      ></TextInput>
      <Button
        onClick={() => sendUserMessage(userMessage)}
        displayText="Send!"
      />
    </div>
  );
};

export default ChatView;
