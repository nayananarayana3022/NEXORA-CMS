
package com.nexora.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.entity.Movie;
import com.nexora.entity.Article;

import com.nexora.service.MovieService;
import com.nexora.service.ArticleService;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final MovieService movieService;
    private final ArticleService articleService;

    public PublicController(
            MovieService movieService,
            ArticleService articleService) {

        this.movieService = movieService;
        this.articleService = articleService;
    }

    // Get published movies
    @GetMapping("/movies")
    public List<Movie> getPublishedMovies() {
        return movieService.getPublishedMovies();
    }

    // Get published articles
    @GetMapping("/articles")
    public List<Article> getPublishedArticles() {
        return articleService.getPublishedArticles();
    }
}
