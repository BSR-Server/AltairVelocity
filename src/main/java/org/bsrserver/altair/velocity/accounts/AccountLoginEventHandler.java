package org.bsrserver.altair.velocity.accounts;

import com.velocitypowered.api.event.connection.LoginEvent;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.event.ILoginEventHandler;

public class AccountLoginEventHandler implements ILoginEventHandler {
    @Override
    public void onLoginEvent(AltairVelocity altairVelocity, LoginEvent event) {
        altairVelocity.getAccountOnlinePlayersManager().login(event.getPlayer().getUniqueId());
    }
}
