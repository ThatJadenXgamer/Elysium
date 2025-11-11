package net.jadenxgamer.elysium_api.impl.networking;

import com.mojang.serialization.Codec;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.networking.to_client.DodgeRollAnimationPayload;
import net.jadenxgamer.elysium_api.impl.networking.to_client.ScreenFlashPayload;
import net.jadenxgamer.elysium_api.impl.networking.to_client.SyncDataAttachmentPayload;
import net.jadenxgamer.elysium_api.impl.networking.to_server.DodgeRollPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ElysiumPayloads {
    private static final String VERSION = "0.1.0";

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Elysium.MOD_ID).versioned(VERSION);

        registrar.playToClient(ScreenFlashPayload.TYPE, ScreenFlashPayload.CODEC, ScreenFlashPayload::handleDataOnClient);
        registrar.playToClient(DodgeRollAnimationPayload.TYPE, DodgeRollAnimationPayload.CODEC, DodgeRollAnimationPayload::handleDataOnClient);
        registrar.playToClient(SyncDataAttachmentPayload.TYPE, SyncDataAttachmentPayload.CODEC, SyncDataAttachmentPayload::handleDataOnClient);

        registrar.playToServer(DodgeRollPayload.TYPE, DodgeRollPayload.CODEC, DodgeRollPayload::handleDataOnServer);
    }

    private static final Map<Supplier<AttachmentType<?>>, Codec<?>> ATTACHMENT_CODECS = new HashMap<>();

    private static final Supplier<Map<ResourceLocation, Codec<?>>> CACHED_ATTACHMENT_CODECS = Lazy.of(() -> ATTACHMENT_CODECS
            .entrySet()
            .stream()
            .map(entry ->
                    new AbstractMap.SimpleEntry<>(
                            NeoForgeRegistries.ATTACHMENT_TYPES.getKey(entry.getKey().get()),
                            entry.getValue()
                    ))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

    public static <T> Supplier<AttachmentType<T>> registerAttachmentCodec(Supplier<AttachmentType<T>> type, Codec<T> codec) {
        ATTACHMENT_CODECS.put(type::get, codec);
        return type;
    }

    public static <T> Codec<T> getAttachmentCodec(AttachmentType<T> type) {
        //noinspection unchecked
        return (Codec<T>) CACHED_ATTACHMENT_CODECS.get().get(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(type));
    }
}
