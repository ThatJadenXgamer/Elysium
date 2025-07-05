package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.core.datadriven.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.core.datadriven.use_behaviors.UseBehavior;
import net.jadenxgamer.elysium_api.impl.core.datadriven.sound_transformers.SoundTransformer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ElysiumRegistries {
    public static final ResourceKey<Registry<UseBehavior>> BLOCK_USE_BEHAVIORS = key("block/use_behaviors");
    public static final ResourceKey<Registry<SoundTransformer>> BLOCK_SOUND_TRANSFORMERS = key("block/sound_transformers");
    public static final ResourceKey<Registry<BiomeReplacerDataDriven>> BIOME_REPLACER = key("biome_replacer");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(Elysium.id(name));
    }
}