import React, { useState, useEffect } from "react";
import Button from "./Button";
import ChatView from "./ChatView";
import backendClient from "../utils/BackendClient";

const FriendView = () => {
  const [friendList, setFriendList] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);

  const handleSelectedFriend = (friend) => {
    setSelectedFriend(friend);
    setFriendList((prev) => {
      for (let d of prev) {
        if (d.connectionId == friend.connectionId) {
          d.unSeen = 0;
        }
      }
      return [...prev];
    });
  };

  useEffect(() => {
    const loadFriends = async () => {
      console.log("call made to get friends");
      const data = await backendClient.getFriends();
      const apiResponse = await backendClient.getUnseenMessages();
      if (apiResponse && apiResponse.length > 0) {
        apiResponse.forEach((r) => {
          data.filter((data) => data.connectionId == r.fromUser);
          for (let d of data) {
            if (d.connectionId == r.fromUser) {
              d.unSeen = r.count;
            }
          }
        });
      }
      setFriendList(data);
    };

    loadFriends();
  }, []);

  return (
    <div>
      FriendView
      {friendList.length > 0 &&
        friendList.map((friend, idx) => {
          let count =
            friend.unSeen && friend.unSeen > 0 ? `(${friend.unSeen})` : "";
          let displayText = `Chat with ${friend.connectionUsername} ${count}`;
          return (
            <div key={idx}>
              <Button
                onClick={() => {
                  handleSelectedFriend(friend);
                }}
                displayText={displayText}
              />
            </div>
          );
        })}
      {selectedFriend && (
        <div>
          <br />
          <ChatView friend={selectedFriend}></ChatView>{" "}
        </div>
      )}
    </div>
  );
};

export default FriendView;
