package com.example.demo.dto;

import java.util.List;

public class GitHubRepo {
    private String name;
    private String ownerLogin;
    private List<GitHubBranch> branches;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public List<GitHubBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<GitHubBranch> branches) {
        this.branches = branches;
    }
}
