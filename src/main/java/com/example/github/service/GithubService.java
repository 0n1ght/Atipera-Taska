package com.example.github.service;

import com.example.github.exception.NotFoundException;
import com.example.github.model.BranchResponse;
import com.example.github.model.RepositoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {
    private final RestTemplate restTemplate;

    @Autowired
    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryResponse> getRepositories(String username) {
        String reposUrl = "https://api.github.com/users/" + username + "/repos";

        GithubRepository[] repositories;
        try {
            repositories = restTemplate.getForObject(reposUrl, GithubRepository[].class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException(e.getStatusCode().value(), "User not found");
            }
            throw e;
        }

        if (repositories == null) {
            return List.of();
        }

        return Arrays.stream(repositories)
                .filter(repo -> !repo.isFork())
                .map(repo -> {
                    String branchesUrl = "https://api.github.com/repos/" + username + "/" + repo.getName() + "/branches";
                    GithubBranch[] branches = restTemplate.getForObject(branchesUrl, GithubBranch[].class);
                    List<BranchResponse> branchResponses = Arrays.stream(branches != null ? branches : new GithubBranch[0])
                            .map(branch -> new BranchResponse(branch.getName(), branch.getCommit().getSha()))
                            .collect(Collectors.toList());
                    return new RepositoryResponse(repo.getName(), repo.getOwner().getLogin(), branchResponses);
                })
                .collect(Collectors.toList());
    }

    private static class GithubRepository {
        private String name;
        private boolean fork;
        private Owner owner;
        public String getName() { return name; }
        public boolean isFork() { return fork; }
        public Owner getOwner() { return owner; }
        static class Owner {
            private String login;
            public String getLogin() { return login; }
        }
    }

    private static class GithubBranch {
        private String name;
        private Commit commit;
        public String getName() { return name; }
        public Commit getCommit() { return commit; }
        static class Commit {
            private String sha;
            public String getSha() { return sha; }
        }
    }
}
