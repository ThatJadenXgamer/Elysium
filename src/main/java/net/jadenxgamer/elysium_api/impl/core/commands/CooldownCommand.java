package net.jadenxgamer.elysium_api.impl.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CooldownCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cooldowntick").then(
                Commands.argument("player", EntityArgument.player()).executes(context -> {
                    Player player = EntityArgument.getPlayer(context, "player");
                    int cooldownTick = player.getData(ElysiumAttachmentTypes.COOLDOWN_TICK);
                    context.getSource().sendSuccess(() -> Component.literal("Current cooldown tick: " + cooldownTick), true);
                    return 1;
                })
        ));
    }
}
