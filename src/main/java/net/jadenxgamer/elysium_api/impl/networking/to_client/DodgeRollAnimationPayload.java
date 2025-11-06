package net.jadenxgamer.elysium_api.impl.networking.to_client;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.client.animation.Animation;
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

public record DodgeRollAnimationPayload(int id) implements CustomPacketPayload {

    public static final Type<DodgeRollAnimationPayload> TYPE = new Type<>(Elysium.id("dodge_roll_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DodgeRollAnimationPayload> CODEC = ByteBufCodecs.INT
            .map(DodgeRollAnimationPayload::new, DodgeRollAnimationPayload::id).cast();

    @Override
    public @NotNull Type<DodgeRollAnimationPayload> type() {
        return TYPE;
    }

    public void handleDataOnClient(IPayloadContext ignoredContext) {
        Optional.ofNullable(Minecraft.getInstance().level).ifPresent(level -> {
            Entity entity = level.getEntity(id);
            if (!(entity instanceof RemotePlayer player)) {
                Elysium.LOGGER.info("Not a RemotePlayer!");
                return;
            }

            Animation.DODGE_ROLL.play(player);
        });
    }
}
