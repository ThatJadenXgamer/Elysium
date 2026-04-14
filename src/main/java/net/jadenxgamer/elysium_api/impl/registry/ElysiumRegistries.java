package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.core.datadriven.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.use_behaviors.UseBehavior;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.BlockSoundTransformer;
import net.jadenxgamer.elysium_api.impl.core.datadriven.brewing_recipe.ElysiumBrewingRecipe;
import net.jadenxgamer.elysium_api.impl.core.datadriven.item.RemainderTransformer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class ElysiumRegistries {
    public static final ResourceKey<Registry<UseBehavior>> USE_BEHAVIORS = key("block/use_behaviors");
    public static final ResourceKey<Registry<BlockSoundTransformer>> BLOCK_SOUND_TRANSFORMERS = key("block/sound_transformers");
    public static final ResourceKey<Registry<BiomeReplacerDataDriven>> BIOME_REPLACER = key("biome_replacer");
    public static final ResourceKey<Registry<RemainderTransformer>> REMAINDER_TRANSFORMERS = key("item/remainder_transformers");
    public static final ResourceKey<Registry<ElysiumBrewingRecipe>> BREWING_RECIPES = key("brewing_recipes");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(Elysium.id(name));
    }

    public static void datapackInit(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ElysiumRegistries.USE_BEHAVIORS, UseBehavior.CODEC);
        event.dataPackRegistry(ElysiumRegistries.BLOCK_SOUND_TRANSFORMERS, BlockSoundTransformer.CODEC, BlockSoundTransformer.CODEC);
        event.dataPackRegistry(ElysiumRegistries.BIOME_REPLACER, BiomeReplacerDataDriven.CODEC);
        event.dataPackRegistry(ElysiumRegistries.REMAINDER_TRANSFORMERS, RemainderTransformer.CODEC);
        event.dataPackRegistry(ElysiumRegistries.BREWING_RECIPES, ElysiumBrewingRecipe.CODEC, ElysiumBrewingRecipe.CODEC);
    }
}