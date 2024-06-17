package org.bsrserver.altair.velocity.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.credential.CredentialCommandFactory;
import org.bsrserver.altair.velocity.data.DataCommandFactory;
import org.bsrserver.altair.velocity.hitokoto.HitokotoCommandFactory;

public class CommandFactory {
    private static Component getHelpMessage() {
        return Component.text("----- Command Help -----", NamedTextColor.GREEN)
                .append(Component.text("\n"))
                .append(Component.text("/altair data - Data commands\n"))
                .append(Component.text("/altair credential - Default altair credential\n"))
                .append(Component.text("/altair hitokoto - Hitokoto commands\n"))
                .append(Component.text("/altair - Show help message"));
    }

    public static BrigadierCommand createRootCommand(AltairVelocity altairVelocity) {
        LiteralCommandNode<CommandSource> rootCommandNode = BrigadierCommand.literalArgumentBuilder("altair")
                .executes(context -> {
                    context.getSource().sendMessage(getHelpMessage());
                    return Command.SINGLE_SUCCESS;
                })
                .then(DataCommandFactory.createDataCommand(altairVelocity))
                .then(CredentialCommandFactory.createCredentialCommand(altairVelocity))
                .then(HitokotoCommandFactory.createHitokotoCommand(altairVelocity))
                .build();
        return new BrigadierCommand(rootCommandNode);
    }
}
