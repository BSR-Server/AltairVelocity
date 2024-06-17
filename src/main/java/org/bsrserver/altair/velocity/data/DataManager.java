package org.bsrserver.altair.velocity.data;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataManager {
    private final AltairVelocity altairVelocity;
    private final Logger logger;
    private final OkHttpClient client = new OkHttpClient();
    private final HashMap<Integer, Account> accountHashMap = new HashMap<>();
    private final ArrayList<String> quotations = new ArrayList<>();
    private final HashMap<UUID, MinecraftProfile> minecraftProfileHashMap = new HashMap<>();
    private final HashMap<String, ServerInfo> serverInfoHashMap = new HashMap<>();
    private final HashMap<Integer, ServerGroup> serverGroupHashMap = new HashMap<>();

    public DataManager(AltairVelocity altairVelocity) {
        this.altairVelocity = altairVelocity;
        this.logger = altairVelocity.getLogger();
        altairVelocity.getScheduledExecutorService().scheduleAtFixedRate(this::scheduledTask, 0, 30, TimeUnit.SECONDS);
    }

    private void scheduledTask() {
        try {
            updateAccounts();
            updateQuotations();
            updateMinecraftProfiles();
            updateServers();
            updateServerGroups();
        } catch (Exception e) {
            altairVelocity.getLogger().error("Failed to get update some data", e);
        }
    }

    private Request createGetRequest(String path) {
        String authorization = "KeySecuredClient " + altairVelocity.getConfig().getBackendSecuredClientKey();
        return new Request.Builder()
                .url(altairVelocity.getConfig().getBackendBaseUrl() + path)
                .header("Authorization", authorization)
                .build();
    }

    private void updateAccounts() {
        // get accounts
        JSONArray accountsJSONArray = null;
        try (Response response = client.newCall(createGetRequest("/v1/auth/accounts")).execute()) {
            if (response.body() != null) {
                accountsJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("accounts");
            }
        } catch (IOException exception) {
            logger.error("Failed to get accounts", exception);
        }

        // update accounts
        if (accountsJSONArray != null) {
            accountHashMap.clear();
            for (JSONObject accountJSONObject : accountsJSONArray.toArray(JSONObject.class)) {
                Account account = accountJSONObject.to(Account.class);
                accountHashMap.put(account.getAccountId(), account);
            }
        }
    }

    private void updateQuotations() {
        // get quotations
        JSONArray quotationsJSONArray = null;
        try (Response response = client.newCall(createGetRequest("/v1/hitokoto/quotations")).execute()) {
            if (response.body() != null) {
                quotationsJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("quotations");
            }
        } catch (IOException exception) {
            logger.error("Failed to get quotations", exception);
        }

        // update quotations
        if (quotationsJSONArray != null) {
            quotations.clear();
            for (JSONObject quotation : quotationsJSONArray.toArray(JSONObject.class)) {
                quotations.add(quotation.getString("sourceName") + "：" + quotation.getString("content"));
            }
        }
    }

    private void updateMinecraftProfiles() {
        // get minecraftProfiles
        JSONArray minecraftProfilesJSONArray = null;
        try (Response response = client.newCall(createGetRequest("/v1/minecraft/minecraftProfiles?getAll=true")).execute()) {
            if (response.body() != null) {
                minecraftProfilesJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("minecraftProfiles");
            }
        } catch (IOException exception) {
            logger.error("Failed to get minecraftProfiles", exception);
        }

        // update minecraftProfiles
        if (minecraftProfilesJSONArray != null) {
            minecraftProfileHashMap.clear();
            for (JSONObject minecraftProfileJSONObject : minecraftProfilesJSONArray.toArray(JSONObject.class)) {
                MinecraftProfile minecraftProfile = minecraftProfileJSONObject.to(MinecraftProfile.class);
                minecraftProfileHashMap.put(minecraftProfile.getUuid(), minecraftProfile);
            }
        }
    }

    private void updateServers() {
        // get servers
        JSONArray serversJSONArray = null;
        try (Response response = client.newCall(createGetRequest("/v1/minecraft/servers")).execute()) {
            if (response.body() != null) {
                serversJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("servers");
            }
        } catch (IOException exception) {
            logger.error("Failed to get servers", exception);
        }

        // update servers
        if (serversJSONArray != null) {
            serverInfoHashMap.clear();
            for (JSONObject server : serversJSONArray.toArray(JSONObject.class)) {
                serverInfoHashMap.put(
                        server.getString("serverName"),
                        new ServerInfo(
                                server.getString("serverName"),
                                server.getString("givenName"),
                                LocalDate.parse(server.getString("foundationDate")),
                                server.getInteger("priority")
                        )
                );
            }
        }
    }

    private void updateServerGroups() {
        // get servers
        JSONArray serverGroupsJSONArray = null;
        try (Response response = client.newCall(createGetRequest("/v1/minecraft/serverGroups?withServers=true&withAccounts=true")).execute()) {
            if (response.body() != null) {
                serverGroupsJSONArray = JSONObject
                        .parseObject(response.body().string())
                        .getJSONObject("data")
                        .getJSONArray("serverGroups");
            }
        } catch (IOException exception) {
            logger.error("Failed to get serverGroups", exception);
        }

        // update servers
        if (serverGroupsJSONArray != null) {
            serverGroupHashMap.clear();
            for (JSONObject serverGroupJSONObject : serverGroupsJSONArray.toArray(JSONObject.class)) {
                ServerGroup serverGroup = serverGroupJSONObject.to(ServerGroup.class);
                serverGroupHashMap.put(serverGroup.getGroupId(), serverGroup);
            }
        }
    }

    public Optional<ServerInfo> getServerInfo(String serverName) {
        return Optional.ofNullable(serverInfoHashMap.get(serverName));
    }

    public String getRandomQuotation() {
        if (!quotations.isEmpty()) {
            return quotations.get((int) (Math.random() * quotations.size()));
        } else {
            return "";
        }
    }
}
