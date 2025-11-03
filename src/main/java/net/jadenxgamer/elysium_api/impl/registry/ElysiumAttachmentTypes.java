package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.Elysium;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ElysiumAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Elysium.MOD_ID);

    public static final Supplier<AttachmentType<Integer>> INVULNERABILITY = ATTACHMENT_TYPES.register(
            "invulnerability", () -> AttachmentType
                    .builder(() -> 0)
                    .build()
    );

}
