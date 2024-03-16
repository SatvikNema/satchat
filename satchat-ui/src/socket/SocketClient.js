import { Client } from "@stomp/stompjs";
class SocketClient {
  constructor(url, jwt) {
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

  subscribe = (topic, callback) => {
    return this.client.subscribe(topic, (message) => {
      callback(message);
    });
  };

  awaitConnect = async (awaitConnectConfig) => {
    const {
      retries = 3,
      curr = 0,
      timeinterval = 100,
    } = awaitConnectConfig || {};
    return new Promise((resolve, reject) => {
      console.log(timeinterval);
      setTimeout(() => {
        if (this.connected) {
          resolve();
        } else {
          console.log("failed to connect! retrying");
          if (curr >= retries) {
            console.log("failed to connect within the specified time interval");
            reject();
          }
          this.awaitConnect({ ...awaitConnectConfig, curr: curr + 1 });
        }
      }, timeinterval);
    });
  };

  get connected() {
    return this.client.connected;
  }

  get jwt() {
    return this.jwt;
  }

  set jwt(value) {}
}

export default SocketClient;
