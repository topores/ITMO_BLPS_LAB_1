package com.rostislavdavydov.blps.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.rostislavdavydov.blps.lab1.model.User;
import com.rostislavdavydov.blps.lab1.model.Article;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, CrudRepository<Article, Long> {

    List<Article> findAllByUser(User user);

    List<Article> findAllByState(String state);

    List<Article> findAllByUserAndState(User user, String state);

    List<Article> findAllByUserAndStateNot(User user, String state);

    Optional<Article> findByIdAndState(Long id, String state);

    Optional<Article> findByIdAndStateAndUser(Long id, String state, User user);
}
