package org.bsrserver.altair.velocity.event;

import com.velocitypowered.api.event.connection.DisconnectEvent;
import org.bsrserver.altair.velocity.AltairVelocity;

public interface IDisconnectEventHandler {
    void onDisconnectedEvent(AltairVelocity altairVelocity, DisconnectEvent event);
}
