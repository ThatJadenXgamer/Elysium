package net.jadenxgamer.elysium_api.api.client.dodge;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.animation.AnimationLayers;
import net.jadenxgamer.elysium_api.impl.client.animation.Animation;
import net.jadenxgamer.elysium_api.impl.networking.to_server.DodgeRollPayload;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
        if (!player.onGround()) return;

        Animation.DODGE_ROLL.play(player);

        player.addDeltaMovement(player.getForward()
                .multiply(1, 0, 1)
                .scale(player.getAttributeValue(ElysiumAttributes.DODGE_POWER))
                .scale(player.getSpeed() * 10));

        PacketDistributor.sendToServer(DodgeRollPayload.INSTANCE);
    }

}
