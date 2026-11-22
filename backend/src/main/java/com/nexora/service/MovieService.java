package com.nexora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nexora.entity.Movie;
import com.nexora.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Add a movie
    public Movie addMovie(Movie movie) {

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        movie.setCreatedBy(email);

        return movieRepository.save(movie);
    }

    // Get all movies
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Get movie by ID
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    // Update a movie

public Movie updateMovie(Long id, Movie updatedMovie) {

    Movie existingMovie = getMovieById(id);

    if (existingMovie == null) {
        return null;
    }

    // Get the logged-in user's email
    org.springframework.security.core.Authentication authentication =
            org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String email = authentication.getName();

    // Check whether the logged-in user is ADMIN
    boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority ->
                    authority.getAuthority().equals("ROLE_ADMIN"));

    // Allow only ADMIN or the movie's creator to update
    if (!isAdmin && !email.equals(existingMovie.getCreatedBy())) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN,
                "You are not allowed to update this movie!"
        );
    }

    existingMovie.setTitle(updatedMovie.getTitle());
    existingMovie.setGenre(updatedMovie.getGenre());
    existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
    existingMovie.setDescription(updatedMovie.getDescription());
    existingMovie.setPosterUrl(updatedMovie.getPosterUrl());
    existingMovie.setTrailerUrl(updatedMovie.getTrailerUrl());
    

    return movieRepository.save(existingMovie);
}


    // Delete a movie
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
    

    public List<Movie> searchMoviesByTitle(String title) {
    	return movieRepository.findByTitleContainingIgnoreCase(title);
}

    public List<Movie> filterMoviesByGenre(String genre) {
    	return movieRepository.findByGenreIgnoreCase(genre);
}

    public Movie submitMovieForApproval(Long id) {

        Movie movie = getMovieById(id);

        if (movie == null) {
            return null;
        }

        // Get logged-in user's details
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        // Check whether the user is ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        // Only ADMIN or the movie creator can submit
        if (!isAdmin && !email.equals(movie.getCreatedBy())) {

            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You are not allowed to submit this movie!"
            );
        }

        movie.setStatus("PENDING");

        return movieRepository.save(movie);
    }
public Movie approveMovie(Long id) {

    Movie movie = getMovieById(id);

    if (movie == null) {
        return null;
    }

    if (!"PENDING".equals(movie.getStatus())) {
        throw new RuntimeException("Only pending movies can be approved!");
    }

    movie.setStatus("PUBLISHED");

    return movieRepository.save(movie);
}

public Movie rejectMovie(Long id) {

    Movie movie = getMovieById(id);

    if (movie == null) {
        return null;
    }

    if (!"PENDING".equals(movie.getStatus())) {
        throw new RuntimeException("Only pending movies can be rejected!");
    }

    movie.setStatus("REJECTED");

    return movieRepository.save(movie);
}
public List<Movie> getPublishedMovies() {
    return movieRepository.findByStatus("PUBLISHED");
}

}
