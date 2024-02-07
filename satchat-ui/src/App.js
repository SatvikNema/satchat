import { useState } from "react";
import FriendView from "./components/FriendView";
import UserSelection from "./components/UserSelection";
import UserContext from "./context/UserContext";
import { LOGIN_URL, webSocketUrl } from "./utils/GeneralConstants";
import backendClient from "./utils/BackendClient";
import SocketClientContext from "./context/SocketClientContext";
import SocketClient from "./socket/SocketClient";

function App() {
  const [context, setContext] = useState({});
  const [socketClientContext, setSocketClientContext] = useState({});

  const loginUser = async ({ username }) => {
    const password = "password"; // todo load this from user input
    // login user and get the jwt
    const requestPayload = JSON.stringify({
      username,
      password,
    });
    const response = await fetch(LOGIN_URL, {
      method: "POST",
      body: requestPayload,
      headers: {
        "content-type": "application/json",
      },
    });

    const jsonPayload = await response.json();
    const { id, username: responseUsername, email, token } = jsonPayload;
    backendClient.jwt = token;
    setContext({ id, username: responseUsername, email, token });

    const socketClient = new SocketClient(webSocketUrl, token);
    setSocketClientContext({ socketClient });
  };

  return (
    <UserContext.Provider value={context}>
      <SocketClientContext.Provider value={socketClientContext}>
        {context && context.username ? (
          <div>
            Currently logged in: {context.username}
            <br />
            <FriendView />
          </div>
        ) : (
          <UserSelection onUserContextSet={loginUser} />
        )}
      </SocketClientContext.Provider>
    </UserContext.Provider>
  );
}

export default App;
