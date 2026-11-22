
const MOVIE_API = "http://localhost:8080/api/public/movies";
const ARTICLE_API = "http://localhost:8080/api/public/articles";


// Load published movies
async function loadPublicMovies() {

    const container = document.getElementById("movieContainer");
    const message = document.getElementById("movieMessage");

    container.innerHTML = "";
    message.textContent = "Loading movies...";

    try {

        const response = await fetch(MOVIE_API);

        if (!response.ok) {
            throw new Error("Unable to load movies");
        }

        const movies = await response.json();

        message.textContent = "";

        if (movies.length === 0) {
            message.textContent = "No published movies available.";
            return;
        }

        movies.forEach(movie => {

            const movieCard = document.createElement("div");

            movieCard.className = "col-md-4";

            movieCard.innerHTML = `
                <div class="card h-100 shadow-sm">

                    <img src="${movie.posterUrl || 'https://placehold.co/400x250?text=Movie'}"
                         class="card-img-top"
                         style="height:250px; object-fit:cover;"
                         alt="Movie poster">

                    <div class="card-body">

                        <h5 class="card-title">${movie.title}</h5>

                        <p class="text-muted">${movie.genre || ""}</p>

                        <p>${movie.description || ""}</p>

                        <p>
                            <strong>Release Date:</strong>
                            ${movie.releaseDate || "N/A"}
                        </p>

                        ${movie.trailerUrl ?
                            `<a href="${movie.trailerUrl}"
                                target="_blank"
                                rel="noopener noreferrer"
                                class="btn btn-primary">
                                Watch Trailer
                            </a>` : ""}

                    </div>
                </div>
            `;

            container.appendChild(movieCard);

        });

    } catch (error) {

        message.textContent = error.message;
        console.error("Movie loading error:", error);

    }
}


// Load published articles
async function loadPublicArticles() {

    const container = document.getElementById("articleContainer");
    const message = document.getElementById("articleMessage");

    container.innerHTML = "";
    message.textContent = "Loading articles...";

    try {

        const response = await fetch(ARTICLE_API);

        if (!response.ok) {
            throw new Error("Unable to load articles");
        }

        const articles = await response.json();

        message.textContent = "";

        if (articles.length === 0) {
            message.textContent = "No published articles available.";
            return;
        }

        articles.forEach(article => {

            const articleCard = document.createElement("div");

            articleCard.className = "col-md-4";

            articleCard.innerHTML = `
                <div class="card h-100 shadow-sm">

                    <img src="${article.imageUrl || 'https://placehold.co/400x250?text=Article'}"
                         class="card-img-top"
                         style="height:250px; object-fit:cover;"
                         alt="Article image">

                    <div class="card-body">

                        <h5 class="card-title">${article.title}</h5>

                        <p class="text-muted">
                            ${article.category || ""}
                        </p>

                        <p>${article.author || ""}</p>

                        <p>${article.content || ""}</p>

                    </div>
                </div>
            `;

            container.appendChild(articleCard);

        });

    } catch (error) {

        message.textContent = error.message;
        console.error("Article loading error:", error);

    }
}


// Load public content when page opens
loadPublicMovies();
loadPublicArticles();
