package net.jadenxgamer.elysium_api.impl.networking.to_client;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.roll.DodgeRoll;
import net.jadenxgamer.elysium_api.impl.client.animation.AnimationControllers;
import net.jadenxgamer.elysium_api.impl.client.animation.Animations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DodgeRollPayload() implements CustomPacketPayload {

    public static final Type<DodgeRollPayload> TYPE = new Type<>(Elysium.id("dodge_roll"));

    public static final StreamCodec<FriendlyByteBuf, DodgeRollPayload> CODEC = StreamCodec.unit(new DodgeRollPayload());

    @Override
    public @NotNull Type<DodgeRollPayload> type() {
        return TYPE;
    }

    public void handleDataOnClient(IPayloadContext context) {
        if (!(context.player() instanceof AbstractClientPlayer clientPlayer)) {
            Elysium.LOGGER.error("IPayloadContext#player() is not an AbstractClientPlayer!");
            return;
        }
        DodgeRoll.applyMovement(clientPlayer);

        PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(clientPlayer, AnimationControllers.MOVEMENT);
        if (controller == null) {
            Elysium.LOGGER.error("AnimationController \"elysium:movement\" is null!");
            return;
        }
        controller.triggerAnimation(Animations.DODGE_ROLL);
    }
}
