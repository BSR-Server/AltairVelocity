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
import java.util.concurrent.TimeUnit;

public class DataManager {
    private final AltairVelocity altairVelocity;
    private final Logger logger;
    private final OkHttpClient client = new OkHttpClient();
    private final ArrayList<String> quotations = new ArrayList<>();
    private final HashMap<String, ServerInfo> servers = new HashMap<>();

    public DataManager(AltairVelocity altairVelocity) {
        this.altairVelocity = altairVelocity;
        this.logger = altairVelocity.getLogger();
        altairVelocity.getScheduledExecutorService().scheduleAtFixedRate(this::scheduledTask, 0, 30, TimeUnit.SECONDS);
    }

    private void scheduledTask() {
        try {
            updateQuotations();
            updateServers();
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

    private void updateQuotations() {
        // clear
        quotations.clear();

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

        // save quotations
        if (quotationsJSONArray != null) {
            for (JSONObject quotation : quotationsJSONArray.toArray(JSONObject.class)) {
                quotations.add(quotation.getString("sourceName") + "：" + quotation.getString("content"));
            }
        }
    }

    private void updateServers() {
        // clear
        servers.clear();

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

        // save servers
        if (serversJSONArray != null) {
            for (JSONObject server : serversJSONArray.toArray(JSONObject.class)) {
                servers.put(
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

    public Optional<ServerInfo> getServerInfo(String serverName) {
        return Optional.ofNullable(servers.get(serverName));
    }

    public String getRandomQuotation() {
        if (!quotations.isEmpty()) {
            return quotations.get((int) (Math.random() * quotations.size()));
        } else {
            return "";
        }
    }
}
