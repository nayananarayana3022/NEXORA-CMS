
const API_URL = "http://localhost:8080/api/dashboard/statistics";

// Get JWT token from browser storage
const token = localStorage.getItem("token");

// Redirect to login if token is missing
if (!token) {
    window.location.href = "login.html";
}

// Fetch dashboard statistics
async function loadDashboard() {

    try {

        const response = await fetch(API_URL, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("token");
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to load dashboard statistics");
        }

        const data = await response.json();

        document.getElementById("totalMovies").textContent =
            data.totalMovies ?? 0;

        document.getElementById("totalArticles").textContent =
            data.totalArticles ?? 0;

        document.getElementById("publishedMovies").textContent =
            data.publishedMovies ?? 0;

        document.getElementById("totalUsers").textContent =
            data.totalUsers ?? 0;

    } catch (error) {

        console.error("Dashboard Error:", error);
        alert("Unable to load dashboard data. Please check the server.");

    }
}

// Logout function
function logout() {
    localStorage.removeItem("token");
    window.location.href = "login.html";
}

// Load statistics when page opens
loadDashboard();
