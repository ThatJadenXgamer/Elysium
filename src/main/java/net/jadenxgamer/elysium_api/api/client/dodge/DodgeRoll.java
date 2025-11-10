package net.jadenxgamer.elysium_api.api.client.dodge;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.ElysiumFeatures;
import net.jadenxgamer.elysium_api.impl.client.animation.Animation;
import net.jadenxgamer.elysium_api.impl.networking.to_server.DodgeRollPayload;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class DodgeRoll {

    public static void dodgeRoll() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            Elysium.LOGGER.error("Client Player");
            return;
        }

        if (ElysiumFeatures.DODGE_ROLL.canDodge(player)) {

            player.getData(ElysiumAttachmentTypes.DODGE_COOLDOWN).set();
            Animation.DODGE_ROLL.play(player);

            player.addDeltaMovement(Vec3.directionFromRotation(0, player.getYRot())
                    .scale(player.getAttributeValue(ElysiumAttributes.DODGE_POWER))
                    .scale(player.getSpeed() * 10));

            PacketDistributor.sendToServer(DodgeRollPayload.INSTANCE);
        }
    }

}
