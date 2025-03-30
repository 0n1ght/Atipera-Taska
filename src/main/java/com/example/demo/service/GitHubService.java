package com.example.demo.service;

import com.example.demo.dto.GitHubRepo;
import com.example.demo.dto.GitHubUserRepos;
import com.example.demo.exception.GitHubUserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String GITHUB_API_URL = "https://api.github.com/users/";

    public GitHubUserRepos getUserRepositories(String username) {

        String url = GITHUB_API_URL + username + "/repos?type=owner&fork=false";

        GitHubRepo[] repos = restTemplate.getForObject(url, GitHubRepo[].class);
        if (repos == null || repos.length == 0) {
            throw new GitHubUserNotFoundException("User not found or no repositories available.");
        }

        return new GitHubUserRepos(repos);
    }
}
