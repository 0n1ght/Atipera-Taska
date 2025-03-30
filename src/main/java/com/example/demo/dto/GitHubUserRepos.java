package com.example.demo.dto;

import java.util.List;

public class GitHubUserRepos {
    private List<GitHubRepo> repos;

    public GitHubUserRepos(GitHubRepo[] repos) {
        this.repos = List.of(repos);
    }

    public List<GitHubRepo> getRepos() {
        return repos;
    }

    public void setRepos(List<GitHubRepo> repos) {
        this.repos = repos;
    }
}
