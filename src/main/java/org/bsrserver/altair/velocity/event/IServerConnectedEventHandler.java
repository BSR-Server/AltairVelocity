package org.bsrserver.altair.velocity.event;

import com.velocitypowered.api.event.player.ServerConnectedEvent;
import org.bsrserver.altair.velocity.AltairVelocity;

public interface IServerConnectedEventHandler {
    void onServerConnectedEvent(AltairVelocity altairVelocity, ServerConnectedEvent event);
}
