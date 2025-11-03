package net.jadenxgamer.elysium_api.api.roll;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class DodgeRoll {

    private static final double dodgeRollPower = 2.0;

    public static void dodgeRoll(ServerPlayer player, boolean invulnerable) {
        if (!player.onGround()) return;
        if (invulnerable) player.invulnerableTime = 10;
        applyMovement(player);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new DodgeRollPayload());
    }

    public static void applyMovement(Player player) {
        Vec3 vector = player.getForward().multiply(dodgeRollPower, 0.0f, dodgeRollPower);
        Elysium.LOGGER.info("{}", vector);
        player.addDeltaMovement(vector);
    }

}
