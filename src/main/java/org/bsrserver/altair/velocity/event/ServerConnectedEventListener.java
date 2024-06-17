package org.bsrserver.altair.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.credential.CredentialServerConnectedHandler;

import java.util.ArrayList;
import java.util.List;

public class ServerConnectedEventListener {
    private final AltairVelocity altairVelocity;
    private final List<IServerConnectedHandler> serverConnectedHandlers;

    public ServerConnectedEventListener(AltairVelocity altairVelocity) {
        // init
        this.altairVelocity = altairVelocity;
        this.serverConnectedHandlers = new ArrayList<>();

        // event handlers
        serverConnectedHandlers.add(new CredentialServerConnectedHandler());
    }

    @Subscribe(order = PostOrder.LATE)
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        for (IServerConnectedHandler serverConnectedHandler : serverConnectedHandlers) {
            serverConnectedHandler.onServerConnectedEvent(altairVelocity, event);
        }
    }
}
