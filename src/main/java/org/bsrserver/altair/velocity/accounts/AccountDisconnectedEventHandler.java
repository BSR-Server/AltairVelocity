package org.bsrserver.altair.velocity.accounts;

import com.velocitypowered.api.event.connection.DisconnectEvent;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.event.IDisconnectEventHandler;

public class AccountDisconnectedEventHandler implements IDisconnectEventHandler {
    @Override
    public void onDisconnectedEvent(AltairVelocity altairVelocity, DisconnectEvent event) {
        altairVelocity.getAccountOnlinePlayersManager().logout(event.getPlayer().getUniqueId());
    }
}
