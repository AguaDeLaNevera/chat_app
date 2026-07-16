const GRAPHQL_URL = "/graphql";
const DEFAULT_KEYCLOAK_TOKEN_URL =
  "http://localhost:9090/realms/chat/protocol/openid-connect/token";

function getKeycloakTokenUrl() {
  return window.ChatConfig?.keycloakTokenUrl ?? DEFAULT_KEYCLOAK_TOKEN_URL;
}

export async function graphqlRequest(query, token, variables = {}, signal = undefined) {
  const headers = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(GRAPHQL_URL, {
    method: "POST",
    headers,
    body: JSON.stringify({ query, variables }),
    signal,
  });

  const payload = await response.json().catch(() => ({}));
  return { response, payload };
}

export async function keycloakPasswordGrant(username, password) {
  const response = await fetch(getKeycloakTokenUrl(), {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      grant_type: "password",
      client_id: "chat-app",
      username,
      password,
    }),
  });

  const payload = await response.json().catch(() => ({}));
  return { response, payload };
}

export function getToken() {
  return localStorage.getItem("token");
}

export function setToken(token) {
  localStorage.setItem("token", token);
}

export function clearToken() {
  localStorage.removeItem("token");
}

export function parseJwt(token) {
  const payload = token.split(".")[1] || "";
  const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
  const padded = base64 + "=".repeat((4 - (base64.length % 4)) % 4);
  return JSON.parse(atob(padded));
}
