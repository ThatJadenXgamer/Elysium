package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.impl.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.properties_transformer.BlockProperties;
import net.jadenxgamer.elysium_api.impl.sound_transformer.SoundTransformer;
import net.jadenxgamer.elysium_api.impl.use_behavior.UseBehavior;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistryEvent;

public class ElysiumRegistries {
    public static final ResourceKey<Registry<BlockProperties>> BLOCK_PROPERTIES_TRANSFORMER = key("block/properties_transformer");
    public static final ResourceKey<Registry<SoundTransformer>> BLOCK_SOUND_TRANSFORMER = key("block/sound_transformers");
    public static final ResourceKey<Registry<UseBehavior>> BLOCK_USE_BEHAVIORS = key("block/use_behaviors");
    public static final ResourceKey<Registry<BiomeReplacerDataDriven>> BIOME_REPLACER = key("biome_replacer");

    public static void datapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BLOCK_PROPERTIES_TRANSFORMER, BlockProperties.CODEC);
        event.dataPackRegistry(BLOCK_SOUND_TRANSFORMER, SoundTransformer.CODEC);
        event.dataPackRegistry(BLOCK_USE_BEHAVIORS, UseBehavior.CODEC);
        event.dataPackRegistry(BIOME_REPLACER, BiomeReplacerDataDriven.CODEC);
    }

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation("elysium", name));
    }
}
