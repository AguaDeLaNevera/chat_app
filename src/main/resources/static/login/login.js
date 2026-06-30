const BASE_URL = "http://localhost:8080/chat";

async function login(){

    const username = document.getElementById("username").value.trim();

    const password = document.getElementById("password").value;

    if(username.length===0 || password.length===0){

        document.getElementById("error").innerText =
            "Fill every field";

        return;
    }

    try{

        const response = await fetch(BASE_URL + "/login",{

            method:"POST",

            headers:{
                "Content-Type":"application/json"
            },

            body:JSON.stringify({

                username,
                password

            })

        });

        if(!response.ok){

            document.getElementById("error").innerText =
                "Wrong username or password";

            return;
        }

        const data = await response.json();

        localStorage.setItem("token",data.token);

        window.location.href="index.html";

    }
    catch(e){

        document.getElementById("error").innerText =
            "Cannot connect to server";

    }

}