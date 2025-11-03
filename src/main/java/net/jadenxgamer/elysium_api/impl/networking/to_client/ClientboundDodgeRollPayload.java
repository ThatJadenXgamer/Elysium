package net.jadenxgamer.elysium_api.impl.networking.to_client;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.animation.AnimationControllers;
import net.jadenxgamer.elysium_api.impl.client.animation.Animations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ClientboundDodgeRollPayload(int id) implements CustomPacketPayload {

    public static final Type<ClientboundDodgeRollPayload> TYPE = new Type<>(Elysium.id("dodge_roll_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDodgeRollPayload> CODEC = ByteBufCodecs.INT
            .map(ClientboundDodgeRollPayload::new, ClientboundDodgeRollPayload::id).cast();

    @Override
    public @NotNull Type<ClientboundDodgeRollPayload> type() {
        return TYPE;
    }

    public void handleDataOnClient(IPayloadContext ignoredContext) {
        Optional.ofNullable(Minecraft.getInstance().level).ifPresent(level -> {
            Entity entity = level.getEntity(id);
            if (!(entity instanceof RemotePlayer player)) {
                Elysium.LOGGER.info("Not a RemotePlayer!");
                return;
            }
            PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(player, AnimationControllers.MOVEMENT);
            if (controller == null) {
                Elysium.LOGGER.error("AnimationController \"elysium:movement\" is null!");
                return;
            }
            controller.triggerAnimation(Animations.DODGE_ROLL);
        });
    }
}
