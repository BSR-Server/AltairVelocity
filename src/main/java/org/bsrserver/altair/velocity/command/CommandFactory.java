package org.bsrserver.altair.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import org.bsrserver.altair.velocity.AltairVelocity;

public class CommandFactory {
    private static Component getHelpMessage() {
        return Component.text("----- Command Help -----")
                .append(Component.text("\n"))
                .append(Component.text("/altair credential - Default altair credential"));
    }

    public static BrigadierCommand createRootCommand(AltairVelocity altairVelocity) {
        LiteralCommandNode<CommandSource> rootCommandNode = BrigadierCommand.literalArgumentBuilder("altair")
                .executes(context -> {
                    context.getSource().sendMessage(getHelpMessage());
                    return Command.SINGLE_SUCCESS;
                })
                .build();
        return new BrigadierCommand(rootCommandNode);
    }
}