// /chat for REST API
const BASE_URL = "http://localhost:8080/graphql";

async function register() {

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    if(username.length === 0 || password.length === 0){
        document.getElementById("error").innerText =
            "Fill every field";
        return;
    }

    try{
        // /register for REST API
        const response = await fetch(BASE_URL,{

            method:"POST",

            headers:{
                "Content-Type":"application/json"
            },

            body:JSON.stringify({
                query: `
                mutation {
                    register(
                        username: "${username}",
                        password: "${password}"
                    ){
                        id
                        username
                    }
                }
                `
            })

        });

        if(!response.ok){

            const text = await response.text();

            document.getElementById("error").innerText = text;

            return;
        }

        alert("Account created!");

        window.location.href = "login.html";

    }
    catch(e){

        document.getElementById("error").innerText =
            "Could not connect to server";

    }

}