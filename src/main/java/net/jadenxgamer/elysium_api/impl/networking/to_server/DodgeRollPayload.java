package net.jadenxgamer.elysium_api.impl.networking.to_server;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.ElysiumFeatures;
import net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollAnimationPayload;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttachmentTypes;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumAttributes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

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
        if (ElysiumFeatures.DODGE_ROLL.canDodge(player)) {

            player.getData(ElysiumAttachmentTypes.DODGE_COOLDOWN).set();
            player.invulnerableTime = 10;

            player.getFoodData().addExhaustion(4f);

            player.addDeltaMovement(player.getForward()
                    .multiply(1, 0, 1)
                    .scale(player.getAttributeValue(ElysiumAttributes.DODGE_POWER))
                    .scale(player.getSpeed()));

            PacketDistributor.sendToPlayersTrackingEntity(context.player(), new DodgeRollAnimationPayload(context.player().getId()));
        }
    }
}
