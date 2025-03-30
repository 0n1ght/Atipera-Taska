package com.example.github.controller;

import com.example.github.model.RepositoryResponse;
import com.example.github.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repositories")
public class GithubRepositoryController {
    private final GithubService githubService;

    @Autowired
    public GithubRepositoryController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}")
    public List<RepositoryResponse> getUserRepositories(@PathVariable String username) {
        return githubService.getRepositories(username);
    }
}
