
package com.nexora.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexora.entity.Movie;
import com.nexora.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void testSearchMoviesByTitle() {

        Movie movie = new Movie();
        movie.setTitle("Inception");

        when(movieRepository.findByTitleContainingIgnoreCase("Inception"))
                .thenReturn(List.of(movie));

        List<Movie> movies =
                movieService.searchMoviesByTitle("Inception");

        assertEquals(1, movies.size());
        assertEquals("Inception", movies.get(0).getTitle());
    }

    @Test
    void testFilterMoviesByGenre() {

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Action");

        when(movieRepository.findByGenreIgnoreCase("Action"))
                .thenReturn(List.of(movie));

        List<Movie> movies =
                movieService.filterMoviesByGenre("Action");

        assertEquals(1, movies.size());
        assertEquals("Action", movies.get(0).getGenre());
    }

    @Test
    void testGetPublishedMovies() {

        Movie movie = new Movie();
        movie.setTitle("Avatar");
        movie.setStatus("PUBLISHED");

        when(movieRepository.findByStatus("PUBLISHED"))
                .thenReturn(List.of(movie));

        List<Movie> movies = movieService.getPublishedMovies();

        assertEquals(1, movies.size());
        assertEquals("PUBLISHED", movies.get(0).getStatus());
    }
}
