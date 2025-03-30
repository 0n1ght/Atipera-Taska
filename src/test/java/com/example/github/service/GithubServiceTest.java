package com.example.github.service;

import com.example.github.exception.NotFoundException;
import com.example.github.model.BranchResponse;
import com.example.github.model.RepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockRestServiceServer
public class GithubServiceTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GithubService githubService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testGetRepositories_success() {
        String username = "testuser";
        String reposResponse = "[{" +
                "\"name\": \"repo1\"," +
                "\"fork\": false," +
                "\"owner\": { \"login\": \"" + username + "\" }" +
                "}]";

        mockServer.expect(requestTo("https://api.github.com/users/" + username + "/repos"))
                .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

        String branchesResponse = "[{" +
                "\"name\": \"main\"," +
                "\"commit\": { \"sha\": \"abc123\" }" +
                "}]";

        mockServer.expect(requestTo("https://api.github.com/repos/" + username + "/repo1/branches"))
                .andRespond(withSuccess(branchesResponse, MediaType.APPLICATION_JSON));

        List<RepositoryResponse> repos = githubService.getRepositories(username);
        mockServer.verify();

        assertThat(repos).isNotNull().hasSize(1);
        RepositoryResponse repo = repos.get(0);
        assertThat(repo.getRepositoryName()).isEqualTo("repo1");
        assertThat(repo.getOwnerLogin()).isEqualTo(username);
        List<BranchResponse> branches = repo.getBranches();
        assertThat(branches).hasSize(1);
        BranchResponse branch = branches.get(0);
        assertThat(branch.getName()).isEqualTo("main");
        assertThat(branch.getLastCommitSha()).isEqualTo("abc123");
    }

    @Test
    public void testGetRepositories_userNotFound() {
        String username = "nonexistent";

        mockServer.expect(requestTo("https://api.github.com/users/" + username + "/repos"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\": \"Not Found\"}"));

        assertThatThrownBy(() -> githubService.getRepositories(username))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");

        mockServer.verify();
    }

    @Test
    public void testGetRepositories_emptyRepoList() {
        String username = "emptyuser";
        String reposResponse = "[]";

        mockServer.expect(requestTo("https://api.github.com/users/" + username + "/repos"))
                .andRespond(withSuccess(reposResponse, MediaType.APPLICATION_JSON));

        List<RepositoryResponse> repos = githubService.getRepositories(username);
        mockServer.verify();
        assertThat(repos).isEmpty();
    }
}
