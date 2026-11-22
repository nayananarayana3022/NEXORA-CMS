package com.nexora.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexora.entity.Article;
import com.nexora.repository.ArticleRepository;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void testSearchArticlesByTitle() {

        Article article = new Article();
        article.setTitle("Top 10 Movies to Watch");

        when(articleRepository.findByTitleContainingIgnoreCase("Movies"))
                .thenReturn(List.of(article));

        List<Article> articles =
                articleService.searchArticlesByTitle("Movies");

        assertEquals(1, articles.size());
        assertEquals("Top 10 Movies to Watch",
                articles.get(0).getTitle());
    }

    @Test
    void testFilterArticlesByCategory() {

        Article article = new Article();
        article.setTitle("Entertainment News");
        article.setCategory("Entertainment");

        when(articleRepository.findByCategoryIgnoreCase("Entertainment"))
                .thenReturn(List.of(article));

        List<Article> articles =
                articleService.filterArticlesByCategory("Entertainment");

        assertEquals(1, articles.size());
        assertEquals("Entertainment",
                articles.get(0).getCategory());
    }

    @Test
    void testGetPublishedArticles() {

        Article article = new Article();
        article.setTitle("Latest Entertainment News");
        article.setStatus("PUBLISHED");

        when(articleRepository.findByStatus("PUBLISHED"))
                .thenReturn(List.of(article));

        List<Article> articles = articleService.getPublishedArticles();

        assertEquals(1, articles.size());
        assertEquals("PUBLISHED", articles.get(0).getStatus());
    }
}
