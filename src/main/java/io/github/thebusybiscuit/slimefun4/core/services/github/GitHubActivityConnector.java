package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

class GitHubActivityConnector extends GitHubConnector {

    private final ActivityCallback callback;

    @ParametersAreNonnullByDefault
    GitHubActivityConnector(GitHubService github, String repository, ActivityCallback callback) {
        super(github, repository);
        this.callback = callback;
    }

    @Override
    public void onSuccess(@Nonnull JsonNode response) {
        JSONObject object = response.getObject();
        int forks = object.getInt("forks");
        int stars = object.getInt("stargazers_count");
        LocalDateTime lastPush = NumberUtils.parseGitHubDate(object.getString("pushed_at"));

        callback.accept(forks, stars, lastPush);
    }

    @Override
    public String getFileName() {
        return "repo";
    }

    @Override
    public String getEndpoint() {
        return "";
    }

    @Override
    public Map<String, Object> getParameters() {
        return new HashMap<>();
    }

}
