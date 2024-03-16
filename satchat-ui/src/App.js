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
    const jsonPayload = await backendClient.login({
      username,
      password,
    });
    const { id, username: responseUsername, email, token } = jsonPayload;
    backendClient.jwt = token;

    const socketClient = new SocketClient(webSocketUrl, token);
    await socketClient.awaitConnect();
    setSocketClientContext({ socketClient });
    setContext({ id, username: responseUsername, email, token });
  };

  return (
    <UserContext.Provider value={context}>
      <SocketClientContext.Provider value={socketClientContext}>
        {context && context.username ? (
          <div>
            <div>
              <h3>Currently logged in: {context.username}</h3>
            </div>
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
