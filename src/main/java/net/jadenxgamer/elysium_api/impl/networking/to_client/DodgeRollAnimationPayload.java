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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record DodgeRollAnimationPayload(int id) implements CustomPacketPayload {

    public static final Type<DodgeRollAnimationPayload> TYPE = new Type<>(Elysium.elysiumPath("dodge_roll_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DodgeRollAnimationPayload> CODEC = ByteBufCodecs.INT
            .map(DodgeRollAnimationPayload::new, DodgeRollAnimationPayload::id).cast();

    @Override
    public @NotNull Type<DodgeRollAnimationPayload> type() {
        return TYPE;
    }

    public void handleDataOnClient(IPayloadContext ignoredContext) {
        if (FMLEnvironment.dist.isClient()) Client.handleDodgeRollAnimation(this,  ignoredContext);
    }

    @OnlyIn(Dist.CLIENT)
    private static class Client {

        public static void handleDodgeRollAnimation(final DodgeRollAnimationPayload payload, final IPayloadContext context) {
            Optional.ofNullable(Minecraft.getInstance().level).ifPresent(level -> {
                Entity entity = level.getEntity(payload.id());
                if (!(entity instanceof RemotePlayer player)) {
                    Elysium.LOGGER.info("Not a RemotePlayer!");
                    return;
                }
                Animation.DODGE_ROLL.play(player);
            });
        }
    }
}
