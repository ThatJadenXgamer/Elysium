package net.jadenxgamer.elysium_api.api.roll;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class DodgeRoll {

    public static void dodgeRoll(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollPayload());
    }

}
