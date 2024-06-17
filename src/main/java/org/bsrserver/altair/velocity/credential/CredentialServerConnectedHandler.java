package org.bsrserver.altair.velocity.credential;

import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.event.IServerConnectedHandler;

import java.util.Optional;
import java.util.UUID;

public class CredentialServerConnectedHandler implements IServerConnectedHandler {
    @Override
    public void onServerConnectedEvent(AltairVelocity altairVelocity, ServerConnectedEvent event) {
        if (event.getPreviousServer().isEmpty()) {
            UUID uuid = event.getPlayer().getUniqueId();
            Optional<Credential> credential = altairVelocity.getCredentialDataManager().getCredential(uuid);

            // return if credential is not present
            if (credential.isEmpty()) {
                return;
            }

            // send message
            if (!credential.get().isGot()) {
                // commands
                String GET_COMMAND = "/altair credential get";
                String AVOID_COMMAND = "/altair credential avoidWelcomeMessage";

                // message
                Component message = Component.text("-".repeat(40))
                        .append(Component.text("\n"))
                        .append(
                                Component
                                        .text("[IMPORTANT] ")
                                        .color(NamedTextColor.GREEN)
                        )
                        .append(
                                Component
                                        .text("BSR Website is now alive!")
                                        .color(NamedTextColor.WHITE)
                        )
                        .append(Component.text("\n"))
                        .append(
                                Component
                                        .text("Click")
                                        .color(NamedTextColor.WHITE)
                        )
                        .append(
                                Component
                                        .text(" [here] ")
                                        .color(NamedTextColor.GREEN)
                                        .hoverEvent(Component.text(GET_COMMAND))
                                        .clickEvent(ClickEvent.runCommand(GET_COMMAND))
                        )
                        .append(
                                Component
                                        .text("to show your default credential and website link.")
                                        .color(NamedTextColor.WHITE)
                        )
                        .append(Component.text("\n"))
                        .append(
                                Component
                                        .text("Click")
                                        .color(NamedTextColor.WHITE)
                        )
                        .append(
                                Component
                                        .text(" [here] ")
                                        .color(NamedTextColor.GREEN)
                                        .hoverEvent(Component.text(AVOID_COMMAND))
                                        .clickEvent(ClickEvent.runCommand(AVOID_COMMAND))
                        )
                        .append(
                                Component
                                        .text("to avoid always displaying this message at login.")
                                        .color(NamedTextColor.WHITE)
                        )
                        .append(Component.text("\n"))
                        .append(Component.text("-".repeat(40)));

                // send message to player
                event.getPlayer().sendMessage(message);
            }
        }
    }
}
