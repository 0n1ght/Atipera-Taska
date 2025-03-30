package com.example.github.controller;

import com.example.github.exception.NotFoundException;
import com.example.github.model.BranchResponse;
import com.example.github.model.RepositoryResponse;
import com.example.github.service.GithubService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubRepositoryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @MockBean
    private GithubService githubService;

    @Test
    public void testGetUserRepositories_success() {
        String username = "testuser";
        List<RepositoryResponse> fakeRepos = List.of(
                new RepositoryResponse("repo1", username,
                        List.of(new BranchResponse("main", "abc123")))
        );

        Mockito.when(githubService.getRepositories(username)).thenReturn(fakeRepos);

        RepositoryResponse[] response = testRestTemplate.getForObject("/repositories/" + username, RepositoryResponse[].class);
        assertThat(response).isNotNull().hasSize(1);
        RepositoryResponse repo = response[0];
        assertThat(repo.getRepositoryName()).isEqualTo("repo1");
        assertThat(repo.getOwnerLogin()).isEqualTo(username);
        assertThat(repo.getBranches()).hasSize(1);
        BranchResponse branch = repo.getBranches().get(0);
        assertThat(branch.getName()).isEqualTo("main");
        assertThat(branch.getLastCommitSha()).isEqualTo("abc123");
    }

    @Test
    public void testGetUserRepositories_userNotFound() {
        String username = "nonexistent";

        Mockito.when(githubService.getRepositories(username))
                .thenThrow(new NotFoundException(404, "User not found"));

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/repositories/" + username, String.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
        assertThat(responseEntity.getBody()).contains("\"status\":404", "\"message\":\"User not found\"");
    }

    @Test
    public void testGetUserRepositories_emptyRepoList() {
        String username = "emptyuser";

        Mockito.when(githubService.getRepositories(username)).thenReturn(Collections.emptyList());

        RepositoryResponse[] response = testRestTemplate.getForObject("/repositories/" + username, RepositoryResponse[].class);
        assertThat(response).isNotNull();
        assertThat(response.length).isEqualTo(0);
    }
}
