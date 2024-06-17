package org.bsrserver.altair.velocity.hitokoto;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import org.bsrserver.altair.velocity.AltairVelocity;
import org.bsrserver.altair.velocity.data.DataManager;

public class HitokotoCommandFactory {
    private static void sendRandomQuotations(AltairVelocity altairVelocity, CommandSource source, int count) {
        DataManager dataManager = altairVelocity.getDataManager();
        for (int i = 0; i < count; i++) {
            source.sendMessage(Component.text("[§a一言§r] " + dataManager.getRandomQuotation()));
        }
    }

    public static LiteralCommandNode<CommandSource> createHitokotoCommand(AltairVelocity altairVelocity) {
        return BrigadierCommand.literalArgumentBuilder("hitokoto")
                .executes(context -> {
                    context.getSource().sendPlainMessage("Usage: /altair hitokoto random [count]");
                    return Command.SINGLE_SUCCESS;
                })
                .then(
                        BrigadierCommand.literalArgumentBuilder("random")
                                .executes(context -> {
                                    sendRandomQuotations(altairVelocity, context.getSource(), 1);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .then(
                                        BrigadierCommand.requiredArgumentBuilder("count", IntegerArgumentType.integer(1, 10))
                                                .executes(context -> {
                                                    sendRandomQuotations(altairVelocity, context.getSource(), IntegerArgumentType.getInteger(context, "count"));
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                )
                )
                .build();
    }
}
