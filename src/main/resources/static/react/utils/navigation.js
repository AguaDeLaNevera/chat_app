import { clearToken } from "../api/chatApi";

export function redirectToLogin() {
    clearToken();
    window.location.href = "/login";
}