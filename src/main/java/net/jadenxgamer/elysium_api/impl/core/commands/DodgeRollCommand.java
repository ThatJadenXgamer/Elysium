package net.jadenxgamer.elysium_api.impl.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.jadenxgamer.elysium_api.api.roll.DodgeRoll;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DodgeRollCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dodgeroll")
                .executes(context -> {
                    CommandSourceStack sourceStack = context.getSource();
                    if (!sourceStack.isPlayer()) {
                        sourceStack.sendFailure(Component.literal("Please provide a player!"));
                        return 0;
                    }
                    //noinspection DataFlowIssue
                    DodgeRoll.dodgeRoll(sourceStack.getPlayer(), true);
                    sourceStack.sendSuccess(() -> Component.literal("Get rolled."), true);
                    return 1;
                })
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            DodgeRoll.dodgeRoll(player, true);
                            context.getSource().sendSuccess(() -> Component.literal("Rolling."), true);
                            player.sendSystemMessage(Component.literal("Get rolled."), true);
                            return 1;
                        })));
    }
}
