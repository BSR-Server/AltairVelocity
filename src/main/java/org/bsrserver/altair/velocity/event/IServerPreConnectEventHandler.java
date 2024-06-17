package org.bsrserver.altair.velocity.event;

import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import org.bsrserver.altair.velocity.AltairVelocity;

public interface IServerPreConnectEventHandler {
    void onServerPreConnectedEvent(AltairVelocity altairVelocity, ServerPreConnectEvent event);
}
