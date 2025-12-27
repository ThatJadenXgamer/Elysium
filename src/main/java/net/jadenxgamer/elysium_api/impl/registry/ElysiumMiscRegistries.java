package net.jadenxgamer.elysium_api.impl.registry;

import com.mojang.serialization.MapCodec;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.core.misc.neoforge.EffectsBiomeModifier;
import net.jadenxgamer.elysium_api.impl.core.worldgen.feature.StructureStamp;
import net.jadenxgamer.elysium_api.impl.core.worldgen.structure.MultilayerJigsawStructure;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ElysiumMiscRegistries {

    /**
     * NeoForge Deferred Registers
     */

    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Elysium.MOD_ID);

    public static final Supplier<MapCodec<EffectsBiomeModifier>> EFFECTS_MODIFIER = BIOME_MODIFIERS.register("effects_modifier", () -> EffectsBiomeModifier.CODEC);

    /**
     * Vanilla Deferred Registers
     */

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Elysium.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPE = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Elysium.MOD_ID);

    public static final Supplier<Feature<StructureStamp.StructureStampConfiguration>> STRUCTURE_STAMP = FEATURES.register("structure_stamp", () ->
            new StructureStamp(StructureStamp.StructureStampConfiguration.CODEC));

    public static final Supplier<StructureType<MultilayerJigsawStructure>> MULTILAYERED_JIGSAW = STRUCTURE_TYPE.register("multilayered_jigsaw", () ->
            () -> MultilayerJigsawStructure.CODEC);

    public static void init(IEventBus eventBus) {
        BIOME_MODIFIERS.register(eventBus);
        FEATURES.register(eventBus);
        STRUCTURE_TYPE.register(eventBus);
    }
}