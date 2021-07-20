package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

class ContributionsConnector extends GitHubConnector {

    /**
     * GitHub Bots that do not count as Contributors
     * (includes "invalid-email-address" because it is an invalid contributor)
     */
    private final List<String> ignoredAccounts = new ArrayList<>();

    /**
     * Matches a GitHub name with a Minecraft name.
     */
    private final Map<String, String> aliases = new HashMap<>();

    private final String prefix;
    private final String role;
    private final int page;

    private boolean finished = false;

    @ParametersAreNonnullByDefault
    ContributionsConnector(GitHubService github, String prefix, int page, String repository, ContributorRole role) {
        super(github, repository);

        this.prefix = prefix;
        this.page = page;
        this.role = role.getId();

        loadConfiguration();
    }

    /**
     * This method loads all aliases.
     * This mapping matches a GitHub username with a Minecraft username.
     * These people are... "special cases".
     */
    private void loadConfiguration() {
        // Bots and invalid accounts we want to ignore.
        ignoredAccounts.add("invalid-email-address");
        ignoredAccounts.add("renovate");
        ignoredAccounts.add("renovate-bot");
        ignoredAccounts.add("renovate[bot]");
        ignoredAccounts.add("TheBusyBot");
        ignoredAccounts.add("ImgBotApp");
        ignoredAccounts.add("imgbot");
        ignoredAccounts.add("imgbot[bot]");
        ignoredAccounts.add("github-actions[bot]");
        ignoredAccounts.add("gitlocalize-app");
        ignoredAccounts.add("gitlocalize-app[bot]");
        ignoredAccounts.add("mt-gitlocalize");

        // Known Minecraft aliases.
        aliases.put("WalshyDev", "HumanRightsAct");
        aliases.put("J3fftw1", "_lagpc_");
        aliases.put("ajan-12", "ajan_12");
        aliases.put("mrcoffee1026", "mr_coffee1026");
        aliases.put("Cyber-MC", "CyberPatriot");
        aliases.put("BurningBrimstone", "Bluedevil74");
        aliases.put("bverhoeven", "soczol");
        aliases.put("ramdon-person", "ramdon_person");
        aliases.put("NCBPFluffyBear", "FluffyBear_");
        aliases.put("martinbrom", "OneTime97");
        aliases.put("LilBC", "Lil_BC");
    }

    /**
     * This returns whether this {@link ContributionsConnector} has finished its task.
     * 
     * @return Whether it is finished
     */
    public boolean hasFinished() {
        return finished;
    }

    @Override
    public void onSuccess(@Nonnull JsonNode response) {
        finished = true;

        if (response.isArray()) {
            computeContributors(response.getArray());
        } else {
            Slimefun.logger().log(Level.WARNING, "Received an unusual answer from GitHub, possibly a timeout? ({0})", response);
        }
    }

    @Override
    public void onFailure() {
        finished = true;
    }

    @Override
    public String getFileName() {
        return prefix + "_contributors";
    }

    @Override
    public String getEndpoint() {
        return "/contributors";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("per_page", 100);
        parameters.put("page", page);
        return parameters;
    }

    private void computeContributors(@Nonnull JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            String name = object.getString("login");
            int commits = object.getInt("contributions");
            String profile = object.getString("html_url");

            if (!ignoredAccounts.contains(name)) {
                String username = aliases.getOrDefault(name, name);
                github.addContributor(username, profile, role, commits);
            }
        }
    }
}
