package org.bsrserver.altair.velocity.credential;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bsrserver.altair.velocity.AltairVelocity;

import java.util.Optional;
import java.util.UUID;

public class CredentialCommandFactory {
    private static Component getCopiableMessage(String text) {
        return Component.text("[%s]".formatted(text))
                .color(NamedTextColor.GREEN)
                .hoverEvent(Component.text("Click to copy"))
                .clickEvent(ClickEvent.copyToClipboard(text));
    }

    private static Component noCredentialMessage() {
        return Component
                .text("You do not have a default credential, please contact administrators.")
                .color(NamedTextColor.YELLOW);
    }

    private static Component getCredentialMessage(Credential credential) {
        final String GLOBAL_LOGIN_URL = "https://bsrserver.org:8443/login";
        final String CHINA_LOGIN_URL = "https://bsrserver.org.cn:8443/login";
        Component message = Component.empty();

        // append credential
        message = message
                .append(Component.text("Your default altair credential is: "))
                .append(Component.text("\n"))
                .append(Component.text("Username: "))
                .append(getCopiableMessage(credential.username()))
                .append(Component.text("\n"))
                .append(Component.text("Password: "))
                .append(getCopiableMessage(credential.password()))
                .append(Component.text("\n"));

        // altair url
        message = message
                .append(
                        Component
                                .text("Click to visit the website: ")
                                .color(NamedTextColor.WHITE)
                )
                .append(
                        Component
                                .text("[GLOBAL]")
                                .color(NamedTextColor.GREEN)
                                .hoverEvent(Component.text("Click to visit " + GLOBAL_LOGIN_URL))
                                .clickEvent(ClickEvent.openUrl(GLOBAL_LOGIN_URL))
                )
                .append(
                        Component
                                .text(" and ")
                                .color(NamedTextColor.WHITE)
                )
                .append(
                        Component
                                .text("[CHINA]")
                                .color(NamedTextColor.GREEN)
                                .hoverEvent(Component.text("Click to visit " + CHINA_LOGIN_URL))
                                .clickEvent(ClickEvent.openUrl(CHINA_LOGIN_URL))
                );

        // return message
        return message;
    }

    public static LiteralCommandNode<CommandSource> createCredentialCommand(AltairVelocity altairVelocity) {
        CredentialDataManager credentialDataManager = altairVelocity.getCredentialDataManager();
        return BrigadierCommand.literalArgumentBuilder("credential")
                .requires(source -> source instanceof Player)
                .executes(context -> {
                    context.getSource().sendMessage(
                            Component.text("----- Credential Command Help -----\n", NamedTextColor.GREEN)
                                    .append(Component.text("/altair credential get - Get default altair credential\n", NamedTextColor.WHITE))
                                    .append(Component.text("/altair credential avoidWelcomeMessage - Avoid welcome message", NamedTextColor.WHITE))
                    );
                    return Command.SINGLE_SUCCESS;
                })
                .then(
                        BrigadierCommand.literalArgumentBuilder("get")
                                .executes(context -> {
                                    CommandSource source = context.getSource();
                                    UUID uuid = ((Player) source).getUniqueId();

                                    // send credential
                                    Optional<Credential> credential = credentialDataManager.getCredential(uuid);
                                    if (credential.isPresent()) {
                                        source.sendMessage(getCredentialMessage(credential.get()));
                                        credentialDataManager.setGot(uuid);
                                    } else {
                                        source.sendMessage(noCredentialMessage());
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build()
                )
                .then(
                        BrigadierCommand.literalArgumentBuilder("avoidWelcomeMessage")
                                .executes(context -> {
                                    CommandSource source = context.getSource();
                                    UUID uuid = ((Player) source).getUniqueId();

                                    // set got
                                    Optional<Credential> credential = credentialDataManager.getCredential(uuid);
                                    if (credential.isPresent()) {
                                        source.sendMessage(
                                                Component
                                                        .text("You will no longer receive this message at login.")
                                                        .color(NamedTextColor.GREEN)
                                        );
                                        credentialDataManager.setGot(uuid);
                                    } else {
                                        source.sendMessage(noCredentialMessage());
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build()
                )
                .build();
    }
}
