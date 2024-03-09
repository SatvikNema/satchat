import React, { useContext, useState, useEffect } from "react";
import TextInput from "./TextInput";
import Button from "./Button";
import SocketClientContext from "../context/SocketClientContext";
import backendClient from "../utils/BackendClient";

const ChatView = ({ friend }) => {
  const { connectionId, connectionUsername, convId } = friend;
  const obj = useContext(SocketClientContext);
  const { socketClient: client } = obj;
  const [messages, setMessages] = useState({});
  const [userMessage, setUserMessage] = useState("");

  useEffect(() => {
    let subscription = client.subscribe(`/topic/${convId}`, (message) => {
      setMessages((prev) => {
        const friendsMessages = prev[connectionId] || [];

        const newMessages = [...friendsMessages, JSON.parse(message.body)];
        const newObj = { ...prev, [connectionId]: newMessages };
        return newObj;
      });
    });

    const getUnseenMessages = async () => {
      const apiResponse = await backendClient.getUnseenMessages(connectionId);
      setMessages((prev) => {
        const friendsMessages = prev[connectionId] || [];

        const newMessages = [...friendsMessages, ...apiResponse];
        const newObj = { ...prev, [connectionId]: newMessages };
        return newObj;
      });
      backendClient.setReadMessages(apiResponse);
    };

    getUnseenMessages();

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, [connectionId]);

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
      <div>Chatting with {connectionUsername}</div>
      {messages[connectionId] &&
        messages[connectionId].length > 0 &&
        messages[connectionId].map((message, idx) => {
          if (message.messageType == "CHAT") {
            return (
              <div key={idx}>
                {message.senderUsername}: {message.content}
              </div>
            );
          } else {
            return (
              <div key={idx}>
                {message.senderUsername}: (new) {message.content}
              </div>
            );
          }
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
