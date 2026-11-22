
const REGISTER_API = "https://nexora-cms-api-me1j.onrender.com/api/users/register";

document.getElementById("registerForm").addEventListener(
    "submit",
    async function(event) {

        event.preventDefault();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        const message = document.getElementById("message");

        // Check password confirmation
        if (password !== confirmPassword) {

            message.style.color = "red";
            message.textContent = "Passwords do not match!";

            return;
        }

        const user = {
            name: name,
            email: email,
            password: password
        };

        try {

            const response = await fetch(REGISTER_API, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(user)

            });

            const data = await response.json();

            if (response.ok) {

                message.style.color = "green";

                message.textContent =
                    "Registration successful! Redirecting to login...";

                document.getElementById("registerForm").reset();

                setTimeout(() => {
                    window.location.href = "login.html";
                }, 2000);

            } else {

                message.style.color = "red";

                message.textContent =
                    data.message || "Registration failed!";

            }

        } catch (error) {

            message.style.color = "red";

            message.textContent =
                "Unable to connect to server!";

            console.error("Registration error:", error);

        }

    }
);
