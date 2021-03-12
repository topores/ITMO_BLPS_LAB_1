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
@RequestMapping("/eapi")
public class EditorController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public EditorController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @ApiOperation(value = "${EditorController.editArticle}")
    @PostMapping("/edit")
    public Article editArticle(@ApiParam("article_id") @RequestParam(name = "article_id") Long article_id,
                               @ApiParam("text") @RequestParam(name = "text") String text) {

        Optional<Article> o_article = articleService.fetchArticleByIdAndState(article_id, "AWAIT_EDIT");
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();
        article.setState("AWAIT_MOD_VERIF").setText(text);
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${EditorController.findArticles}")
    @GetMapping("/find_by_author_id")
    public List<Article> findArticles(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id) {

        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        return articleService.fetchArticlesByUserAndState(user,"AWAIT_EDIT");
    }


    @ApiOperation(value = "${EditorController.findArticle}")
    @GetMapping("/find_by_article_id")
    public Optional<Article> findArticle(@ApiParam("article_id") @RequestParam(name = "article_id") Long article_id) {

        return articleService.fetchArticleByIdAndState(article_id,"AWAIT_EDIT");
    }

}
