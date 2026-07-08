// /chat for REST API
const BASE_URL = "http://localhost:8080/graphql";

async function login() {

    const username = document.getElementById("username").value.trim();

    const password = document.getElementById("password").value;

    if (username.length === 0 || password.length === 0) {

        document.getElementById("error").innerText =
            "Fill every field";

        return;
    }

    try {
        // /login for REST API
        const response = await fetch("http://localhost:9090/realms/chat/protocol/openid-connect/token", {

            method: "POST",

            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },

            body: new URLSearchParams({
                grant_type: "password",
                client_id: "chat-app",
                username: username,
                password: password
            })
        });

        if (!response.ok) {

            document.getElementById("error").innerText =
                "Wrong username or password";

            return;
        }

        const data = await response.json();

        console.log(data);
        localStorage.setItem("token", data.access_token);

        window.location.href = "index.html";

    } catch (e) {

        document.getElementById("error").innerText =
            "Cannot connect to server";

    }

}