import React, { useState, useContext, useEffect } from "react";
import Button from "./Button";
import ChatView from "./ChatView";
import UserContext from "../context/UserContext";
import { BASE_URL } from "../utils/GeneralConstants";
import backendClient from "../utils/BackendClient";

const FriendView = () => {
  const { username, token } = useContext(UserContext);
  const [friendList, setFriendList] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);

  useEffect(() => {
    const loadFriends = async (username) => {
      const data = await backendClient.getFriends(username, token);
      setFriendList(data);
    };

    loadFriends(username);
  }, []);

  return (
    <div>
      FriendView
      {friendList.length > 0 &&
        friendList.map((friend, idx) => (
          <div key={idx}>
            <Button
              onClick={() => setSelectedFriend(friend)}
              displayText={`Chat with ${friend.connectionUsername}`}
            />
          </div>
        ))}
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
