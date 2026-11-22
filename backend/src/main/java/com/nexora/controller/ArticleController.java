package com.nexora.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexora.entity.Article;
import com.nexora.service.ArticleService;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // Add article
    @PostMapping
    public Article addArticle(@RequestBody Article article) {
        return articleService.addArticle(article);
    }

    // Get all articles
    @GetMapping
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    // Get article by ID
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {

        Article article = articleService.getArticleById(id);

        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(article);
    }

    // Update article
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody Article article) {

        Article updatedArticle = articleService.updateArticle(id, article);

        if (updatedArticle == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedArticle);
    }

    // Delete article
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {

        deleteArticleById(id);

        return ResponseEntity.noContent().build();
    }

    private void deleteArticleById(Long id) {
        articleService.deleteArticle(id);
    }

    @GetMapping("/search")
    public List<Article> searchArticles(@RequestParam String title) {
    	return articleService.searchArticlesByTitle(title);
    }

    @GetMapping("/category/{category}")
    public List<Article> filterArticlesByCategory(
    		@PathVariable String category) {

    	return articleService.filterArticlesByCategory(category);
}

@PutMapping("/{id}/submit")
public ResponseEntity<Article> submitArticleForApproval(
        @PathVariable Long id) {

    Article article = articleService.submitArticleForApproval(id);

    if (article == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(article);
}

@PutMapping("/{id}/approve")
public ResponseEntity<Article> approveArticle(@PathVariable Long id) {

    Article article = articleService.approveArticle(id);

    if (article == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(article);
}

@PutMapping("/{id}/reject")
public ResponseEntity<Article> rejectArticle(@PathVariable Long id) {

    Article article = articleService.rejectArticle(id);

    if (article == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(article);
}

}
