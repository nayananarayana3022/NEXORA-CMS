
const API_URL = "https://nexora-cms-api-me1j.onrender.com/api/movies";
const token = localStorage.getItem("token");

// Get logged-in user's role from JWT
function getUserRole() {

    if (!token) {
        return null;
    }

    try {
        const payloadPart = token.split(".")[1];

        const normalized = payloadPart
            .replace(/-/g, "+")
            .replace(/_/g, "/");

        const padded = normalized.padEnd(
            Math.ceil(normalized.length / 4) * 4,
            "="
        );

        const payload = JSON.parse(atob(padded));

        return payload.role;

    } catch (error) {
        console.error("Unable to read user role:", error);
        return null;
    }
}

const userRole = getUserRole();

console.log("Logged-in Role:", userRole);
// Check whether user is logged in
if (!token) {
    window.location.href = "login.html";
}

// Common function to communicate with backend
async function apiRequest(url, method = "GET", body = null) {

    const options = {
        method: method,
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        }
    };

    if (body !== null) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(url, options);

    if (response.status === 401) {
        localStorage.removeItem("token");
        window.location.href = "login.html";
        return null;
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
        throw new Error(data?.message || "Something went wrong!");
    }

    return data;
}


// Load all movies
async function loadMovies() {

    const tableBody = document.getElementById("movieTableBody");
    const message = document.getElementById("tableMessage");

    try {

        const movies = await apiRequest(API_URL);

        if (movies === null) return;

        tableBody.innerHTML = "";

        if (movies.length === 0) {
            message.textContent = "No movies found.";
            return;
        }

        message.textContent = "";

        movies.forEach(movie => {

            const row = document.createElement("tr");

            const values = [
                movie.id,
                movie.title,
                movie.genre || "-",
                movie.releaseDate || "-",
                movie.status || "DRAFT"
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");

createMovieActionButtons(movie, actionCell);

row.appendChild(actionCell);
            tableBody.appendChild(row);

        });

    } catch (error) {

        message.textContent = error.message;
        console.error("Movie loading error:", error);

    }
}


// Show Add Movie Form
function showAddMovieForm() {

    document.getElementById("movieForm").reset();

    document.getElementById("movieId").value = "";

    document.getElementById("formTitle").textContent =
        "Add New Movie";

    document.getElementById("movieFormContainer").style.display =
        "block";
}


// Hide Movie Form
function hideMovieForm() {

    document.getElementById("movieFormContainer").style.display =
        "none";

}


// Add or Update Movie
document.getElementById("movieForm").addEventListener("submit",
    async function(event) {

        event.preventDefault();

        const id = document.getElementById("movieId").value;

        const movie = {

            title: document.getElementById("title").value,
            genre: document.getElementById("genre").value,
            releaseDate: document.getElementById("releaseDate").value || null,
            description: document.getElementById("description").value,
            posterUrl: document.getElementById("posterUrl").value,
            trailerUrl: document.getElementById("trailerUrl").value

        };

        try {

            let result;

            if (id) {

                result = await apiRequest(
                    API_URL + "/" + id,
                    "PUT",
                    movie
                );

            } else {

                movie.status = "DRAFT";

                result = await apiRequest(
                    API_URL,
                    "POST",
                    movie
                );

            }

            if (result === null) return;

            document.getElementById("movieMessage").textContent =
                id ? "Movie updated successfully!" : "Movie added successfully!";

            document.getElementById("movieMessage").style.color =
                "green";

            document.getElementById("movieForm").reset();
            document.getElementById("movieId").value = "";

            hideMovieForm();

            loadMovies();

        } catch (error) {

            document.getElementById("movieMessage").textContent =
                error.message;

            document.getElementById("movieMessage").style.color =
                "red";

        }

    }
);


// Edit Movie
async function editMovie(id) {

    try {

        const movie = await apiRequest(API_URL + "/" + id);

        if (movie === null) return;

        document.getElementById("movieId").value = movie.id;
        document.getElementById("title").value = movie.title || "";
        document.getElementById("genre").value = movie.genre || "";
        document.getElementById("releaseDate").value = movie.releaseDate || "";
        document.getElementById("description").value = movie.description || "";
        document.getElementById("posterUrl").value = movie.posterUrl || "";
        document.getElementById("trailerUrl").value = movie.trailerUrl || "";

        document.getElementById("formTitle").textContent =
            "Edit Movie";

        document.getElementById("movieFormContainer").style.display =
            "block";

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        alert(error.message);

    }

}


// Delete Movie
async function deleteMovie(id) {

    const confirmation = confirm(
        "Are you sure you want to delete this movie?"
    );

    if (!confirmation) return;

    try {

        await apiRequest(API_URL + "/" + id, "DELETE");

        alert("Movie deleted successfully!");

        loadMovies();

    } catch (error) {

        alert(error.message);

    }

}


// Search Movies
async function searchMovies() {

    const title = document.getElementById("searchMovie").value.trim();

    if (!title) {
        loadMovies();
        return;
    }

    try {

        const movies = await apiRequest(
            API_URL + "/search?title=" + encodeURIComponent(title)
        );

        if (movies === null) return;

        const tableBody = document.getElementById("movieTableBody");
        const message = document.getElementById("tableMessage");

        tableBody.innerHTML = "";

        if (movies.length === 0) {
            message.textContent = "No matching movies found.";
            return;
        }

        message.textContent = "";

        movies.forEach(movie => {

            const row = document.createElement("tr");

            [
                movie.id,
                movie.title,
                movie.genre || "-",
                movie.releaseDate || "-",
                movie.status || "DRAFT"
            ].forEach(value => {

                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);

            });

            const actionCell = document.createElement("td");

createMovieActionButtons(movie, actionCell);

row.appendChild(actionCell);
tableBody.appendChild(row);

        });

    } catch (error) {

        alert(error.message);

    }

}


// Logout
function logout() {

    localStorage.removeItem("token");

    window.location.href = "login.html";

}

// Submit movie for approval
async function submitMovie(id) {
    try {
        await apiRequest(API_URL + "/" + id + "/submit", "PUT");

        alert("Movie submitted for approval!");

        loadMovies();

    } catch (error) {
        alert(error.message);
    }
}


// Approve movie
async function approveMovie(id) {
    try {
        await apiRequest(API_URL + "/" + id + "/approve", "PUT");

        alert("Movie approved successfully!");

        loadMovies();

    } catch (error) {
        alert(error.message);
    }
}


// Reject movie
async function rejectMovie(id) {

    if (!confirm("Are you sure you want to reject this movie?")) {
        return;
    }

    try {
        await apiRequest(API_URL + "/" + id + "/reject", "PUT");

        alert("Movie rejected!");

        loadMovies();

    } catch (error) {
        alert(error.message);
    }
}
function createMovieActionButtons(movie, actionCell) {

    console.log("ACTION FUNCTION RUNNING");
    
    console.log("Role:", userRole);
console.log("Movie:", movie.title, "| Status:", movie.status);       

    const payload = JSON.parse(
        atob(token.split(".")[1]
            .replace(/-/g, "+")
            .replace(/_/g, "/"))
    );

    const email = payload.sub;
    const isOwner = movie.createdBy === email;

    // Edit button: Admin or the movie's Author
    if (userRole === "ADMIN" ||
    (userRole === "AUTHOR" && isOwner &&
     (movie.status === "DRAFT" || movie.status === "REJECTED"))) {

        const editButton = document.createElement("button");
        editButton.textContent = "Edit";
        editButton.className = "action-btn edit-btn";
        editButton.onclick = () => editMovie(movie.id);

        actionCell.appendChild(editButton);
    }

    // Submit button: Admin or movie's Author
if ((userRole === "ADMIN" ||
    (userRole === "AUTHOR" && isOwner)) &&
    (movie.status === "DRAFT" || movie.status === "REJECTED")) {

    const submitButton = document.createElement("button");

    submitButton.textContent = "Submit";
    submitButton.className = "action-btn";
    submitButton.style.background = "#0d6efd";
    submitButton.style.color = "white";

    submitButton.onclick = () => submitMovie(movie.id);

    actionCell.appendChild(submitButton);
    console.log("SUBMIT BUTTON ADDED:", movie.title);
}

    // Delete button: Admin only
    if (userRole === "ADMIN") {

        const deleteButton = document.createElement("button");
        deleteButton.textContent = "Delete";
        deleteButton.className = "action-btn delete-btn";
        deleteButton.onclick = () => deleteMovie(movie.id);

        actionCell.appendChild(deleteButton);
    }


    // Approve and Reject: Admin or Editor
if ((userRole === "ADMIN" || userRole === "EDITOR") &&
    movie.status === "PENDING") {

    const approveButton = document.createElement("button");
    approveButton.textContent = "Approve";
    approveButton.className = "action-btn";
    approveButton.style.background = "green";
    approveButton.style.color = "white";
    approveButton.onclick = () => approveMovie(movie.id);
    actionCell.appendChild(approveButton);

    const rejectButton = document.createElement("button");
    rejectButton.textContent = "Reject";
    rejectButton.className = "action-btn";
    rejectButton.style.background = "red";
    rejectButton.style.color = "white";
    rejectButton.onclick = () => rejectMovie(movie.id);
    actionCell.appendChild(rejectButton);
}
}
// Load movies when page opens
loadMovies();

