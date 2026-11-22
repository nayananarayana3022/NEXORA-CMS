package com.nexora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nexora.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
	long countByStatus(String status);
	
	List<Article> findByTitleContainingIgnoreCase(String title);

    List<Article> findByCategoryIgnoreCase(String category);
    
    List<Article> findByStatus(String status);

}
