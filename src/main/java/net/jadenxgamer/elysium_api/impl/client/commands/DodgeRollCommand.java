package net.jadenxgamer.elysium_api.impl.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.jadenxgamer.elysium_api.api.client.dodge.DodgeRoll;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DodgeRollCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dodgeroll").executes(context -> {
            DodgeRoll.dodgeRoll();
            return 1;
        }));
    }
}
