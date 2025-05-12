package net.jadenxgamer.elysium_api.impl.surface_rules;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public interface ElysiumNoiseGeneratorSettings {

    void initElysiumSurfaceRules(ResourceKey<LevelStem> dimension);

    ResourceKey<LevelStem> getDimension();
}
