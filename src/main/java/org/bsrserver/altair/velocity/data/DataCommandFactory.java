package org.bsrserver.altair.velocity.data;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bsrserver.altair.velocity.AltairVelocity;

import java.util.Optional;
import java.util.UUID;

public class DataCommandFactory {
    public static LiteralCommandNode<CommandSource> createDataCommand(AltairVelocity altairVelocity) {
        return BrigadierCommand.literalArgumentBuilder("data")
                .requires(source -> {
                    if (source instanceof ConsoleCommandSource) {
                        return true;
                    } else {
                        UUID uuid = ((Player) source).getUniqueId();
                        Optional<Account> accountOptional = altairVelocity.getDataManager().getAccount(uuid);
                        return accountOptional.isPresent() && accountOptional.get().hasRole(Role.ROLE_ADMIN);
                    }
                })
                .executes(context -> {
                    context.getSource().sendMessage(
                            Component.text("----- Data Command Help -----", NamedTextColor.GREEN)
                                    .append(Component.text("\n", NamedTextColor.WHITE))
                                    .append(Component.text("/altair data update - Update data"))
                    );
                    return Command.SINGLE_SUCCESS;
                })
                .then(
                        BrigadierCommand.literalArgumentBuilder("update")
                                .executes(context -> {
                                    altairVelocity.getDataManager().updateData();
                                    context.getSource().sendMessage(Component.text("Data updated", NamedTextColor.GREEN));
                                    return Command.SINGLE_SUCCESS;
                                })
                                .build()
                )
                .build();
    }
}
