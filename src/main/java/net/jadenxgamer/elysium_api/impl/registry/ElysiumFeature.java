package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.core.worldgen.feature.StructureStamp;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ElysiumFeature {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Elysium.MOD_ID);

    public static final Supplier<Feature<StructureStamp.StructureStampConfiguration>> STRUCTURE_STAMP = FEATURES.register("structure_stamp", () ->
            new StructureStamp(StructureStamp.StructureStampConfiguration.CODEC));

    public static void init(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}