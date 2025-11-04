package net.jadenxgamer.elysium_api.impl.networking.to_server;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollAnimationPayload;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttributes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalDouble;

public final class DodgeRollPayload implements CustomPacketPayload {

    public static final Type<DodgeRollPayload> TYPE = new Type<>(Elysium.id("dodge_roll_server"));

    public static final DodgeRollPayload INSTANCE = new DodgeRollPayload();

    public static final StreamCodec<FriendlyByteBuf, DodgeRollPayload> CODEC = StreamCodec.unit(INSTANCE);

    private DodgeRollPayload() {

    }

    @Override
    public @NotNull Type<DodgeRollPayload> type() {
        return TYPE;
    }

    public void handleDataOnServer(IPayloadContext context) {
        Player player = context.player();
        player.invulnerableTime = 10;

        OptionalDouble.of(player.getAttributeValue(ElysiumAttributes.DODGE_POWER))
                .ifPresent(p -> player.addDeltaMovement(player.getForward().multiply(p, 0.0f, p)));

        PacketDistributor.sendToPlayersTrackingEntity(context.player(), new DodgeRollAnimationPayload(context.player().getId()));
    }
}
