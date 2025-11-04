package net.jadenxgamer.elysium_api.api.client.dodge;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.animation.AnimationControllers;
import net.jadenxgamer.elysium_api.impl.client.animation.Animations;
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
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, AnimationControllers.MOVEMENT);
        if (controller == null) {
            Elysium.LOGGER.error("AnimationController \"elysium:movement\" is null!");
            return;
        }

        controller.triggerAnimation(Animations.DODGE_ROLL);

        player.addDeltaMovement(player.getForward()
                .multiply(1, 0, 1)
                .scale(player.getAttributeValue(ElysiumAttributes.DODGE_POWER))
                .scale(player.getSpeed() * 10));

        PacketDistributor.sendToServer(DodgeRollPayload.INSTANCE);
    }

}
