package org.bsrserver.altair.velocity.credential;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.velocitypowered.api.proxy.Player;
import org.bsrserver.altair.velocity.AltairVelocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;

public class CredentialDataManager {
    private final AltairVelocity altairVelocity;
    private final File dataFile;
    private JSONObject users;

    public CredentialDataManager(AltairVelocity plugin) {
        this.altairVelocity = plugin;

        // load data files
        dataFile = new File(this.altairVelocity.getDataDirectory().toAbsolutePath().toString(), "defaultCredentials.json");
        if (!dataFile.exists()) {
            this.altairVelocity.getLogger().error("Cannot find data file: " + dataFile.getAbsolutePath());
            return;
        }

        // read data files
        try {
            users = JSON.parseObject(dataFile.toURI().toURL());
        } catch (MalformedURLException ignored) {
        }
    }

    private void saveData() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dataFile);
            JSON.writeTo(fileOutputStream, users, JSONWriter.Feature.PrettyFormat);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsername(UUID uuid) {
        return altairVelocity.getProxyServer().getPlayer(uuid).map(Player::getUsername).orElse("Unknown");
    }

    public Optional<Credential> getCredential(UUID uuid) {
        if (users == null) {
            return Optional.empty();
        }

        JSONObject user = users.getJSONObject(uuid.toString());
        if (user != null) {
            String password = user.getString("password");
            if (password == null) {
                return Optional.empty();
            } else {
                return Optional.of(new Credential(
                        user.getString("username"),
                        user.getString("password"),
                        user.getBoolean("got_password")
                ));
            }
        } else {
            return Optional.empty();
        }
    }

    public void setGot(UUID uuid) {
        if (users != null) {
            JSONObject user = users.getJSONObject(uuid.toString());
            if (user != null) {
                user.put("got_password", true);
                saveData();
            }
        }
    }
}
