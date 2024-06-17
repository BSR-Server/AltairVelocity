package org.bsrserver.altair.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.bsrserver.altair.velocity.command.CommandFactory;
import org.bsrserver.altair.velocity.config.Config;
import org.bsrserver.altair.velocity.credential.CredentialDataManager;
import org.bsrserver.altair.velocity.event.ServerConnectedEventListener;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Plugin(
        id = "altair-velocity",
        name = "Altair Velocity Plugin",
        version = "1.0.0",
        url = "https://bsrserver.org:8443/",
        description = "Altair Velocity Plugin",
        authors = {
                "BSR Server",
                "Andy Zhang",
        }
)
public class AltairVelocity {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;
    private final Config config;
    private final CredentialDataManager credentialDataManager;

    @Inject
    public AltairVelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        // save parameters
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        // check data directory
        if (!dataDirectory.toFile().exists()) {
            this.logger.warn("Created data directory: " + this.dataDirectory);
            this.dataDirectory.toFile().mkdir();
        }

        // init members
        this.config = new Config(dataDirectory);
        this.credentialDataManager = new CredentialDataManager(this);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        // register event
        proxyServer.getEventManager().register(this, new ServerConnectedEventListener(this));

        // register command
        proxyServer.getCommandManager().register(
                proxyServer
                        .getCommandManager()
                        .metaBuilder("altair")
                        .plugin(this)
                        .build(),
                CommandFactory.createRootCommand(this)
        );
    }
}
