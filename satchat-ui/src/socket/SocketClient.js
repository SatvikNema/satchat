import { Client } from "@stomp/stompjs";
import { webSocketUrl } from "../utils/GeneralConstants";
let instance = null;

class SocketClient {
  constructor(url, jwt) {
    if (!instance) {
      this.url = url;
      this.jwt = jwt;
      this.client = new Client();

      this.client.configure({
        brokerURL: url,
        connectHeaders: {
          Authorization: `Bearer ${jwt}`,
        },
        onConnect: () => {
          console.log("connected!");
        },
      });

      this.client.activate();
      instance = this;
    }
  }

  publish = ({ destination, body }) => {
    this.client.publish({
      destination: destination,
      body: JSON.stringify(body),
    });
  };

  deactivate = () => {
    this.client.deactivate();
  };

  getClientInstance = () => this.client;

  get connected() {
    return this.client.connected;
  }

  get jwt() {
    return this.jwt;
  }

  set jwt(value) {}
}

export default SocketClient;
