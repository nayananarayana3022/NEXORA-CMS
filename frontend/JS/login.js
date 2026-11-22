
const API_URL = "http://localhost:8080/api/users/login";

document.getElementById("loginForm").addEventListener("submit", async function(event) {

    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const message = document.getElementById("message");

    try {

        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {

            localStorage.setItem("token", data.token);

            message.style.color = "green";
            message.textContent = "Login successful!";

            window.location.href = "dashboard.html";

        } else {

            message.style.color = "red";
            message.textContent = data.message || "Invalid email or password!";

        }

    } catch (error) {

        message.style.color = "red";
        message.textContent = "Unable to connect to server!";

        console.error(error);
    }

});
