package net.jadenxgamer.elysium_api.api.client.dodge;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.animation.AnimationControllers;
import net.jadenxgamer.elysium_api.impl.client.animation.Animations;
import net.jadenxgamer.elysium_api.impl.networking.to_server.ServerboundDodgeRollPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class DodgeRoll {

    private static final double dodgeRollPower = 1.5d;

    public static void dodgeRoll() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            Elysium.LOGGER.error("Client Player");
            return;
        };
        if (!player.onGround()) return;
        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, AnimationControllers.MOVEMENT);
        if (controller == null) {
            Elysium.LOGGER.error("AnimationController \"elysium:movement\" is null!");
            return;
        }
        controller.triggerAnimation(Animations.DODGE_ROLL);
        applyMovement(player);
        PacketDistributor.sendToServer(new ServerboundDodgeRollPayload());
    }

    public static void applyMovement(Player player) {
        player.addDeltaMovement(player.getForward().multiply(dodgeRollPower, 0.0f, dodgeRollPower));
    }

}
