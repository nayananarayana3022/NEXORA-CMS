package com.nexora.service;

import org.springframework.stereotype.Service;

import com.nexora.repository.MovieRepository;
import com.nexora.repository.ArticleRepository;
import com.nexora.repository.UserRepository;

import java.util.Map;

@Service
public class DashboardService {

    private final MovieRepository movieRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public DashboardService(
            MovieRepository movieRepository,
            ArticleRepository articleRepository,
            UserRepository userRepository) {

        this.movieRepository = movieRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Long> getDashboardStatistics() {

        long totalMovies = movieRepository.count();
        long totalArticles = articleRepository.count();
        long totalUsers = userRepository.count();

        long publishedMovies =
                movieRepository.countByStatus("PUBLISHED");

        long publishedArticles =
                articleRepository.countByStatus("PUBLISHED");

        long draftMovies =
                movieRepository.countByStatus("DRAFT");

        long draftArticles =
                articleRepository.countByStatus("DRAFT");

        return Map.of(
                "totalMovies", totalMovies,
                "totalArticles", totalArticles,
                "totalUsers", totalUsers,
                "publishedMovies", publishedMovies,
                "publishedArticles", publishedArticles,
                "draftMovies", draftMovies,
                "draftArticles", draftArticles
        );
    }
}
