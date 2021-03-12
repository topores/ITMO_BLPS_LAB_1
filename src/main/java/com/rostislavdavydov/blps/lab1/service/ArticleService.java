package com.rostislavdavydov.blps.lab1.service;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.model.User;
import com.rostislavdavydov.blps.lab1.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article saveArticle(Article article) {

        articleRepository.save(article);
        return article;
    }

    public List<Article> fetchArticlesByUser(User user) {
            return articleRepository.findAllByUser(user);

    }
    public List<Article> fetchArticlesByUserAndState(User user, String state) {
        return articleRepository.findAllByUserAndState(user,state);

    }
    public List<Article> fetchArticlesByUserAndNotState(User user, String state) {
        return articleRepository.findAllByUserAndStateNot(user,state);

    }
    public Optional<Article> fetchArticleByIdAndState(Long id, String state){
        return articleRepository.findByIdAndState(id,state);
    }

    public Optional<Article> fetchArticleByIdAndStateAndUser(Long id, String state,User user){
        return articleRepository.findByIdAndStateAndUser(id,state,user);
    }






}
