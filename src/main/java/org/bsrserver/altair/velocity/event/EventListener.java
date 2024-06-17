package org.bsrserver.altair.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.accounts.AccountDisconnectedEventHandler;
import org.bsrserver.altair.velocity.accounts.AccountLoginEventHandler;
import org.bsrserver.altair.velocity.credential.CredentialServerConnectedEventHandler;
import org.bsrserver.altair.velocity.greeter.GreeterServerConnectedEventHandler;
import org.bsrserver.altair.velocity.whitelist.WhitelistEventHandler;

import java.util.ArrayList;
import java.util.List;

public class EventListener {
    private final AltairVelocity altairVelocity;
    private final List<ILoginEventHandler> loginEventHandlers;
    private final List<IServerPreConnectEventHandler> serverPreConnectEventHandlers;
    private final List<IServerConnectedEventHandler> serverConnectedHandlers;
    private final List<IDisconnectEventHandler> disconnectEventHandlers;

    public EventListener(AltairVelocity altairVelocity) {
        // init
        this.altairVelocity = altairVelocity;
        this.loginEventHandlers = new ArrayList<>();
        this.serverPreConnectEventHandlers = new ArrayList<>();
        this.serverConnectedHandlers = new ArrayList<>();
        this.disconnectEventHandlers = new ArrayList<>();

        // login event handlers
        loginEventHandlers.add(new WhitelistEventHandler());
        loginEventHandlers.add(new AccountLoginEventHandler());

        // server pre connect event handlers
        serverPreConnectEventHandlers.add(new WhitelistEventHandler());

        // server connected event handlers
        serverConnectedHandlers.add(new GreeterServerConnectedEventHandler());
        serverConnectedHandlers.add(new CredentialServerConnectedEventHandler());

        // disconnected event handlers
        disconnectEventHandlers.add(new AccountDisconnectedEventHandler());
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        for (ILoginEventHandler loginEventHandler : loginEventHandlers) {
            loginEventHandler.onLoginEvent(altairVelocity, event);
        }
    }

    @Subscribe
    public void onServerPreConnectEvent(ServerPreConnectEvent event) {
        for (IServerPreConnectEventHandler serverPreConnectEventHandler : serverPreConnectEventHandlers) {
            serverPreConnectEventHandler.onServerPreConnectedEvent(altairVelocity, event);
        }
    }

    @Subscribe(order = PostOrder.LATE)
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        for (IServerConnectedEventHandler serverConnectedHandler : serverConnectedHandlers) {
            serverConnectedHandler.onServerConnectedEvent(altairVelocity, event);
        }
    }

    @Subscribe
    public void onDisconnectedEvent(DisconnectEvent event) {
        for (IDisconnectEventHandler disconnectEventHandler : disconnectEventHandlers) {
            disconnectEventHandler.onDisconnectedEvent(altairVelocity, event);
        }
    }
}
