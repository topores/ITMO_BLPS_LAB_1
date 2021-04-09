package com.rostislavdavydov.blps.lab1.api;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.model.Request;
import com.rostislavdavydov.blps.lab1.model.User;
import com.rostislavdavydov.blps.lab1.service.ArticleService;
import com.rostislavdavydov.blps.lab1.service.RequestService;
import com.rostislavdavydov.blps.lab1.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    private final ArticleService articleService;
    private final UserService userService;
    private final RequestService requestService;

    @Autowired
    public AuthorController(ArticleService articleService, UserService userService, RequestService requestService) {
        this.articleService = articleService;
        this.userService = userService;
        this.requestService = requestService;
    }

    @ApiOperation(value = "${AuthorController.addArticle}")
    @PostMapping("articles/add")
    public Article addArticle(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                              @ApiParam("topic") @RequestParam(name = "topic") String topic,
                              @ApiParam("text") @RequestParam(name = "text") String text,
                              @ApiParam("draft") @RequestParam(name = "draft", required = false) Boolean isDraft) {
        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        if (!"AUTHOR".equals(user.getRole())) return null;
        List<Request> rList = requestService.fetchRequestsByTopicAndState(topic, "APPROVED");
        //if (rList.isEmpty()) return null;
        for (Request r : rList) {
            r.setState("CLOSED");
            requestService.saveRequest(r);
        }
        //
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
    @PostMapping("articles/{article_id}/edit")
    public Article editArticle(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                               @ApiParam("article_id") @PathVariable(name = "article_id") Long article_id,
                               @ApiParam("text") @RequestParam(name = "text", required = false) String text) {
        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        Optional<Article> o_article = articleService.fetchArticleByIdAndStateAndUser(article_id, "AWAIT_AUTHOR_SUBMIT", user);
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();
        if (text == null) article.setState("AWAIT_MOD_VERIF");
        else {
            article.setState("AWAIT_CORR_VERIF").setText(text);
        }
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${AuthorController.addRequest}")
    @PostMapping("/requests/add")
    public Request request(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                           @ApiParam("topic") @RequestParam(name = "topic") String topic,
                           @ApiParam("description") @RequestParam(name = "description") String description) {
        User user = userService.fetchUserById(author_id);
        if (user == null) return null;


        return requestService.saveRequest(new Request()
                .setUser(user)
                .setTopic(topic)
                .setDescription(description)
                .setState("OPENED"));
    }

    @ApiOperation(value = "${AuthorController.findArticles}")
    @GetMapping("articles")
    public List<Article> findArticles(@ApiParam("author_id") @RequestParam(name = "author_id") Long author_id,
                                      @ApiParam("draft") @RequestParam(name = "draft", required = false) Boolean isDraft) {

        User user = userService.fetchUserById(author_id);
        if (user == null) return null;
        if (isDraft == null) return articleService.fetchArticlesByUser(user);
        else {
            if (isDraft) return articleService.fetchArticlesByUserAndState(user, "DRAFT");
            else return articleService.fetchArticlesByUserAndNotState(user, "DRAFT");
        }

    }
}
