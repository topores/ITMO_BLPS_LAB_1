package com.rostislavdavydov.blps.lab1.api;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.model.User;
import com.rostislavdavydov.blps.lab1.service.ArticleService;
import com.rostislavdavydov.blps.lab1.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/aapi")
public class AuthorController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public AuthorController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @ApiOperation(value = "${AuthorController.addArticle}")
    @PostMapping("/add")
    public Article addArticle(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                              @ApiParam("topic") @RequestParam(name = "topic") String topic,
                              @ApiParam("text") @RequestParam(name = "text") String text,
                              @ApiParam("draft") @RequestParam(name = "draft", required = false) Boolean isDraft) {
        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        if (!"AUTHOR".equals(user.getRole())) return null;
        if (isDraft == null) isDraft = false;
        String state;
        if (isDraft) state = "DRAFT";
        else state = "AWAIT_CORR_VERIF";

        return articleService.saveArticle(new Article()
                .setTopic(topic)
                .setText(text)
                .setUser(user)
                .setState(state)
        );
    }
    @ApiOperation(value = "${AuthorController.editArticle}")
    @PostMapping("/edit")
    public Article editArticle(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                               @ApiParam("article_id") @RequestParam(name = "article_id") Long article_id,
                              @ApiParam("text") @RequestParam(name = "text",required = false) String text) {
        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        Optional<Article> o_article=articleService.fetchArticleByIdAndStateAndUser(article_id,"AWAIT_AUTHOR_SUBMIT",user);
        if (!o_article.isPresent()) return null;
        Article article=o_article.get();
        if (text==null) article.setState("AWAIT_MOD_VERIF");
        else {
            article.setState("AWAIT_CORR_VERIF").setText(text);
        }
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${AuthorController.requestEditArticle}")
    @PostMapping("/request_edit")
    public Article requestEditArticle(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                                      @ApiParam("article_id") @RequestParam(name = "article_id") Long article_id) {
        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        Optional<Article> o_article=articleService.fetchArticleByIdAndStateAndUser(article_id,"SUBMITTED",user);
        if (!o_article.isPresent()) return null;
        Article article=o_article.get();
        article.setState("AWAIT_EDIT");

        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${AuthorController.findArticles}")
    @GetMapping("/find")
    public List<Article> findArticles(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                                      @ApiParam("draft") @RequestParam(name = "draft", required = false) Boolean isDraft) {

        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        if (isDraft == null)  return articleService.fetchArticlesByUser(user);
        else {
            if (isDraft) return articleService.fetchArticlesByUserAndState(user,"DRAFT");
            else return articleService.fetchArticlesByUserAndNotState(user,"DRAFT");
        }

    }
}
