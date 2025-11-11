package net.jadenxgamer.elysium_api.impl.networking.to_client;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.networking.ElysiumPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record SyncDataAttachmentPayload<T>(AttachmentType<T> attachmentType, T value) implements CustomPacketPayload {

    public static final Type<SyncDataAttachmentPayload<?>> TYPE = new Type<>(Elysium.id("sync_data_attachment"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDataAttachmentPayload<?>> CODEC = ResourceLocation.STREAM_CODEC
            .<RegistryFriendlyByteBuf>cast()
            .<AttachmentType<?>>map(
                    NeoForgeRegistries.ATTACHMENT_TYPES::get,
                    NeoForgeRegistries.ATTACHMENT_TYPES::getKey
            ).dispatch(
                    SyncDataAttachmentPayload::attachmentType,
                    SyncDataAttachmentPayload::codecFromAttachmentType
            );

    private static <T> StreamCodec<RegistryFriendlyByteBuf, SyncDataAttachmentPayload<T>> codecFromAttachmentType(AttachmentType<T> type) {
        return StreamCodec.composite(
                StreamCodec.unit(type), SyncDataAttachmentPayload::attachmentType,
                ByteBufCodecs.fromCodec(ElysiumPayloads.getAttachmentCodec(type)).cast(), SyncDataAttachmentPayload::value,
                SyncDataAttachmentPayload::new
        );
    }

    public static <T> void sync(ServerPlayer player, Supplier<AttachmentType<T>> type) {
        sync(player, type.get());
    }

    public static <T> void sync(ServerPlayer player, AttachmentType<T> type) {
        PacketDistributor.sendToPlayer(player, new SyncDataAttachmentPayload<>(type, player.getData(type)));
    }

    @Override
    public @NotNull Type<SyncDataAttachmentPayload<?>> type() {
        return TYPE;
    }

    public void handleDataOnClient(IPayloadContext ignored) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.setData(attachmentType, value);
        }
    }
}
