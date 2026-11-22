
package com.nexora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nexora.entity.Article;
import com.nexora.repository.ArticleRepository;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // Add article

public Article addArticle(Article article) {

    article.setCreatedAt(java.time.LocalDateTime.now());

    String email = org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    article.setCreatedBy(email);

    return articleRepository.save(article);
}

 public List<Article> getPublishedArticles() {
      return articleRepository.findByStatus("PUBLISHED");
 }


    // Get all articles
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // Get article by ID
    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    // Update article

public Article updateArticle(Long id, Article updatedArticle) {

    Article existingArticle = getArticleById(id);

    if (existingArticle == null) {
        return null;
    }

    // Get logged-in user's authentication
    org.springframework.security.core.Authentication authentication =
            org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String email = authentication.getName();

    // Check whether the user is ADMIN
    boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority ->
                    authority.getAuthority().equals("ROLE_ADMIN"));

    // Allow only ADMIN or the article's creator
    if (!isAdmin && !email.equals(existingArticle.getCreatedBy())) {
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN,
                "You are not allowed to update this article!"
        );
    }

    existingArticle.setTitle(updatedArticle.getTitle());
    existingArticle.setCategory(updatedArticle.getCategory());
    existingArticle.setAuthor(updatedArticle.getAuthor());
    existingArticle.setContent(updatedArticle.getContent());
    existingArticle.setImageUrl(updatedArticle.getImageUrl());
    

    return articleRepository.save(existingArticle);
}

    // Delete article
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
    

    public List<Article> searchArticlesByTitle(String title) {
    	return articleRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Article> filterArticlesByCategory(String category) {
    	return articleRepository.findByCategoryIgnoreCase(category);
}

    public Article submitArticleForApproval(Long id) {

        Article article = getArticleById(id);

        if (article == null) {
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

        // Only ADMIN or the article creator can submit
        if (!isAdmin && !email.equals(article.getCreatedBy())) {

            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You are not allowed to submit this article!"
            );
        }

        article.setStatus("PENDING");

        return articleRepository.save(article);
    }

public Article approveArticle(Long id) {

    Article article = getArticleById(id);

    if (article == null) {
        return null;
    }

    if (!"PENDING".equals(article.getStatus())) {
        throw new RuntimeException("Only pending articles can be approved!");
    }

    article.setStatus("PUBLISHED");

    return articleRepository.save(article);
}

public Article rejectArticle(Long id) {

    Article article = getArticleById(id);

    if (article == null) {
        return null;
    }

    if (!"PENDING".equals(article.getStatus())) {
        throw new RuntimeException("Only pending articles can be rejected!");
    }

    article.setStatus("REJECTED");

    return articleRepository.save(article);
}


}
