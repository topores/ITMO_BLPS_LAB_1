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
@RequestMapping("/mapi")
public class ModeratorController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public ModeratorController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @ApiOperation(value = "${ModeratorController.moderateArticle}")
    @PostMapping("/moderate")
    public Article moderateArticle(@ApiParam("article_id") @RequestParam(name = "article_id") Long article_id,
                              @ApiParam("sumbit") @RequestParam(name = "submit", required = true) Boolean submit) {
        Optional<Article> o_article=articleService.fetchArticleByIdAndState(article_id,"AWAIT_MOD_VERIF");
        if (!o_article.isPresent()) return null;
        Article article=o_article.get();
        if (submit) article.setState("SUBMITTED");
        else article.setState("AWAIT_AUTHOR_SUBMIT");
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${ModeratorController.findArticles}")
    @GetMapping("/find_by_author_id")
    public List<Article> findArticles(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id) {

        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        return articleService.fetchArticlesByUserAndState(user,"AWAIT_MOD_VERIF");
        }


    @ApiOperation(value = "${ModeratorController.findArticle}")
    @GetMapping("/find_by_article_id")
    public Optional<Article> findArticle(@ApiParam("article_id") @RequestParam(name = "article_id") Long article_id) {

        return articleService.fetchArticleByIdAndState(article_id,"AWAIT_MOD_VERIF");
    }


}
