import { BASE_URL, LOGIN_URL } from "./GeneralConstants";

class BackendClient {
  constructor() {
    this.jwt = "";
  }

  set jwt(token) {}

  getFriends = async (username, jwt) => {
    const url = `${BASE_URL}/${username}/friends`;
    return this.sendRequest(
      url,
      {
        method: "GET",
      },
      jwt
    );
  };

  login = async (loginRequestPayload, jwt) => {
    return this.sendRequest(
      LOGIN_URL,
      {
        method: "POST",
        body: JSON.stringify(loginRequestPayload),
        headers: {
          "content-type": "application/json",
        },
      },
      jwt
    );
  };

  sendRequest = async (url, apiConfigs, jwt) => {
    let { headers } = apiConfigs;
    if (!headers) {
      headers = {};
    }
    headers["Authorization"] = `Bearer ${jwt}`;
    apiConfigs.headers = headers;

    const response = await fetch(url, apiConfigs);
    return await response.json();
  };
}

let backendClient = new BackendClient();

export default backendClient;
