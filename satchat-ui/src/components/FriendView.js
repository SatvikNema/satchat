import React, { useState, useEffect } from "react";
import Button from "./Button";
import ChatView from "./ChatView";
import backendClient from "../utils/BackendClient";

const FriendView = ({ unSeenMessages }) => {
  const [friendList, setFriendList] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);
  const [friendUnSeenMessages, setFriendUnSeenMessages] = useState([]);

  const handleSelectedFriend = (friend, apiFriendUnSeenMessages) => {
    setSelectedFriend(friend);
    setFriendUnSeenMessages(apiFriendUnSeenMessages);

    backendClient.setReadMessages(apiFriendUnSeenMessages);
  };

  useEffect(() => {
    const loadFriends = async () => {
      const data = await backendClient.getFriends();
      setFriendList(data);
    };

    loadFriends();
  }, []);

  return (
    <div>
      FriendView
      {friendList.length > 0 &&
        friendList.map((friend, idx) => {
          const connectionUsername = friend.connectionUsername;
          let displayText = `Chat with ${connectionUsername}`;
          let apiFriendUnSeenMessages = [];
          if (connectionUsername in unSeenMessages) {
            apiFriendUnSeenMessages = unSeenMessages[connectionUsername];
            displayText += ` (${apiFriendUnSeenMessages.length})`;
          }
          return (
            <div key={idx}>
              <Button
                onClick={() => {
                  handleSelectedFriend(friend, apiFriendUnSeenMessages);
                }}
                displayText={displayText}
              />
            </div>
          );
        })}
      {selectedFriend && (
        <div>
          <br />
          <ChatView
            friend={selectedFriend}
            unSeenMessages={friendUnSeenMessages}
          ></ChatView>{" "}
        </div>
      )}
    </div>
  );
};

export default FriendView;
