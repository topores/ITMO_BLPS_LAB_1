package com.rostislavdavydov.blps.lab1.api;

import com.rostislavdavydov.blps.lab1.model.Article;
import com.rostislavdavydov.blps.lab1.model.Claim;
import com.rostislavdavydov.blps.lab1.service.ArticleService;
import com.rostislavdavydov.blps.lab1.service.UserService;
import com.rostislavdavydov.blps.lab1.service.ClaimService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {

    private final ArticleService articleService;
    private final ClaimService claimService;
    private final UserService userService;

    @Autowired
    public ModeratorController(ArticleService articleService, UserService userService, ClaimService claimService) {
        this.articleService = articleService;
        this.userService = userService;
        this.claimService = claimService;
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

    @ApiOperation(value = "${EditorController.approveArticle}")
    @PostMapping("articles/{article_id}/decline")
    public Article declineArticle(@ApiParam("article_id") @PathVariable(name = "article_id") Long article_id) {
        Optional<Article> o_article = articleService.fetchArticleByIdAndState(article_id, "AWAIT_MOD_VERIF");
        if (!o_article.isPresent()) return null;
        Article article = o_article.get();
        article.setState("AWAIT_AUTHOR_SUBMIT");
        return articleService.saveArticle(article);
    }

    @ApiOperation(value = "${ModeratorController.findArticles}")
    @GetMapping("/articles")
    public List<Article> findArticles() {

        return articleService.fetchArticlesByState("AWAIT_MOD_VERIF");
    }



    @ApiOperation(value = "${ModeratorController.findClaims}")
    @GetMapping("/claims")
    public List<Claim> findClaims(@ApiParam("onlyOpened") @RequestParam(name = "onlyOpened", required = false) Boolean onlyOpened) {
        if (onlyOpened == null) {
            onlyOpened=false;
        }
        if (!onlyOpened) return claimService.fetchClaims();
        else return claimService.fetchClaimsByState("OPENED");
    }


    @ApiOperation(value = "${ModeratorController.approveClaims}")
    @PostMapping("claims/{claim_id}/approve")
    public Claim approveClaim(@ApiParam("claim_id") @PathVariable(name = "claim_id") Long claim_id) {
        Optional<Claim> o_claim = claimService.fetchClaimByIdAndState(claim_id,"OPENED");
        if (!o_claim.isPresent()) return null;
        Claim claim = o_claim.get();
        claim.setState("AWAIT_EDIT_VERIF");
        return claimService.saveClaim(claim);
    }

    @ApiOperation(value = "${ModeratorController.declineClaims}")
    @PostMapping("claims/{claim_id}/decline")
    public Claim declineClaim(@ApiParam("claim_id") @PathVariable(name = "claim_id") Long claim_id) {
        Optional<Claim> o_claim = claimService.fetchClaimByIdAndState(claim_id,"OPENED");
        if (!o_claim.isPresent()) return null;
        Claim claim = o_claim.get();
        claim.setState("CLOSED_DECLINED");
        return claimService.saveClaim(claim);
    }

}