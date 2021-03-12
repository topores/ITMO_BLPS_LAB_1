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
@RequestMapping("/capi")
public class CorrectorController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public CorrectorController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @ApiOperation(value = "${CorrectorController.correctArticle}")
    @PostMapping("/correct")
    public Article correctArticle(@ApiParam("article_id") @RequestParam(name = "article_id") Long article_id,
                              @ApiParam("text") @RequestParam(name = "text", required = false) String text) {
        Optional<Article> o_article=articleService.fetchArticleByIdAndState(article_id,"AWAIT_CORR_VERIF");
        if (!o_article.isPresent()) return null;
        Article article=o_article.get();

        if (text!=null) {
            article.setText(text);
            article.setState("AWAIT_AUTHOR_VERIF");

        } else article.setState("AWAIT_MOD_VERIF");
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${CorrectorController.findArticles}")
    @GetMapping("/find_by_author_id")
    public List<Article> findArticles(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id) {

        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        return articleService.fetchArticlesByUserAndState(user,"AWAIT_CORR_VERIF");
        }


    @ApiOperation(value = "${CorrectorController.findArticle}")
    @GetMapping("/find_by_article_id")
    public Optional<Article> findArticle(@ApiParam("article_id") @RequestParam(name = "article_id") Long article_id) {

        return articleService.fetchArticleByIdAndState(article_id,"AWAIT_CORR_VERIF");
    }


}
