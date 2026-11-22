
const API_URL = "http://localhost:8080/api/articles";

const token = localStorage.getItem("token");

// Check login
if (!token) {
    window.location.href = "login.html";
}

// API request function
async function apiRequest(url, method = "GET", body = null) {

    const options = {
        method: method,
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(url, options);

    if (response.status === 401 || response.status === 403) {
        if (response.status === 401) {
            localStorage.removeItem("token");
            window.location.href = "login.html";
        }

        throw new Error(
            response.status === 403
                ? "You are not allowed to perform this action!"
                : "Session expired. Please login again."
        );
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
        throw new Error(data?.message || "Something went wrong!");
    }

    return data;
}

async function loadArticles() {

    const tableBody = document.getElementById("articleTableBody");
    const tableMessage = document.getElementById("tableMessage");

    tableBody.innerHTML = "";
    tableMessage.textContent = "Loading articles...";

    try {

        const articles = await apiRequest(API_URL);

        tableMessage.textContent = "";

        if (articles.length === 0) {
            tableMessage.textContent = "No articles found.";
            return;
        }

        articles.forEach(article => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${article.id}</td>
                <td>${article.title}</td>
                <td>${article.category || ""}</td>
                <td>${article.author || ""}</td>
                <td>${article.status || ""}</td>
            `;
const actionCell = document.createElement("td");

createArticleActionButtons(article, actionCell);

row.appendChild(actionCell);
tableBody.appendChild(row);

        });

    } catch (error) {

        tableMessage.textContent = error.message;
        console.error("Article loading error:", error);

    }
}


// Get logged-in user's role
function getUserRole() {

    if (!token) return null;

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


// Create article action buttons
function createArticleActionButtons(article, actionCell) {

    const payloadPart = token.split(".")[1];

    const normalized = payloadPart
        .replace(/-/g, "+")
        .replace(/_/g, "/");

    const padded = normalized.padEnd(
        Math.ceil(normalized.length / 4) * 4,
        "="
    );

    const payload = JSON.parse(atob(padded));

    const email = payload.sub;

    const isOwner = article.createdBy === email;


    // Edit button
    if (userRole === "ADMIN" ||
        (userRole === "AUTHOR" && isOwner)) {

        const editButton = document.createElement("button");

        editButton.textContent = "Edit";
        editButton.className = "action-btn edit-btn";
        editButton.onclick = () => editArticle(article.id);

        actionCell.appendChild(editButton);
    }


    // Delete button: Admin only
    if (userRole === "ADMIN") {

        const deleteButton = document.createElement("button");

        deleteButton.textContent = "Delete";
        deleteButton.className = "action-btn delete-btn";
        deleteButton.onclick = () => deleteArticle(article.id);

        actionCell.appendChild(deleteButton);
    }


    // Submit button
    if ((userRole === "ADMIN" || (userRole === "AUTHOR" && isOwner)) &&
        (article.status === "DRAFT" || article.status === "REJECTED")) {

        const submitButton = document.createElement("button");

        submitButton.textContent = "Submit";
        submitButton.className = "action-btn submit-btn";
        submitButton.onclick = () => submitArticle(article.id);

        actionCell.appendChild(submitButton);
    }


    // Approve and Reject: Admin or Editor
    if ((userRole === "ADMIN" || userRole === "EDITOR") &&
        article.status === "PENDING") {

        const approveButton = document.createElement("button");

        approveButton.textContent = "Approve";
        approveButton.className = "action-btn";
        approveButton.style.background = "green";
        approveButton.style.color = "white";
        approveButton.onclick = () => approveArticle(article.id);

        actionCell.appendChild(approveButton);


        const rejectButton = document.createElement("button");

        rejectButton.textContent = "Reject";
        rejectButton.className = "action-btn delete-btn";
        rejectButton.onclick = () => rejectArticle(article.id);

        actionCell.appendChild(rejectButton);
    }
}


// Load articles when page opens
loadArticles();


async function submitArticle(id) {

    try {
        await apiRequest(API_URL + "/" + id + "/submit", "PUT");

        alert("Article submitted for approval!");

        loadArticles();

    } catch (error) {
        alert(error.message);
    }
}


async function approveArticle(id) {

    try {
        await apiRequest(API_URL + "/" + id + "/approve", "PUT");

        alert("Article approved successfully!");

        loadArticles();

    } catch (error) {
        alert(error.message);
    }
}


async function rejectArticle(id) {

    if (!confirm("Are you sure you want to reject this article?")) {
        return;
    }

    try {
        await apiRequest(API_URL + "/" + id + "/reject", "PUT");

        alert("Article rejected!");

        loadArticles();

    } catch (error) {
        alert(error.message);
    }
}

async function editArticle(id) {

    try {

        const article = await apiRequest(API_URL + "/" + id);

        document.getElementById("articleId").value = article.id;
        document.getElementById("title").value = article.title;
        document.getElementById("category").value = article.category;
        document.getElementById("author").value = article.author;
        document.getElementById("content").value = article.content;
        document.getElementById("imageUrl").value = article.imageUrl || "";

        document.getElementById("formHeading").textContent = "Edit Article";

        document.getElementById("articleFormContainer").style.display = "block";

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {
        alert(error.message);
    }
}


async function deleteArticle(id) {

    if (!confirm("Are you sure you want to delete this article?")) {
        return;
    }

    try {

        await apiRequest(API_URL + "/" + id, "DELETE");

        alert("Article deleted successfully!");

        loadArticles();

    } catch (error) {
        alert(error.message);
    }
}

function showAddArticleForm() {

    document.getElementById("articleForm").reset();

    document.getElementById("articleId").value = "";

    document.getElementById("formHeading").textContent =
        "Add New Article";

    document.getElementById("articleFormContainer").style.display =
        "block";
}


function hideArticleForm() {

    document.getElementById("articleFormContainer").style.display =
        "none";

    document.getElementById("articleForm").reset();
}


document.getElementById("articleForm").addEventListener(
    "submit",
    async function(event) {

        event.preventDefault();

        const id = document.getElementById("articleId").value;

        const article = {
            title: document.getElementById("title").value,
            category: document.getElementById("category").value,
            author: document.getElementById("author").value,
            content: document.getElementById("content").value,
            imageUrl: document.getElementById("imageUrl").value,
            status: "DRAFT"
        };

        try {

            if (id) {

                await apiRequest(API_URL + "/" + id, "PUT", article);

                alert("Article updated successfully!");

            } else {

                await apiRequest(API_URL, "POST", article);

                alert("Article added successfully!");

            }

            hideArticleForm();

            loadArticles();

        } catch (error) {

            alert(error.message);

        }
    }
);

async function searchArticles() {

    const title = document.getElementById("searchArticle").value.trim();

    if (!title) {
        loadArticles();
        return;
    }

    const tableBody = document.getElementById("articleTableBody");
    const tableMessage = document.getElementById("tableMessage");

    tableBody.innerHTML = "";
    tableMessage.textContent = "Searching...";

    try {

        const articles = await apiRequest(
            API_URL + "/search?title=" + encodeURIComponent(title)
        );

        tableMessage.textContent = "";

        if (articles.length === 0) {
            tableMessage.textContent = "No matching articles found.";
            return;
        }

        articles.forEach(article => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${article.id}</td>
                <td>${article.title}</td>
                <td>${article.category || ""}</td>
                <td>${article.author || ""}</td>
                <td>${article.status || ""}</td>
            `;

            const actionCell = document.createElement("td");

            createArticleActionButtons(article, actionCell);

            row.appendChild(actionCell);
            tableBody.appendChild(row);

        });

    } catch (error) {

        tableMessage.textContent = error.message;
        console.error("Article search error:", error);

    }
}


// Logout
function logout() {

    localStorage.removeItem("token");

    window.location.href = "login.html";
}
