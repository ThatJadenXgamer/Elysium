package net.jadenxgamer.elysium_api.impl.registry;

import com.mojang.serialization.Codec;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.api.util.Cooldown;
import net.jadenxgamer.elysium_api.impl.networking.ElysiumPayloads;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ElysiumAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Elysium.MOD_ID);

    public static final Supplier<AttachmentType<Integer>> COOLDOWN_TICK = ElysiumPayloads.registerAttachmentCodec(ATTACHMENT_TYPES.register(
            "cooldown_tick", () -> AttachmentType.builder(() -> 0).build()
    ), Codec.INT);

    public static final Supplier<AttachmentType<Cooldown>> DODGE_COOLDOWN = ATTACHMENT_TYPES.register(
            "dodge_cooldown", () -> AttachmentType
                    .builder(holder -> Cooldown.forHolder(holder, 10))
                    .build()
    );

    public static void init(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

}
