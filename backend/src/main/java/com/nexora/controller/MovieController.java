package com.nexora.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexora.entity.Movie;
import com.nexora.service.MovieService;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Add movie
    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        return movieService.addMovie(movie);
    }

    // Get all movies
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);

        if (movie == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(movie);
    }

    // Update movie
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long id,
            @RequestBody Movie movie) {

        Movie updatedMovie = movieService.updateMovie(id, movie);

        if (updatedMovie == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMovie);
    }

    // Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam String title) {
    	return movieService.searchMoviesByTitle(title);
}

	@GetMapping("/genre/{genre}")
	public List<Movie> filterMoviesByGenre(@PathVariable String genre) {
		return movieService.filterMoviesByGenre(genre);
}

	@PutMapping("/{id}/submit")
public ResponseEntity<Movie> submitMovieForApproval(
        @PathVariable Long id) {

    Movie movie = movieService.submitMovieForApproval(id);

    if (movie == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(movie);
}

	@PutMapping("/{id}/approve")
public ResponseEntity<Movie> approveMovie(@PathVariable Long id) {

    Movie movie = movieService.approveMovie(id);

    if (movie == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(movie);
}
	@PutMapping("/{id}/reject")
public ResponseEntity<Movie> rejectMovie(@PathVariable Long id) {

    Movie movie = movieService.rejectMovie(id);

    if (movie == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(movie);
}


}
