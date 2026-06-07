package net.jadenxgamer.elysium_api.impl.registry;

import com.mojang.serialization.MapCodec;
import net.jadenxgamer.elysium_api.ElysiumAPI;
import net.jadenxgamer.elysium_api.impl.core.biome.MosaicBiomeSource;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.BlockSoundTransformer;
import net.jadenxgamer.elysium_api.impl.core.datadriven.block.use_behaviors.UseBehavior;
import net.jadenxgamer.elysium_api.impl.core.datadriven.brewing_recipe.ElysiumBrewingRecipe;
import net.jadenxgamer.elysium_api.impl.core.datadriven.item.RemainderTransformer;
import net.jadenxgamer.elysium_api.impl.core.datadriven.mosaic.MosaicBiomeEntry;
import net.jadenxgamer.elysium_api.impl.core.misc.neoforge.EffectsBiomeModifier;
import net.jadenxgamer.elysium_api.impl.core.worldgen.feature.StructureStamp;
import net.jadenxgamer.elysium_api.impl.core.worldgen.structure.MultilayerJigsawStructure;
import net.jadenxgamer.elysium_api.impl.core.worldgen.structure.processor.ProtectNonReplaceableProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.function.Supplier;

public class ElysiumRegistries {

    /**
     * NeoForge/Vanilla Deferred Registries
     */

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES = DeferredRegister.create(BuiltInRegistries.BIOME_SOURCE, ElysiumAPI.MOD_ID);
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ElysiumAPI.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, ElysiumAPI.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, ElysiumAPI.MOD_ID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, ElysiumAPI.MOD_ID);

    public static final Supplier<MapCodec<MosaicBiomeSource>> MOSAIC = BIOME_SOURCES.register("mosaic", () -> MosaicBiomeSource.CODEC);
    public static final Supplier<MapCodec<EffectsBiomeModifier>> EFFECTS_MODIFIER = BIOME_MODIFIERS.register("effects_modifier", () -> EffectsBiomeModifier.CODEC);
    public static final Supplier<Feature<StructureStamp.Config>> STRUCTURE_STAMP = FEATURES.register("structure_stamp", () -> new StructureStamp(StructureStamp.Config.CODEC));
    public static final Supplier<StructureType<MultilayerJigsawStructure>> MULTILAYERED_JIGSAW = STRUCTURE_TYPE.register("multilayered_jigsaw", () -> () -> MultilayerJigsawStructure.CODEC);
    public static final Supplier<StructureProcessorType<ProtectNonReplaceableProcessor>> PROTECT_NON_REPLACEABLE = STRUCTURE_PROCESSOR.register("protect_non_replaceable", () -> () -> ProtectNonReplaceableProcessor.CODEC);

    /**
     * Elysium Registries (currently nothing)
     */

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(ElysiumAPI.elysiumPath(name));
    }

    public static void init(IEventBus eventBus) {
        BIOME_SOURCES.register(eventBus);
        BIOME_MODIFIERS.register(eventBus);
        FEATURES.register(eventBus);
        STRUCTURE_TYPE.register(eventBus);
        STRUCTURE_PROCESSOR.register(eventBus);
    }

    public static void registryInit(NewRegistryEvent event) {
        // currently nothing
    }

    public static void datapackInit(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Keys.USE_BEHAVIORS, UseBehavior.CODEC);
        event.dataPackRegistry(Keys.BLOCK_SOUND_TRANSFORMERS, BlockSoundTransformer.CODEC, BlockSoundTransformer.CODEC);
        event.dataPackRegistry(Keys.REMAINDER_TRANSFORMERS, RemainderTransformer.CODEC);
        event.dataPackRegistry(Keys.BREWING_RECIPES, ElysiumBrewingRecipe.CODEC, ElysiumBrewingRecipe.CODEC);
        event.dataPackRegistry(Keys.MOSAIC_BIOME_ENTRY, MosaicBiomeEntry.CODEC);
    }

    public static final class Keys {
        // Elysium Registries (currently nothing)

        // Data-Driven Registries
        public static final ResourceKey<Registry<UseBehavior>> USE_BEHAVIORS = key("block/use_behaviors");
        public static final ResourceKey<Registry<BlockSoundTransformer>> BLOCK_SOUND_TRANSFORMERS = key("block/sound_transformers");
        public static final ResourceKey<Registry<RemainderTransformer>> REMAINDER_TRANSFORMERS = key("item/remainder_transformers");
        public static final ResourceKey<Registry<ElysiumBrewingRecipe>> BREWING_RECIPES = key("brewing_recipes");
        public static final ResourceKey<Registry<MosaicBiomeEntry>> MOSAIC_BIOME_ENTRY = key("mosaic_biome_entry");
    }
}