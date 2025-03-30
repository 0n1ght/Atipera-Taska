package com.example.demo.controller;

import com.example.demo.dto.GitHubUserRepos;
import com.example.demo.exception.GitHubUserNotFoundException;
import com.example.demo.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
public class GitHubController {
    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/user/{username}/repos")
    public GitHubUserRepos getUserRepositories(@PathVariable String username) {

        return gitHubService.getUserRepositories(username);
    }

    @ExceptionHandler(GitHubUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(GitHubUserNotFoundException ex) {

        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    public static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
