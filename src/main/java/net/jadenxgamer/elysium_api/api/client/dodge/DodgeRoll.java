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

import java.util.OptionalDouble;

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

        OptionalDouble.of(player.getAttributeValue(ElysiumAttributes.DODGE_POWER))
                .ifPresent(p -> player.addDeltaMovement(player.getForward().multiply(p, 0.0f, p)));

        PacketDistributor.sendToServer(DodgeRollPayload.INSTANCE);
    }

}
