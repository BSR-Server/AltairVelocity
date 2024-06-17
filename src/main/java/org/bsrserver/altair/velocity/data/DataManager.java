package org.bsrserver.altair.velocity.data;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.slf4j.Logger;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DataManager {
    private final OkHttpClient client = new OkHttpClient();
    private final AltairVelocity altairVelocity;
    private final Logger logger;

    private final File cacheFile;
    private final HashMap<Integer, Account> accountHashMap = new HashMap<>();
    private final List<String> quotations = new ArrayList<>();
    private final HashMap<UUID, MinecraftProfile> minecraftProfileHashMap = new HashMap<>();
    private final HashMap<String, ServerInfo> serverInfoHashMap = new HashMap<>();
    private final List<ServerGroup> serverGroups = new ArrayList<>();

    public DataManager(AltairVelocity altairVelocity) {
        this.altairVelocity = altairVelocity;
        this.logger = altairVelocity.getLogger();
        this.cacheFile = new File(altairVelocity.getDataDirectory().toAbsolutePath().toString(), "dataCache.dat");

        // init data from cache
        readFromFile();

        // scheduled task
        altairVelocity
                .getScheduledExecutorService()
                .scheduleWithFixedDelay(
                        this::scheduledTask,
                        0,
                        60,
                        TimeUnit.SECONDS
                );
    }

    private Cache getCache() {
        return Cache
                .builder()
                .accountHashMap(accountHashMap)
                .quotations(quotations)
                .minecraftProfileHashMap(minecraftProfileHashMap)
                .serverInfoHashMap(serverInfoHashMap)
                .serverGroups(serverGroups)
                .build();
    }

    private void saveToFile() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            objectOutputStream.writeObject(getCache());
        } catch (IOException e) {
            logger.error("Failed to save cache to file.", e);
        }
    }

    private void readFromFile() {
        if (cacheFile.exists()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(cacheFile))) {
                // read cache
                Cache cache = (Cache) objectInputStream.readObject();

                // clear data
                accountHashMap.clear();
                quotations.clear();
                minecraftProfileHashMap.clear();
                serverInfoHashMap.clear();
                serverGroups.clear();

                // fill data
                accountHashMap.putAll(cache.getAccountHashMap());
                quotations.addAll(cache.getQuotations());
                minecraftProfileHashMap.putAll(cache.getMinecraftProfileHashMap());
                serverInfoHashMap.putAll(cache.getServerInfoHashMap());
                serverGroups.addAll(cache.getServerGroups());
            } catch (Exception e) {
                logger.error("Failed to read cache from file.", e);
            }
        }
    }

    private void scheduledTask() {
        this.updateData();
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
            for (JSONObject serverJSONObject : serversJSONArray.toArray(JSONObject.class)) {
                serverInfoHashMap.put(
                        serverJSONObject.getString("serverName"),
                        new ServerInfo(
                                serverJSONObject.getInteger("serverId"),
                                serverJSONObject.getString("serverName"),
                                serverJSONObject.getString("givenName"),
                                LocalDate.parse(serverJSONObject.getString("foundationDate")),
                                serverJSONObject.getInteger("priority")
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
            serverGroups.clear();
            for (JSONObject serverGroupJSONObject : serverGroupsJSONArray.toArray(JSONObject.class)) {
                ServerGroup serverGroup = serverGroupJSONObject.to(ServerGroup.class);
                serverGroup.setParentObjects(new ArrayList<>());
                serverGroup.setServerObjects(new ArrayList<>());
                serverGroups.add(serverGroup);
            }
        }
    }

    private void fillServerGroups() {
        for (ServerGroup serverGroup : serverGroups) {
            // parents
            serverGroup.getParentObjects().addAll(
                    serverGroups
                            .stream()
                            .filter(parent -> serverGroup
                                    .getParents()
                                    .contains(parent.getGroupId())
                            )
                            .toList()
            );

            // servers
            serverGroup.getServerObjects().addAll(
                    serverInfoHashMap
                            .values()
                            .stream()
                            .filter(serverInfo -> serverGroup
                                    .getServers()
                                    .contains(serverInfo.getServerId())
                            )
                            .toList()
            );
        }
    }

    synchronized public void updateData() {
        // update data
        try {
            CompletableFuture
                    .allOf(
                            CompletableFuture.runAsync(this::updateAccounts),
                            CompletableFuture.runAsync(this::updateQuotations),
                            CompletableFuture.runAsync(this::updateMinecraftProfiles),
                            CompletableFuture.runAsync(this::updateServers),
                            CompletableFuture.runAsync(this::updateServerGroups)
                    )
                    .get();
        } catch (Exception e) {
            altairVelocity.getLogger().error("Failed to get update some data", e);
        }

        // save to file
        saveToFile();

        // fill objects to serverGroups
        fillServerGroups();
    }

    public String getDebugInfo() {
        return "Cache: " + getCache().toString();
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

    public Optional<Account> getAccount(UUID uuid) {
        // get minecraft profile
        MinecraftProfile minecraftProfile = minecraftProfileHashMap.get(uuid);
        if (minecraftProfile == null || minecraftProfile.getScheduledDeletionAt() != null) {
            return Optional.empty();
        }

        // get account
        return Optional.ofNullable(accountHashMap.get(minecraftProfile.getAccountId()));
    }

    public boolean isWhitelisted(UUID uuid, String serverName) {
        // get account
        Integer accountId = getAccount(uuid).map(Account::getAccountId).orElse(null);
        if (accountId == null) {
            return false;
        }

        // check server group
        for (ServerGroup serverGroup : serverGroups) {
            if (
                    serverGroup.getAccounts().contains(accountId)
                            && serverGroup.getAllServers().stream().anyMatch(serverInfo -> serverInfo.getServerName().equals(serverName))
            ) {
                return true;
            }
        }

        // default
        return false;
    }
}
