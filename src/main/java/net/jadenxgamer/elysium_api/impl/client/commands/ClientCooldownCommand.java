package net.jadenxgamer.elysium_api.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ClientCooldownCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cooldowntick").executes(context -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                int cooldownTick = player.getData(ElysiumAttachmentTypes.COOLDOWN_TICK);
                context.getSource().sendSuccess(() -> Component.literal("Current client cooldown tick: " + cooldownTick), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("player is null!"));
                return 0;
            }
        }));
    }
}
