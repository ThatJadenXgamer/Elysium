package net.jadenxgamer.elysium_api.impl.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Set;

public interface ElysiumBiomeSource {

    void addPossibleBiomes(Set<Holder<Biome>> biome);

    void setDimension(ResourceKey<LevelStem> dimension);

    ResourceKey<LevelStem> getDimension();
}
