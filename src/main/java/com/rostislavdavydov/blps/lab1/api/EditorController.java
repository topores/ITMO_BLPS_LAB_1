package com.rostislavdavydov.blps.lab1.api;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.model.Request;
import com.rostislavdavydov.blps.lab1.model.Claim;

import com.rostislavdavydov.blps.lab1.service.ArticleService;
import com.rostislavdavydov.blps.lab1.service.RequestService;
import com.rostislavdavydov.blps.lab1.service.UserService;
import com.rostislavdavydov.blps.lab1.service.ClaimService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/editor")
public class EditorController {

    private final ArticleService articleService;
    private final UserService userService;
    private final ClaimService claimService;
    private final RequestService requestService;


    @Autowired
    public EditorController(ArticleService articleService, UserService userService, RequestService requestService, ClaimService claimService) {
        this.articleService = articleService;
        this.userService = userService;
        this.requestService = requestService;
        this.claimService = claimService;

    }

    @ApiOperation(value = "${EditorController.findClaims}")
    @GetMapping("/claims")
    public List<Claim> findClaims(@ApiParam("onlyAwaitEditor") @RequestParam(name = "onlyAwaitEditor", required = false) Boolean onlyAwaitEditor) {
        if (onlyAwaitEditor == null) {
            onlyAwaitEditor=false;
        }
        if (!onlyAwaitEditor) return claimService.fetchClaims();
        else return claimService.fetchClaimsByState("AWAIT_EDIT_VERIF");
    }


    @ApiOperation(value = "${EditorController.deleteArticle}")
    @DeleteMapping("articles/{article_id}/delete")
    public Article deleteArticle(@ApiParam("article_id") @PathVariable(name = "article_id") Long article_id) {
        Optional<Article> o_article = articleService.fetchArticleById(article_id);
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();

        return articleService.deleteArticle(article);//Article(article);
    }





    @ApiOperation(value = "${EditorController.findRequests}")
    @GetMapping("/requests")
    public List<Request> findRequests(@ApiParam("onlyOpened") @RequestParam(name = "onlyOpened", required = false) Boolean onlyOpened) {
        if (onlyOpened == null) {
            onlyOpened=false;
        }
        if (!onlyOpened) return requestService.fetchRequests();
        else return requestService.fetchRequestsByState("OPENED");
    }


    @ApiOperation(value = "${EditorController.approveRequest}")
    @PostMapping("requests/{request_id}/approve")
    public Request approveRequest(@ApiParam("request_id") @PathVariable(name = "request_id") Long request_id) {
        Optional<Request> o_request = requestService.fetchRequestByIdAndState(request_id, "OPENED");
        if (!o_request.isPresent()) return null;
        Request request = o_request.get();
        request.setState("APPROVED");
        return requestService.saveRequest(request);
    }

    @ApiOperation(value = "${EditorController.declineRequest}")
    @PostMapping("requests/{request_id}/decline")
    public Request declineRequest(@ApiParam("request_id") @PathVariable(name = "request_id") Long request_id) {
        Optional<Request> o_request = requestService.fetchRequestByIdAndState(request_id, "OPENED");
        if (!o_request.isPresent()) return null;
        Request request = o_request.get();
        request.setState("DECLINED");
        return requestService.saveRequest(request);
    }

    @ApiOperation(value = "${EditorController.approveArticle}")
    @PostMapping("articles/{article_id}/approve")
    public Article approveArticle(@ApiParam("article_id") @PathVariable(name = "article_id") Long article_id) {
        Optional<Article> o_article = articleService.fetchArticleByIdAndState(article_id, "AWAIT_MOD_VERIF");
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();
        article.setState("SUBMITTED");
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${EditorController.declineArticle}")
    @PostMapping("articles/{article_id}/decline")
    public Article moderateArticle(@ApiParam("article_id") @PathVariable(name = "article_id") Long article_id) {
        Optional<Article> o_article = articleService.fetchArticleByIdAndState(article_id, "AWAIT_MOD_VERIF");
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();
        article.setState("AWAIT_AUTHOR_SUBMIT");
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${EditorController.findArticles}")
    @GetMapping("/articles")
    public List<Article> findArticles() {

        return articleService.fetchArticlesByState("AWAIT_MOD_VERIF");
    }

}