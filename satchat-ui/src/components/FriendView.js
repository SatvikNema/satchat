import React, { useState, useEffect, useContext } from "react";
import Button from "./Button";
import ChatView from "./ChatView";
import backendClient from "../utils/BackendClient";
import SocketClientContext from "../context/SocketClientContext";
import UserContext from "../context/UserContext";

const FriendView = () => {
  const [friendList, setFriendList] = useState([]);
  const [selectedFriend, setSelectedFriend] = useState(null);
  const { id: userId, username } = useContext(UserContext);
  const { socketClient } = useContext(SocketClientContext);

  const handleSelectedFriend = (friend) => {
    setSelectedFriend(friend);
    setFriendList((prev) => {
      for (let d of prev) {
        if (d.connectionId === friend.connectionId) {
          d.unSeen = 0;
        }
      }
      return [...prev];
    });
  };

  useEffect(() => {
    let data = [];
    const loadFriends = async () => {
      data = await backendClient.getFriends();
      const apiResponse = await backendClient.getUnseenMessages();
      if (apiResponse && apiResponse.length > 0) {
        apiResponse.forEach((r) => {
          for (let d of data) {
            if (d.connectionId === r.fromUser) {
              d.unSeen = r.count;
            }
          }
        });
      }

      setFriendList(data);
    };

    loadFriends();

    let subscription = socketClient.subscribe(
      `/topic/${userId}`,

      (message) => {
        console.log(message);
        const messageBody = JSON.parse(message.body);
        if (
          messageBody.messageType === "CHAT" ||
          messageBody.messageType === "UNSEEN"
        ) {
          const { senderId } = messageBody;
          setFriendList((prev) => {
            for (let d of prev) {
              if (d.connectionId === senderId) {
                d.unSeen += 1;
              }
            }
            return [...prev];
          });
        } else if (messageBody.messageType === "FRIEND_OFFLINE") {
          // do offline shit
          setFriendList((prev) => {
            for (let d of prev) {
              if (d.connectionId === messageBody.userConnection.connectionId) {
                d.isOnline = false;
                break;
              }
            }
            return [...prev];
          });
        } else if (messageBody.messageType === "FRIEND_ONLINE") {
          setFriendList((prev) => {
            for (let d of prev) {
              if (d.connectionId === messageBody.userConnection.connectionId) {
                d.isOnline = true;
                break;
              }
            }
            return [...prev];
          });
        }
      },
      "CHAT",
      "UNSEEN",
      "FRIEND_ONLINE",
      "FRIEND_OFFLINE"
    );

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, []);

  return (
    <div>
      {friendList.length > 0 &&
        friendList.map((friend, idx) => {
          let count =
            friend.unSeen && friend.unSeen > 0 ? `(${friend.unSeen})` : "";
          let onlineStatusText = friend.isOnline ? "(online)" : "";
          let displayText = `${onlineStatusText} Chat with ${friend.connectionUsername} ${count}`;
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
