package net.jadenxgamer.elysium_api.impl.registry;

import net.jadenxgamer.elysium_api.impl.features.StructureStamp;
import net.jadenxgamer.elysium_api.impl.features.configs.StructureStampConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ElysiumFeature {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, "elysium");

    public static final RegistryObject<Feature<StructureStampConfiguration>> STRUCTURE_STAMP = FEATURES.register("structure_stamp", () ->
            new StructureStamp(StructureStampConfiguration.CODEC));

    public static void init(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
