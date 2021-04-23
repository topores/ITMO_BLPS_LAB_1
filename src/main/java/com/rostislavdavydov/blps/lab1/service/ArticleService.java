package com.rostislavdavydov.blps.lab1.service;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.model.Claim;
import com.rostislavdavydov.blps.lab1.model.Request;
import com.rostislavdavydov.blps.lab1.model.User;
import com.rostislavdavydov.blps.lab1.repository.ArticleRepository;
import com.rostislavdavydov.blps.lab1.repository.ClaimRepository;
import com.rostislavdavydov.blps.lab1.repository.RequestRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ClaimRepository claimRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,ClaimRepository claimRepository,RequestRepository requestRepository) {
        this.articleRepository = articleRepository;
        this.claimRepository = claimRepository;
        this.requestRepository = requestRepository;
    }

    public Article saveArticle(Article article) {
        articleRepository.save(article);
        return article;
    }

/*
    public Article deleteArticle(Article article) {
        articleRepository.save(article);
        return article;
    }

 */
@SneakyThrows
@Transactional
public Article addArticle(String topic,String text,Boolean isDraft,User user){

    List<Request> rList = requestRepository.findAllByTopicAndState(topic, "APPROVED");
    if (rList==null) {
        log.info(String.valueOf(rList));
        throw new Exception("efjncmk");
    }
    //if (rList.isEmpty()) return null;
    ///log.info(String.valueOf(rList));

    for (Request r : rList) {
        r.setState("CLOSED");
        requestRepository.save(r);
    }
    //log.info(String.valueOf(rList));

    String state;
    if (isDraft) state = "DRAFT";
    else state = "AWAIT_CORR_VERIF";
    //log.info(String.valueOf(state));

    Article article=new Article()
            .setTopic(topic)
            .setText(text)
            .setUser(user)
            .setState(state);

    //log.info(String.valueOf(article));
    articleRepository.save(article);
    //log.info(String.valueOf(article));
    return article;





}

    @SneakyThrows
    @Transactional
    public void deleteArticle(Article article){

        List<Claim> claims=claimRepository.findAllByArticle(article);
        for (Claim claim:claims){
            if (claim.getState().equals("AWAIT_EDIT_VERIF")){
                claim.setState("APPROVED");
                claimRepository.save(claim);

                //throw new ClaimException("Claim forbidden to approve");
            }

        }
        articleRepository.delete(article);



    }

    public List<Article> fetchArticlesByUser(User user) {
        return articleRepository.findAllByUser(user);
    }

    public List<Article> fetchArticlesByUserAndState(User user, String state) {
        return articleRepository.findAllByUserAndState(user, state);
    }

    public List<Article> fetchArticlesByUserAndNotState(User user, String state) {
        return articleRepository.findAllByUserAndStateNot(user, state);
    }

    public List<Article> fetchArticlesByState(String state) {
        return articleRepository.findAllByState(state);
    }

    public Optional<Article> fetchArticleByIdAndState(Long id, String state) {
        return articleRepository.findByIdAndState(id, state);
    }
    public Optional<Article> fetchArticleById(Long id) {
        return articleRepository.findById(id);
    }

    public Optional<Article> fetchArticleByIdAndStateAndUser(Long id, String state, User user) {
        return articleRepository.findByIdAndStateAndUser(id, state, user);
    }



}