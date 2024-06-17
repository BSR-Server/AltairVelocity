package org.bsrserver.altair.velocity.event;

import com.velocitypowered.api.event.connection.LoginEvent;
import org.bsrserver.altair.velocity.AltairVelocity;

public interface ILoginEventHandler {
    void onLoginEvent(AltairVelocity altairVelocity, LoginEvent event);
}
