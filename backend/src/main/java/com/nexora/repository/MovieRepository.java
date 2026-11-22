package com.nexora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexora.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
	long countByStatus(String status);
	 List<Movie> findByTitleContainingIgnoreCase(String title);

	    List<Movie> findByGenreIgnoreCase(String genre);
	    
	    List<Movie> findByStatus(String status);

}
