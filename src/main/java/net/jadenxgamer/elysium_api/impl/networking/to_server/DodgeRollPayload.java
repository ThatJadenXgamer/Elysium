package net.jadenxgamer.elysium_api.impl.networking.to_server;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.client.dodge.DodgeRoll;
import net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollAnimationPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public final class DodgeRollPayload implements CustomPacketPayload {

    public static final Type<DodgeRollPayload> TYPE = new Type<>(Elysium.id("dodge_roll_server"));

    public static final StreamCodec<FriendlyByteBuf, DodgeRollPayload> CODEC = StreamCodec.unit(new DodgeRollPayload());

    @Override
    public @NotNull Type<DodgeRollPayload> type() {
        return TYPE;
    }

    public void handleDataOnServer(IPayloadContext context) {
        Player player = context.player();
        player.invulnerableTime = 10;
        DodgeRoll.applyMovement(player);
        PacketDistributor.sendToPlayersTrackingEntity(context.player(), new DodgeRollAnimationPayload(context.player().getId()));
    }
}
