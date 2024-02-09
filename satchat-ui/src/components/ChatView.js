import React, { useContext, useState, useEffect } from "react";
import TextInput from "./TextInput";
import Button from "./Button";
import SocketClientContext from "../context/SocketClientContext";

let subscription;
const ChatView = ({ friend, unSeenMessages }) => {
  const { connectionId, connectionUsername, convId } = friend;
  const obj = useContext(SocketClientContext);
  const { socketClient: client } = obj;
  const [messages, setMessages] = useState([]);
  const [userMessage, setUserMessage] = useState("");
  if (unSeenMessages && unSeenMessages.length > 0) {
    console.log("wat");
  }

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
      },
    });
    setUserMessage("");
  };

  return (
    <div>
      ChatView
      {unSeenMessages && unSeenMessages.length > 0 && (
        // <div>There are some unseen messages</div>
        <div>
          unseen =======
          {unSeenMessages.map((message, idx) => {
            return (
              <div key={idx}>
                {message.senderUsername}: {message.content}
              </div>
            );
          })}
          =======
        </div>
      )}
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
