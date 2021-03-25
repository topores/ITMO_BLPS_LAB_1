package com.rostislavdavydov.blps.lab1.api;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.service.ArticleService;
import com.rostislavdavydov.blps.lab1.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corrector")
public class CorrectorController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public CorrectorController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @ApiOperation(value = "${CorrectorController.correctArticle}")
    @PostMapping("articles/{article_id}/approve")
    public Article correctArticle(@ApiParam("article_id") @PathVariable(name = "article_id") Long article_id) {
        Optional<Article> o_article = articleService.fetchArticleByIdAndState(article_id, "AWAIT_CORR_VERIF");
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();
        article.setState("AWAIT_MOD_VERIF");
        return articleService.saveArticle(article);
    }
    @ApiOperation(value = "${CorrectorController.correctArticle}")
    @PostMapping("articles/{article_id}/correct")
    public Article correctArticle(@ApiParam("article_id") @PathVariable(name = "article_id") Long article_id,
                                  @ApiParam("text") @RequestParam(name = "text") String text) {
        Optional<Article> o_article = articleService.fetchArticleByIdAndState(article_id, "AWAIT_CORR_VERIF");
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();

        article.setText(text);
        article.setState("AWAIT_AUTHOR_VERIF");

        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${CorrectorController.findArticles}")
    @GetMapping("/articles")
    public List<Article> findArticles() {
        return articleService.fetchArticlesByState("AWAIT_CORR_VERIF");
    }


}